package com.auth0.sharelock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Secret {

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
}
