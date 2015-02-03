package com.auth0.sharelock.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.auth0.sharelock.R;
import com.auth0.sharelock.Secret;
import com.auth0.sharelock.event.NewSecretEvent;

import de.greenrobot.event.EventBus;

public class SecretInputFragment extends Fragment {

    EventBus bus;

    EditText secretField;
    Button nextButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus = EventBus.getDefault();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_secret_input, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        secretField = (EditText) view.findViewById(R.id.secret_input);
        nextButton = (Button) view.findViewById(R.id.next_button);
        secretField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                nextButton.setVisibility(s.length() > 0 ? View.VISIBLE : View.INVISIBLE);
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Secret secret = new Secret(secretField.getText().toString());
                bus.post(new NewSecretEvent(secret));
            }
        });
    }
}
