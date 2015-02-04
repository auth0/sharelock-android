package com.auth0.sharelock.event;

import android.net.Uri;

public class NewLinkEvent {

    private final Uri link;

    public NewLinkEvent(Uri link) {
        this.link = link;
    }

    public Uri getLink() {
        return link;
    }
}
