package com.auth0.sharelock.event;

import java.util.List;

public class AllowedViewersModifiedEvent {

    private final List<Object> viewers;

    public AllowedViewersModifiedEvent(List<Object> viewers) {
        this.viewers = viewers;
    }

    public List<Object> getViewers() {
        return viewers;
    }
}
