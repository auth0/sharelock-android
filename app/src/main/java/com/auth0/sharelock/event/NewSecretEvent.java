package com.auth0.sharelock.event;

import com.auth0.sharelock.Secret;

public class NewSecretEvent {
    private final Secret secret;

    public NewSecretEvent(Secret secret) {
        this.secret = secret;
    }

    public Secret getSecret() {
        return secret;
    }
}
