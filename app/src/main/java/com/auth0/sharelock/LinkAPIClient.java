package com.auth0.sharelock;

import android.content.Context;
import android.net.Uri;

import com.auth0.api.JsonEntityBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.util.HashMap;
import java.util.Map;

public class LinkAPIClient {

    private final AsyncHttpClient client;
    private final JsonEntityBuilder entityBuilder;

    public LinkAPIClient() {
        this.client = new AsyncHttpClient();
        this.entityBuilder = new JsonEntityBuilder(new ObjectMapper());
    }

    public void generateLinkForSecret(Secret secret, Context context, final LinkCallback callback) {
        final String generateLinkURL = "https://sharelock.io/create";
        Map<String, Object> params = new HashMap<>();
        params.put("d", secret.getSecret());
        StringBuilder acl = new StringBuilder();
        for (String viewer: secret.getAllowedViewers()) {
            acl.append(viewer);
            acl.append(',');
        }
        acl.deleteCharAt(acl.length() - 1);
        params.put("a", acl.toString());
        final HttpEntity entity = entityBuilder.newEntityFrom(params);
        this.client.post(context, generateLinkURL, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Uri linkUri = new Uri.Builder()
                        .scheme("https")
                        .authority("sharelock.io")
                        .appendEncodedPath(new String(responseBody))
                        .build();
                callback.onSuccess(linkUri);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                callback.onError(error);
            }
        });
    }

    public static interface LinkCallback {
        void onSuccess(Uri link);

        void onError(Throwable reason);
    }
}
