package com.example.fludde.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages mock authentication sessions using SharedPreferences.
 * Only used when BuildConfig.MOCK_MODE is true.
 */
public final class MockSessionManager {
    
    private static final String PREFS_NAME = "FluddeMock";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "currentUser";
    
    private MockSessionManager() {
        // Prevent instantiation
    }
    
    /**
     * Check if user is logged in (mock mode).
     */
    public static boolean isLoggedIn(Context context) {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    /**
     * Save login session for mock user.
     */
    public static void login(Context context, String username) {
        getPrefs(context).edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USERNAME, username)
            .apply();
    }
    
    /**
     * Get current logged in username.
     */
    public static String getCurrentUsername(Context context) {
        return getPrefs(context).getString(KEY_USERNAME, "");
    }
    
    /**
     * Logout user.
     */
    public static void logout(Context context) {
        getPrefs(context).edit().clear().apply();
    }
    
    /**
     * Get current user's full profile.
     */
    public static MockData.MockProfile getCurrentUserProfile(Context context) {
        String username = getCurrentUsername(context);
        if (username.isEmpty()) {
            return null;
        }
        return MockData.mockUserProfile(username);
    }
    
    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}