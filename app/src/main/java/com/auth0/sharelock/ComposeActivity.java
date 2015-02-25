package com.auth0.sharelock;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.auth0.sharelock.event.AllowedViewersModifiedEvent;
import com.auth0.sharelock.event.ClipboardSecretEvent;
import com.auth0.sharelock.event.NewLinkEvent;
import com.auth0.sharelock.event.NewSecretEvent;
import com.auth0.sharelock.event.RequestLinkEvent;
import com.auth0.sharelock.event.RequestNewSecretEvent;
import com.auth0.sharelock.event.SharelockAPIErrorEvent;
import com.auth0.sharelock.fragment.LinkFragment;
import com.auth0.sharelock.fragment.SecretInputFragment;
import com.auth0.sharelock.fragment.ShareFragment;
import com.crashlytics.android.Crashlytics;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;

import de.greenrobot.event.EventBus;
import io.fabric.sdk.android.Fabric;


public class ComposeActivity extends BaseMenuActivity {

    private static final String TAG = ComposeActivity.class.getName();
    private static final String COMPOSE_CREATED_SECRET = "compose-created-secret";
    private static final int DELAY_MILLIS = 1000;

    EventBus bus;
    Secret secret;
    LinkAPIClient client;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_compose);

        Toolbar toolbar = (Toolbar) findViewById(R.id.sharelock_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        bus = EventBus.getDefault();
        handler = new Handler();

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        String sharedText = null;
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            }
        }

        if (savedInstanceState == null) {
            final SecretInputFragment fragment = new SecretInputFragment();
            if (sharedText != null) {
                Bundle arguments = new Bundle();
                arguments.putString(SecretInputFragment.SECRET_INPUT_FRAGMENT_SECRET_ARGUMENT, sharedText);
                fragment.setArguments(arguments);
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.sharelock_compose_container, fragment)
                    .commit();
        } else {
            secret = savedInstanceState.getParcelable(COMPOSE_CREATED_SECRET);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (secret != null) {
            outState.putParcelable(COMPOSE_CREATED_SECRET, secret);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final int entryCount = getSupportFragmentManager().getBackStackEntryCount();
        final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (entryCount == 0 && clipboardManager.hasPrimaryClip() && clipboardManager.getPrimaryClip().getItemCount() > 0) {
            final ClipData primaryClip = clipboardManager.getPrimaryClip();
            final ClipData.Item item = primaryClip.getItemAt(0);
            final String text = item.coerceToText(this).toString();
            if (text.trim().length() > 0) {
                final Snackbar snackbar = Snackbar
                        .with(this)
                        .text(getString(R.string.paste_from_clipboard_prompt))
                        .actionLabel(getString(R.string.paste_clipboard_action))
                        .actionColorResource(R.color.sharelock_orange)
                        .actionListener(new ActionClickListener() {
                            @Override
                            public void onActionClicked(Snackbar snackbar) {
                                bus.postSticky(new ClipboardSecretEvent(text));
                                clipboardManager.setPrimaryClip(ClipData.newPlainText("", ""));
                            }
                        })
                        .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE);
                SnackbarManager.show(snackbar);
            }
        }
    }

    @Override
    protected int getMenuLayout() {
        return R.menu.menu_compose;
    }

    public void onEvent(NewSecretEvent event) {
        secret = event.getSecret();
        final ShareFragment fragment = new ShareFragment();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.sharelock_compose_container, fragment)
                .addToBackStack("Share Step")
                .commit();
    }

    public void onEvent(AllowedViewersModifiedEvent event) {
        secret.replaceAllowedViewers(event.getViewers());
        final LinkFragment fragment = new LinkFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(LinkFragment.LINK_FRAGMENT_SECRET_ARGUMENT, secret);
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.sharelock_compose_container, fragment)
                .addToBackStack("Link Step")
                .commit();
        bus.post(new RequestLinkEvent(secret));
    }

    public void onEvent(RequestLinkEvent event) {
        final Secret secret = event.getSecret();
        final EventBus bus = this.bus;
        SharedPreferences preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        client = new LinkAPIClient(preferences.getString(LinkAPIClient.SHARELOCK_ENDPOINT_KEY, LinkAPIClient.DEFAULT_URL));
        client.generateLinkForSecret(secret, this, new LinkAPIClient.LinkCallback() {
            @Override
            public void onSuccess(final Uri link) {
                Log.d(TAG, "Obtained link path " + link);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bus.postSticky(new NewLinkEvent(link));
                        final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        final ClipData clipData = ClipData.newRawUri("sharelocked-link", link);
                        clipboardManager.setPrimaryClip(clipData);
                        Snackbar snackbar = Snackbar.with(ComposeActivity.this)
                                .text(R.string.link_in_clipboard_message)
                                .duration(Snackbar.SnackbarDuration.LENGTH_SHORT);
                        SnackbarManager.show(snackbar);
                    }
                }, DELAY_MILLIS);
            }

            @Override
            public void onError(Throwable reason) {
                Log.e(TAG, "Failed to generate link", reason);
                bus.post(new SharelockAPIErrorEvent());
                AlertDialog dialog = new AlertDialog.Builder(ComposeActivity.this)
                        .setTitle(R.string.link_generation_failed_title)
                        .setMessage(R.string.link_generation_failed)
                        .setCancelable(true)
                        .setPositiveButton(R.string.retry_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                bus.post(new RequestLinkEvent(secret));
                            }
                        })
                        .setNegativeButton(R.string.cancel_button, null)
                        .create();
                dialog.show();
            }
        });
    }

    public void onEvent(RequestNewSecretEvent event) {
        Intent intent = new Intent(this, ComposeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            secret = null;
        }
    }
}
