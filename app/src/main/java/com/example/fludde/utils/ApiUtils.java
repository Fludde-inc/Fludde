package com.example.fludde.utils;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * Centralized HTTP helper with timeouts, retries, and verbose logging.
 * All network calls flow through here so it's easy to see what's going on.
 */
public final class ApiUtils {
    private static final String TAG = "ApiUtils";
    private static final AsyncHttpClient client = new AsyncHttpClient();

    static {
        try {
            // Reasonable network defaults for flaky connections:
            client.setConnectTimeout(15_000);     // 15s to connect
            client.setResponseTimeout(25_000);    // 25s to read response
            client.setMaxRetriesAndTimeout(2, 2_000); // 2 retries with 2s backoff
            client.setUserAgent("Fludde/1.0 (Android)");
            Log.d(TAG, "AsyncHttpClient configured: timeouts + retries + UA");
        } catch (Throwable t) {
            Log.e(TAG, "Failed to configure AsyncHttpClient", t);
        }
    }

    private ApiUtils() {}

    /**
     * GET with verbose logging. Wraps the passed handler so we always log status + payload basics.
     */
    public static void get(String url, JsonHttpResponseHandler handler) {
        final long start = System.currentTimeMillis();
        final String reqTag = "GET " + url;
        Log.d(TAG, reqTag + " → sending");

        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, org.json.JSONObject response) {
                long ms = System.currentTimeMillis() - start;
                Log.d(TAG, reqTag + " ✓ " + statusCode + " (" + ms + "ms) body=JSONObject(len=" + (response != null ? response.length() : 0) + ")");
                if (handler != null) handler.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, org.json.JSONArray response) {
                long ms = System.currentTimeMillis() - start;
                Log.d(TAG, reqTag + " ✓ " + statusCode + " (" + ms + "ms) body=JSONArray(len=" + (response != null ? response.length() : 0) + ")");
                if (handler != null) handler.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
                long ms = System.currentTimeMillis() - start;
                String preview = responseString != null && responseString.length() > 200
                        ? responseString.substring(0, 200) + "…"
                        : responseString;
                Log.d(TAG, reqTag + " ✓ " + statusCode + " (" + ms + "ms) body=String(len=" + (responseString != null ? responseString.length() : 0) + ") preview=\"" + preview + "\"");
                if (handler != null) handler.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, org.json.JSONObject errorResponse) {
                logFailure(reqTag, statusCode, throwable, errorResponse != null ? errorResponse.toString() : null, start);
                if (handler != null) handler.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, org.json.JSONArray errorResponse) {
                logFailure(reqTag, statusCode, throwable, errorResponse != null ? errorResponse.toString() : null, start);
                if (handler != null) handler.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                logFailure(reqTag, statusCode, throwable, responseString, start);
                if (handler != null) handler.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    /**
     * POST with no params (convenience) + logging. Add overloads as needed.
     */
    public static void post(String url, JsonHttpResponseHandler handler) {
        final long start = System.currentTimeMillis();
        final String reqTag = "POST " + url;
        Log.d(TAG, reqTag + " → sending");

        client.post(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, org.json.JSONObject response) {
                long ms = System.currentTimeMillis() - start;
                Log.d(TAG, reqTag + " ✓ " + statusCode + " (" + ms + "ms)");
                if (handler != null) handler.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, org.json.JSONObject errorResponse) {
                logFailure(reqTag, statusCode, throwable, errorResponse != null ? errorResponse.toString() : null, start);
                if (handler != null) handler.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    /** Optional bearer header helper. */
    public static void setApiKey(String apiKey) {
        try {
            client.addHeader("Authorization", "Bearer " + apiKey);
            Log.d(TAG, "Authorization header set (Bearer …" + (apiKey != null ? Math.max(0, apiKey.length() - 4) : 0) + " chars)");
        } catch (Exception e) {
            Log.e(TAG, "Failed to set API key", e);
        }
    }

    /** Centralized failure log (also safe to call from your fragments). */
    public static void handleFailure(int statusCode, Throwable throwable) {
        Log.e(TAG, "Network request failed status=" + statusCode, throwable);
    }

    // ---- internal helpers ----
    private static void logFailure(String tag, int status, Throwable t, String bodyOrNull, long start) {
        long ms = System.currentTimeMillis() - start;
        if (bodyOrNull != null && bodyOrNull.length() > 500) {
            bodyOrNull = bodyOrNull.substring(0, 500) + "…";
        }
        Log.e(TAG, tag + " ✗ " + status + " (" + ms + "ms) body=" + (bodyOrNull == null ? "null" : bodyOrNull), t);
    }
}
