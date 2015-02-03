package com.auth0.sharelock;

public class Secret {

    private final String secret;

    public Secret(String secret) {
        this.secret = secret;
    }

    public String getSecret() {
        return secret;
    }
}
