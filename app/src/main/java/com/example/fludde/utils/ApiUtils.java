package com.example.fludde.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.fludde.BuildConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Centralized HTTP helper with timeouts, retries, and verbose logging.
 * Logs request lifecycle with timings and body sizes so failures are obvious.
 */
public final class ApiUtils {
    private static final String TAG = "ApiUtils";
    private static final AsyncHttpClient client = new AsyncHttpClient();

    static {
        try {
            client.setConnectTimeout(15_000);
            client.setResponseTimeout(25_000);
            client.setMaxRetriesAndTimeout(2, 2_000);
            client.setUserAgent("Fludde/1.0 (Android)");
            Log.d(TAG, "init: timeouts(connect=15s resp=25s) retries=2 UA=Fludde/1.0 MOCK_MODE=" + BuildConfig.MOCK_MODE);
        } catch (Throwable t) {
            Log.e(TAG, "init: Failed to configure AsyncHttpClient", t);
        }
    }

    private ApiUtils() {}

    /** GET with verbose logging; in MOCK_MODE returns canned JSON for known URLs. */
    public static void get(String url, JsonHttpResponseHandler handler) {
        final long start = System.currentTimeMillis();
        final String reqTag = "GET " + url;

        if (BuildConfig.MOCK_MODE) {
            Log.d(TAG, reqTag + " -> MOCK intercept (no network)");
            mockGet(url, handler, start);
            return;
        }

        Log.d(TAG, reqTag + " -> sending");
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, org.json.JSONObject response) {
                long ms = System.currentTimeMillis() - start;
                Log.d(TAG, reqTag + " ✓ " + statusCode + " (" + ms + "ms) JSONObject len=" + (response != null ? response.length() : -1));
                if (handler != null) handler.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, org.json.JSONArray response) {
                long ms = System.currentTimeMillis() - start;
                Log.d(TAG, reqTag + " ✓ " + statusCode + " (" + ms + "ms) JSONArray len=" + (response != null ? response.length() : -1));
                if (handler != null) handler.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                long ms = System.currentTimeMillis() - start;
                int len = responseString == null ? -1 : responseString.length();
                String preview = responseString != null && len > 160 ? responseString.substring(0, 160) + "…" : responseString;
                Log.d(TAG, reqTag + " ✓ " + statusCode + " (" + ms + "ms) String len=" + len + " preview=" + preview);
                if (handler != null) handler.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, org.json.JSONObject errorResponse) {
                logFailure(reqTag, statusCode, throwable, errorResponse != null ? errorResponse.toString() : null, start);
                if (handler != null) handler.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, org.json.JSONArray errorResponse) {
                logFailure(reqTag, statusCode, throwable, errorResponse != null ? errorResponse.toString() : null, start);
                if (handler != null) handler.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logFailure(reqTag, statusCode, throwable, responseString, start);
                if (handler != null) handler.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    /** POST with no params (unchanged; not used by mocks for now). */
    public static void post(String url, JsonHttpResponseHandler handler) {
        final long start = System.currentTimeMillis();
        final String reqTag = "POST " + url;
        Log.d(TAG, reqTag + " -> sending");

        client.post(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, org.json.JSONObject response) {
                long ms = System.currentTimeMillis() - start;
                Log.d(TAG, reqTag + " ✓ " + statusCode + " (" + ms + "ms)");
                if (handler != null) handler.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, org.json.JSONObject errorResponse) {
                logFailure(reqTag, statusCode, throwable, errorResponse != null ? errorResponse.toString() : null, start);
                if (handler != null) handler.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    /** Optional bearer header helper. */
    public static void setApiKey(String apiKey) {
        try {
            client.addHeader("Authorization", "Bearer " + apiKey);
            Log.d(TAG, "Authorization header set (Bearer ... len=" + (apiKey == null ? 0 : apiKey.length()) + ")");
        } catch (Exception e) {
            Log.e(TAG, "Failed to set API key", e);
        }
    }

    /** Centralized failure log (consistent with Android logging guidance). */
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

    // ---- mock engine ----
    private static void mockGet(String url, JsonHttpResponseHandler handler, long start) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                JSONObject payload;

                if (url.contains("themoviedb.org/3/trending/movie")) {
                    payload = MockData.tmdbTrendingJson();
                } else if (url.contains("itunes.apple.com/search")) {
                    payload = MockData.itunesSearchJson();
                } else {
                    payload = new JSONObject();
                }

                long ms = System.currentTimeMillis() - start;
                Log.d(TAG, "MOCK " + url + " ✓ 200 (" + ms + "ms) body=JSONObject(len=" + payload.length() + ")");
                if (handler != null) handler.onSuccess(200, new Header[0], payload);
            } catch (Throwable t) {
                Log.e(TAG, "MOCK " + url + " ✗ error generating payload", t);
                if (handler != null) handler.onFailure(500, new Header[0], t, (JSONObject) null);
            }
        }, 450);
    }
}
