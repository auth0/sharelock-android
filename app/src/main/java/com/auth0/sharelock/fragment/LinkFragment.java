package com.auth0.sharelock.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.auth0.sharelock.R;
import com.auth0.sharelock.Secret;
import com.auth0.sharelock.event.NewLinkEvent;
import com.auth0.sharelock.event.RequestLinkEvent;
import com.auth0.sharelock.event.SharelockAPIErrorEvent;
import com.auth0.sharelock.widget.ShareEditText;

import de.greenrobot.event.EventBus;

public class LinkFragment extends Fragment {

    public static final String LINK_FRAGMENT_SECRET_ARGUMENT = "LINK_FRAGMENT_SECRET_ARGUMENT";

    Secret secret;
    EventBus bus;

    TextView linkText;
    ProgressBar progressBar;
    Button retryButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        if (arguments != null) {
            secret = arguments.getParcelable(LINK_FRAGMENT_SECRET_ARGUMENT);
        }
        bus = EventBus.getDefault();
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        bus.unregister(this);
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
        linkText = (TextView) view.findViewById(R.id.link_text);
        progressBar = (ProgressBar) view.findViewById(R.id.link_progress);
        retryButton = (Button) view.findViewById(R.id.link_retry_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bus.post(new RequestLinkEvent(secret));
                retryButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    public void onEvent(NewLinkEvent event) {
        bus.removeStickyEvent(event);
        progressBar.setVisibility(View.GONE);
        linkText.setText(event.getLink().toString());
    }

    public void onEventMainThread(SharelockAPIErrorEvent event) {
        linkText.setText(R.string.link_generation_failed_message);
        progressBar.setVisibility(View.GONE);
        retryButton.setVisibility(View.VISIBLE);
    }
}
