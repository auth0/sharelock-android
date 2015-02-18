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

    public static final String SHARELOCK_ENDPOINT_KEY = "SharelockEndpoint";
    public static final String DEFAULT_URL = "https://sharelock.io";
    private final AsyncHttpClient client;
    private final JsonEntityBuilder entityBuilder;
    private final Uri baseUri;

    public LinkAPIClient(String baseUrl) {
        this.client = new AsyncHttpClient();
        this.entityBuilder = new JsonEntityBuilder(new ObjectMapper());
        this.baseUri = Uri.parse(baseUrl);
    }

    public void generateLinkForSecret(Secret secret, Context context, final LinkCallback callback) {
        final Uri baseUri = this.baseUri;
        final String generateLinkURL = Uri.withAppendedPath(baseUri, "/create").toString();
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
                String pathSegment = new String(responseBody);
                if (pathSegment.startsWith("/")) {
                    pathSegment = pathSegment.substring(1);
                }
                Uri linkUri = Uri.withAppendedPath(baseUri, pathSegment);
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
