package com.auth0.sharelock;

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
import com.auth0.sharelock.event.NewLinkEvent;
import com.auth0.sharelock.event.NewSecretEvent;
import com.auth0.sharelock.event.RequestLinkEvent;
import com.auth0.sharelock.fragment.LinkFragment;
import com.auth0.sharelock.fragment.SecretInputFragment;
import com.auth0.sharelock.fragment.ShareFragment;

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
        client.generateLinkForSecret(event.getSecret(), this, new LinkAPIClient.LinkCallback() {
            @Override
            public void onSuccess(Uri link) {
                Log.d(TAG, "Obtained link path " + link);
                bus.postSticky(new NewLinkEvent(link));
            }

            @Override
            public void onError(Throwable reason) {
                Log.e(TAG, "Failed to generate link", reason);
            }
        });
    }
}
