package com.auth0.sharelock.event;

import com.auth0.sharelock.Secret;

public class RequestLinkEvent {

    private final Secret secret;

    public RequestLinkEvent(Secret secret) {
        this.secret = secret;
    }

    public Secret getSecret() {
        return secret;
    }
}
