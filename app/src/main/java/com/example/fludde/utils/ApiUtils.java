package com.example.fludde.utils;

import android.net.Uri;
import android.util.Log;
import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class ApiUtils {

    private static final String TAG = "ApiUtils";

    public interface Callback {
        void onSuccess(String body);
        void onError(IOException e);
    }

    private static final OkHttpClient client = new OkHttpClient();

    private static @Nullable String apiKey = null;       // v3
    private static @Nullable String bearerToken = null;   // v4

    private ApiUtils() { }

    /** Prefer v4 bearer token; pass null to clear. */
    public static void setBearer(@Nullable String token) {
        bearerToken = token;
    }

    /** Legacy v3 api_key; pass null to clear. */
    public static void setApiKey(@Nullable String key) {
        apiKey = key;
    }

    /**
     * Handle API request failures by logging the status code and error details.
     * This method is called from various fragments when API requests fail.
     *
     * @param statusCode HTTP status code from the failed request
     * @param throwable The exception/error that occurred
     */
    public static void handleFailure(int statusCode, Throwable throwable) {
        if (throwable != null) {
            Log.e(TAG, "API request failed with status code: " + statusCode, throwable);
            Log.e(TAG, "Error message: " + throwable.getMessage());
        } else {
            Log.e(TAG, "API request failed with status code: " + statusCode + " (no throwable provided)");
        }
    }

    public static void get(String url, final Callback cb) {
        try {
            Request.Builder rb = new Request.Builder();

            // If no bearer, append v3 api_key as query parameter (legacy fallback).
            if (bearerToken == null || bearerToken.isEmpty()) {
                if (apiKey != null && !apiKey.isEmpty()) {
                    Uri uri = Uri.parse(url).buildUpon()
                            .appendQueryParameter("api_key", apiKey)
                            .build();
                    url = uri.toString();
                }
            }

            rb.url(url);

            // If bearer available, use Authorization header (preferred).
            if (bearerToken != null && !bearerToken.isEmpty()) {
                rb.addHeader("Authorization", "Bearer " + bearerToken);
            }

            Request request = rb.build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    if (cb != null) cb.onError(e);
                }

                @Override public void onResponse(Call call, Response response) throws IOException {
                    try (Response res = response) {
                        if (!res.isSuccessful()) {
                            if (cb != null) cb.onError(new IOException("HTTP " + res.code() + " " + res.message()));
                            return;
                        }
                        String body = res.body() != null ? res.body().string() : "";
                        if (cb != null) cb.onSuccess(body);
                    }
                }
            });

        } catch (Exception e) {
            if (cb != null) cb.onError(new IOException("Request build error", e));
        }
    }
}