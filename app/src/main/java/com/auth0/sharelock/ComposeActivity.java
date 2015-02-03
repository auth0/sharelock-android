package com.auth0.sharelock;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.auth0.sharelock.fragment.SecretInputFragment;


public class ComposeActivity extends ActionBarActivity {

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
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.sharelock_compose_container, new SecretInputFragment())
                    .commit();
        }    }


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
}
