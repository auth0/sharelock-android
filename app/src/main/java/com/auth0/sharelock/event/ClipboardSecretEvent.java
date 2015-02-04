package com.auth0.sharelock.event;

public class ClipboardSecretEvent {

    private final String clipboardContent;

    public ClipboardSecretEvent(String clipboardContent) {
        this.clipboardContent = clipboardContent;
    }

    public String getClipboardContent() {
        return clipboardContent;
    }
}
