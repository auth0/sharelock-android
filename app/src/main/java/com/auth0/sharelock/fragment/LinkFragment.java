package com.auth0.sharelock.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
    Uri link;

    TextView linkText;
    ProgressBar progressBar;
    Button retryButton;
    ImageButton shareButton;

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
        shareEditText.setFocusable(false);
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
            }
        });
        shareButton = (ImageButton) view.findViewById(R.id.link_share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, link.toString());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getString(R.string.share_link_chooser_title)));
            }
        });
    }

    public void onEvent(NewLinkEvent event) {
        bus.removeStickyEvent(event);
        progressBar.setVisibility(View.GONE);
        shareButton.setVisibility(View.VISIBLE);
        link = event.getLink();
        linkText.setText(link.toString());
    }

    public void onEventMainThread(SharelockAPIErrorEvent event) {
        linkText.setText(R.string.link_generation_failed_message);
        progressBar.setVisibility(View.GONE);
        retryButton.setVisibility(View.VISIBLE);
        shareButton.setVisibility(View.GONE);
    }

    public void onEvent(RequestLinkEvent event) {
        retryButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        shareButton.setVisibility(View.GONE);
    }
}
