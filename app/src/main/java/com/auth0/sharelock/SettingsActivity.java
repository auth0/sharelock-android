package com.auth0.sharelock;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SettingsActivity extends BaseMenuActivity {

    private TextView endpointEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.sharelock_toolbar);
        setSupportActionBar(toolbar);
        setTitle(null);
        endpointEditText = (TextView) findViewById(R.id.settings_endpoint_url);
        final SharedPreferences preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        endpointEditText.setText(preferences.getString(LinkAPIClient.SHARELOCK_ENDPOINT_KEY, LinkAPIClient.DEFAULT_URL));
        Button resetButton = (Button) findViewById(R.id.settings_default_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endpointEditText.setText(LinkAPIClient.DEFAULT_URL);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        final String urlString = endpointEditText.getText().toString();
        if (Patterns.WEB_URL.matcher(urlString).matches()) {
            SharedPreferences preferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
            preferences.edit()
                    .putString(LinkAPIClient.SHARELOCK_ENDPOINT_KEY, urlString)
                    .apply();
        }
    }

    @Override
    protected int getMenuLayout() {
        return R.menu.menu_settings;
    }
}
