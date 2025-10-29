package com.example.fludde;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fludde.utils.ErrorHandler;
import com.example.fludde.utils.MockData;
import com.example.fludde.utils.MockSessionManager;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Login Activity with full Parse authentication and session management.
 * Updated to support Mock Mode authentication.
 * 
 * Changes:
 * - All hardcoded strings moved to strings.xml
 * - All Toast.makeText() replaced with ErrorHandler methods
 * - Consistent error handling
 * - Improved logging with proper tags
 * - Added mock mode authentication support
 * - Added session check on startup
 * 
 * Mock Credentials (when MOCK_MODE is true):
 * - Username: demo, Password: demo123
 * - Username: testuser, Password: test123
 * - Username: alice, Password: alice123
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 🔒 SESSION CHECK: If user is already logged in, skip login screen
        if (isUserLoggedIn()) {
            Log.d(TAG, "User already logged in, navigating to MainActivity");
            navigateToMain();
            return;
        }
        
        setContentView(R.layout.activity_login);

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);

        // Set up button click listeners
        btnLogin.setOnClickListener(v -> performLogin());
        btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    /**
     * 🔒 Check if user is already logged in.
     * Handles both mock mode and real Parse authentication.
     * 
     * @return true if user is logged in, false otherwise
     */
    private boolean isUserLoggedIn() {
        if (BuildConfig.MOCK_MODE) {
            // Check mock session
            boolean isLoggedIn = MockSessionManager.isLoggedIn(this);
            if (isLoggedIn) {
                Log.d(TAG, "Mock session found for user: " + MockSessionManager.getCurrentUsername(this));
            }
            return isLoggedIn;
        } else {
            // Check Parse session
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null) {
                Log.d(TAG, "Parse session found for user: " + currentUser.getUsername());
                return true;
            }
            return false;
        }
    }

    /**
     * Performs login authentication.
     * Routes to mock authentication if MOCK_MODE is enabled,
     * otherwise uses real Parse authentication.
     */
    private void performLogin() {
        try {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Validation
            if (username.isEmpty() || password.isEmpty()) {
                ErrorHandler.showValidationError(this, R.string.error_login_empty_fields);
                ErrorHandler.logWarning(TAG, "Login attempt with empty fields");
                return;
            }

            // Route to appropriate authentication method
            if (BuildConfig.MOCK_MODE) {
                performMockLogin(username, password);
            } else {
                performParseLogin(username, password);
            }
            
        } catch (Exception e) {
            ErrorHandler.showAuthError(this, R.string.error_login_generic);
            ErrorHandler.logError(TAG, "Error during login attempt", e);
        }
    }

    /**
     * 🆕 REAL PARSE LOGIN IMPLEMENTATION
     * Authenticates user with Parse backend.
     * 
     * @param username The username to authenticate
     * @param password The password to authenticate
     */
    private void performParseLogin(String username, String password) {
        Log.d(TAG, "Attempting Parse login for user: " + username);
        
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null && user != null) {
                    // Login successful
                    Log.d(TAG, "Parse login successful for user: " + user.getUsername());
                    ErrorHandler.showToast(LoginActivity.this, "Welcome back, " + user.getUsername() + "!");
                    navigateToMain();
                } else {
                    // Login failed
                    Log.e(TAG, "Parse login failed", e);
                    
                    if (e != null) {
                        // Handle specific Parse errors
                        if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                            ErrorHandler.showAuthError(LoginActivity.this, "Invalid username or password");
                        } else if (e.getCode() == ParseException.CONNECTION_FAILED) {
                            ErrorHandler.showNetworkError(LoginActivity.this, R.string.error_network);
                        } else {
                            ErrorHandler.showAuthError(LoginActivity.this, R.string.error_login_failed);
                        }
                    } else {
                        ErrorHandler.showAuthError(LoginActivity.this, R.string.error_login_failed);
                    }
                }
            }
        });
    }

    /**
     * Mock authentication handler.
     * Validates credentials against mock users and saves session.
     * 
     * @param username The username to authenticate
     * @param password The password to authenticate
     */
    private void performMockLogin(String username, String password) {
        Log.d(TAG, "Attempting mock login for user: " + username);
        
        // Simulate network delay for realistic behavior
        new android.os.Handler().postDelayed(() -> {
            if (MockData.isValidLogin(username, password)) {
                // Save mock session
                MockSessionManager.login(this, username);
                Log.d(TAG, "Mock login successful: " + username);
                ErrorHandler.showToast(this, "Welcome back, " + username + "!");
                navigateToMain();
            } else {
                ErrorHandler.showAuthError(this, R.string.error_login_failed);
                Log.e(TAG, "Mock login failed for user: " + username);
            }
        }, 500); // 500ms simulated delay
    }

    /**
     * Navigates to the main activity after successful login.
     * Clears the back stack so user cannot return to login screen.
     */
    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}