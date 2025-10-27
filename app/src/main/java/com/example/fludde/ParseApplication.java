package com.example.fludde;

import android.app.Application;
import android.util.Log;

import com.example.fludde.utils.ApiUtils;
import com.parse.Parse;

public class ParseApplication extends Application {

    private static final String TAG = "ParseApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        // Prefer v4 Bearer token; fall back to legacy v3 api_key only if needed.
        if (BuildConfig.TMDB_BEARER != null && !BuildConfig.TMDB_BEARER.isEmpty()) {
            ApiUtils.setApiKey(null);
            ApiUtils.setBearer(BuildConfig.TMDB_BEARER);
            Log.d(TAG, "TMDB auth configured with Bearer token.");
        } else if (BuildConfig.TMDB_API_KEY != null && !BuildConfig.TMDB_API_KEY.isEmpty()) {
            ApiUtils.setBearer(null);
            ApiUtils.setApiKey(BuildConfig.TMDB_API_KEY);
            Log.w(TAG, "Using legacy TMDB api_key. Consider switching to a v4 Bearer token.");
        } else {
            Log.e(TAG, "No TMDB credentials found. Set TMDB_BEARER (preferred) or TMDB_API_KEY.");
        }

        // --- Parse/Back4App initialization ---
        // Only initialize if credentials are provided in local.properties
        if (BuildConfig.BACK4APP_APP_ID != null && !BuildConfig.BACK4APP_APP_ID.isEmpty() &&
            BuildConfig.BACK4APP_CLIENT_KEY != null && !BuildConfig.BACK4APP_CLIENT_KEY.isEmpty()) {
            
            Parse.initialize(new Parse.Configuration.Builder(this)
                    .applicationId(BuildConfig.BACK4APP_APP_ID)
                    .clientKey(BuildConfig.BACK4APP_CLIENT_KEY)
                    .server(BuildConfig.BACK4APP_SERVER_URL)
                    .build()
            );
            Log.d(TAG, "Parse/Back4App initialized successfully.");
        } else {
            Log.w(TAG, "Parse/Back4App credentials not found. Parse functionality will be disabled.");
            Log.w(TAG, "Add BACK4APP_APP_ID, BACK4APP_CLIENT_KEY, and BACK4APP_SERVER_URL to local.properties");
        }
    }
}