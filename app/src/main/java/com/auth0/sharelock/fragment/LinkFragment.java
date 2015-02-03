package com.auth0.sharelock.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.auth0.sharelock.R;
import com.auth0.sharelock.Secret;
import com.auth0.sharelock.widget.ShareEditText;

public class LinkFragment extends Fragment {

    public static final String LINK_FRAGMENT_SECRET_ARGUMENT = "LINK_FRAGMENT_SECRET_ARGUMENT";

    Secret secret;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        if (arguments != null) {
            secret = arguments.getParcelable(LINK_FRAGMENT_SECRET_ARGUMENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_link, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView secretText = (TextView) view.findViewById(R.id.link_secret_text);
        secretText.setText(secret.getSecret());
        ShareEditText shareEditText = (ShareEditText) view.findViewById(R.id.link_share_list);
        shareEditText.setEnabled(false);
        for (String viewer: secret.getAllowedViewers()) {
            shareEditText.addObject(viewer);
        }
    }
}
