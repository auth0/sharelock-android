package com.auth0.sharelock.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.auth0.sharelock.R;
import com.auth0.sharelock.Secret;
import com.auth0.sharelock.event.AllowedViewersModifiedEvent;
import com.auth0.sharelock.widget.ShareEditText;
import com.tokenautocomplete.TokenCompleteTextView;

import de.greenrobot.event.EventBus;

public class ShareFragment extends Fragment {

    EventBus bus;
    ShareEditText shareField;
    Button nextButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus = EventBus.getDefault();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nextButton = (Button) view.findViewById(R.id.next_button);
        shareField = (ShareEditText) view.findViewById(R.id.share_input);
        shareField.setSplitChar(new char[] {' ', ','});
        shareField.setTokenListener(new TokenCompleteTextView.TokenListener() {
            @Override
            public void onTokenAdded(Object o) {
                nextButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTokenRemoved(Object o) {
                nextButton.setVisibility(shareField.getObjects().size() > 0 ? View.VISIBLE : View.INVISIBLE);
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bus.post(new AllowedViewersModifiedEvent(shareField.getObjects()));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.removeAllStickyEvents();
    }
}
