package com.auth0.sharelock;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Secret implements Parcelable {

    private final String secret;

    private List<String> allowedViewers;

    public Secret(String secret) {
        this.secret = secret;
        this.allowedViewers = new ArrayList<>();
    }

    public String getSecret() {
        return secret;
    }

    public List<String> getAllowedViewers() {
        return Collections.unmodifiableList(allowedViewers);
    }

    public void replaceAllowedViewers(List viewers) {
        allowedViewers.clear();
        if (viewers == null) {
            return;
        }
        for (Object viewer: viewers) {
            allowedViewers.add(viewer.toString());
        }
    }

    protected Secret(Parcel in) {
        secret = in.readString();
        if (in.readByte() == 0x01) {
            allowedViewers = new ArrayList<String>();
            in.readList(allowedViewers, String.class.getClassLoader());
        } else {
            allowedViewers = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(secret);
        if (allowedViewers == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(allowedViewers);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Secret> CREATOR = new Parcelable.Creator<Secret>() {
        @Override
        public Secret createFromParcel(Parcel in) {
            return new Secret(in);
        }

        @Override
        public Secret[] newArray(int size) {
            return new Secret[size];
        }
    };
}
