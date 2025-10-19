package com.example.fludde;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    private static final String TAG = "ParseApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        // Register Parse subclasses
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(User.class);

        // Read credentials injected by Gradle (see app/build.gradle)
        final String appId = BuildConfig.BACK4APP_APP_ID;
        final String clientKey = BuildConfig.BACK4APP_CLIENT_KEY;
        final String serverUrl = BuildConfig.BACK4APP_SERVER_URL;

        if (isValidParseConfig(appId, clientKey, serverUrl)) {
            try {
                Parse.initialize(new Parse.Configuration.Builder(this)
                        .applicationId(appId)
                        .clientKey(clientKey)
                        .server(serverUrl)
                        .build());
                Log.i(TAG, "Parse initialized.");
            } catch (Throwable t) {
                // Never hard-crash the whole app if a remote service misconfigures
                Log.e(TAG, "Parse initialization failed; continuing without Parse.", t);
            }
        } else {
            Log.w(TAG,
                    "Parse credentials not configured (BACK4APP_*). " +
                    "Skipping Parse.initialize to avoid startup crash.");
        }
    }

    private static boolean isValidParseConfig(String appId, String clientKey, String serverUrl) {
        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(clientKey) || TextUtils.isEmpty(serverUrl)) {
            return false;
        }
        // Minimal sanity check: Parse requires an http(s) endpoint
        return serverUrl.startsWith("http://") || serverUrl.startsWith("https://");
    }
}
