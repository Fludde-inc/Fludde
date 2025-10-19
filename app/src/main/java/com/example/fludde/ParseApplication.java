package com.example.fludde;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Parse bootstrapping with defensive URL validation + noisy logging so
 * misconfiguration shows clearly in Logcat.
 */
public class ParseApplication extends Application {

    private static final String TAG = "ParseApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        final String appId = getString(R.string.back4app_app_id);
        final String clientKey = getString(R.string.back4app_client_key);
        final String serverUrl = getString(R.string.back4app_server_url);

        // Validation: crash early with a clear message if server URL is bad.
        try {
            URL url = new URL(serverUrl);
            if (!("http".equals(url.getProtocol()) || "https".equals(url.getProtocol()))) {
                throw new MalformedURLException("Server URL must start with http or https");
            }
        } catch (MalformedURLException e) {
            throw new IllegalStateException(
                    "Invalid PARSE server URL: \"" + serverUrl + "\". " +
                            "It must be a full URL like https://parseapi.back4app.com/ .", e);
        }

        // Register subclasses BEFORE using typed queries.
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(User.class);

        // Make Parse noisy in Logcat (helpful during bring-up).
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        try {
            Parse.initialize(new Parse.Configuration.Builder(this)
                    .applicationId(appId)
                    .clientKey(clientKey)
                    .server(serverUrl)
                    .build());
            Log.i(TAG, "Parse.initialize ✓ server=" + serverUrl);

            boolean hasTmdbKey = !TextUtils.isEmpty(BuildConfig.TMDB_API_KEY);
            Log.i(TAG, "BuildConfig.TMDB_API_KEY present=" + hasTmdbKey +
                       (hasTmdbKey ? " (len=" + BuildConfig.TMDB_API_KEY.length() + ")" : ""));
        } catch (Throwable t) {
            Log.e(TAG, "Parse.initialize ✗", t);
            throw t instanceof RuntimeException ? (RuntimeException) t : new RuntimeException(t);
        }

        try {
            ParseInstallation.getCurrentInstallation().saveInBackground();
        } catch (Throwable t) {
            Log.w(TAG, "ParseInstallation save failed (non-fatal): " + t.getMessage());
        }
    }
}
