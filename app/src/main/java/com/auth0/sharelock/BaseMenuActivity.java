package com.auth0.sharelock;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public abstract class BaseMenuActivity extends ActionBarActivity {

    protected abstract int getMenuLayout();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(getMenuLayout(), menu);
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        if (id == R.id.action_new) {
            final Intent intent = new Intent(this, ComposeActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_privacy) {
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getString(R.string.privacy_url)));
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_feedback) {
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getString(R.string.feedback_url)));
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
