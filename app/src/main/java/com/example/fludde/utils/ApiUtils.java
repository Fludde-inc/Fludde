package com.example.fludde.utils;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class ApiUtils {
    private static final String TAG = "ApiUtils";
    private static final AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, JsonHttpResponseHandler responseHandler) {
        try {
            client.get(url, responseHandler);
            Log.d(TAG, "GET request sent to URL: " + url);
        } catch (Exception e) {
            Log.e(TAG, "GET request failed for URL: " + url, e);
        }
    }

    public static void post(String url, JsonHttpResponseHandler responseHandler) {
        try {
            client.post(url, null, responseHandler);
            Log.d(TAG, "POST request sent to URL: " + url);
        } catch (Exception e) {
            Log.e(TAG, "POST request failed for URL: " + url, e);
        }
    }

    public static void setApiKey(String apiKey) {
        try {
            client.addHeader("Authorization", "Bearer " + apiKey);
            Log.d(TAG, "API key set for requests");
        } catch (Exception e) {
            Log.e(TAG, "Failed to set API key", e);
        }
    }

    public static void handleFailure(int statusCode, Throwable throwable) {
        Log.e(TAG, "Network request failed with status code: " + statusCode, throwable);
    }
}
