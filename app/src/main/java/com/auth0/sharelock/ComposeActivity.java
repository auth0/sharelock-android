package com.auth0.sharelock;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
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
import com.auth0.sharelock.event.SharelockAPIErrorEvent;
import com.auth0.sharelock.fragment.LinkFragment;
import com.auth0.sharelock.fragment.SecretInputFragment;
import com.auth0.sharelock.fragment.ShareFragment;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;

import de.greenrobot.event.EventBus;


public class ComposeActivity extends ActionBarActivity {

    public static final String TAG = ComposeActivity.class.getName();
    EventBus bus;
    Secret secret;
    LinkAPIClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        Toolbar toolbar = (Toolbar) findViewById(R.id.sharelock_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        TextView subtitle = (TextView) findViewById(R.id.sharelock_toolbar_subtitle);
        Typeface proximaLight = Typeface.createFromAsset(getAssets(), "fonts/ProximaNovaThin.otf");
        subtitle.setTypeface(proximaLight);
        TextView title = (TextView) findViewById(R.id.sharelock_toolbar_title);
        Typeface proximaRegular = Typeface.createFromAsset(getAssets(), "fonts/ProximaNovaRegular.otf");
        title.setTypeface(proximaRegular);

        bus = EventBus.getDefault();
        client = new LinkAPIClient();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.sharelock_compose_container, new SecretInputFragment())
                    .commit();
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
                        .text("Do you want to add this data to Sharelock?")
                        .actionLabel("Paste")
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        client.generateLinkForSecret(secret, this, new LinkAPIClient.LinkCallback() {
            @Override
            public void onSuccess(Uri link) {
                Log.d(TAG, "Obtained link path " + link);
                bus.postSticky(new NewLinkEvent(link));
            }

            @Override
            public void onError(Throwable reason) {
                Log.e(TAG, "Failed to generate link", reason);
                bus.post(new SharelockAPIErrorEvent());
                AlertDialog dialog = new AlertDialog.Builder(ComposeActivity.this)
                        .setTitle(R.string.link_generation_failed_title)
                        .setMessage(R.string.link_generation_failed_message)
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

    @Override
    protected void onPause() {
        super.onPause();
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            secret = null;
        }
    }
}
