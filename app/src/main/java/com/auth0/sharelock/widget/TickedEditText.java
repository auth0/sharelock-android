package com.auth0.sharelock.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.auth0.sharelock.R;

public class TickedEditText extends EditText {
    public TickedEditText(Context context) {
        super(context);
        watchForChanges();
    }

    public TickedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        watchForChanges();
    }

    public TickedEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        watchForChanges();
    }

    private void watchForChanges() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_secret_entered, 0);
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }
        });
    }
}
