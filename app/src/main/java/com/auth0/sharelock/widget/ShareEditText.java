package com.auth0.sharelock.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.auth0.sharelock.R;
import com.tokenautocomplete.TokenCompleteTextView;

/**
 * Created by hernan on 2/3/15.
 */
public class ShareEditText extends TokenCompleteTextView {
    public ShareEditText(Context context) {
        super(context);
        setAdapter(new ArrayAdapter<String>(getContext(), -1));
    }

    public ShareEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAdapter(new ArrayAdapter<String>(getContext(), -1));
    }

    public ShareEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setAdapter(new ArrayAdapter<String>(getContext(), -1));
    }

    @Override
    protected View getViewForObject(Object o) {
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.view_share_token, (android.view.ViewGroup) getParent(), false);
        TextView tokenView = (TextView) view.findViewById(R.id.share_token_text);
        tokenView.setText(o.toString());
        return view;
    }

    @Override
    protected Object defaultObject(String s) {
        return s.trim();
    }
}
