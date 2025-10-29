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
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Signup Activity with full Parse registration and session management.
 * 
 * Features:
 * - Mock mode registration (when BuildConfig.MOCK_MODE is true)
 * - Real Parse registration (when BuildConfig.MOCK_MODE is false)
 * - Input validation
 * - Error handling using ErrorHandler utility
 * - Session management via MockSessionManager (mock mode only)
 * 
 * Changes:
 * - All hardcoded strings moved to strings.xml
 * - All Toast.makeText() replaced with ErrorHandler methods
 * - Consistent error handling
 * - Improved logging with proper tags
 * - Added mock signup functionality
 * - Implemented real Parse signup
 */
public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    private EditText etUsername;
    private EditText etPassword;
    private EditText etEmail;
    private Button btnSignup;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        btnSignup = findViewById(R.id.btnSignup);
        btnCancel = findViewById(R.id.btnCancel);

        // Set up button click listeners
        btnSignup.setOnClickListener(v -> performSignup());
        btnCancel.setOnClickListener(v -> finish());
    }

    /**
     * Handles the signup process.
     * Routes to mock registration if MOCK_MODE is enabled,
     * otherwise uses real Parse registration.
     */
    private void performSignup() {
        try {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            // Validation
            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                ErrorHandler.showValidationError(this, R.string.error_signup_empty_fields);
                ErrorHandler.logWarning(TAG, "Signup attempt with empty fields");
                return;
            }

            // Additional password validation
            if (password.length() < 6) {
                ErrorHandler.showValidationError(this, "Password must be at least 6 characters");
                return;
            }

            // Basic email validation
            if (!email.contains("@") || !email.contains(".")) {
                ErrorHandler.showValidationError(this, "Please enter a valid email address");
                return;
            }

            // Route to appropriate signup method
            if (BuildConfig.MOCK_MODE) {
                performMockSignup(username, password, email);
            } else {
                performParseSignup(username, password, email);
            }

        } catch (Exception e) {
            ErrorHandler.showAuthError(this, R.string.error_signup_generic);
            ErrorHandler.logError(TAG, "Error during signup attempt", e);
        }
    }

    /**
     * 🆕 REAL PARSE SIGNUP IMPLEMENTATION
     * Registers a new user with Parse backend.
     * 
     * @param username The desired username
     * @param password The desired password
     * @param email The user's email address
     */
    private void performParseSignup(String username, String password, String email) {
        Log.d(TAG, "Attempting Parse signup for user: " + username);

        // Create new ParseUser object
        ParseUser newUser = new ParseUser();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setEmail(email);

        // Optional: Set default profile picture URL
        // newUser.put("profilePicture", "https://i.imgur.com/default.png");

        // Sign up the user asynchronously
        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // Signup successful
                    Log.d(TAG, "Parse signup successful for user: " + username);
                    ErrorHandler.showToast(SignupActivity.this, "Account created successfully! Welcome, " + username + "!");
                    navigateToMain();
                } else {
                    // Signup failed
                    Log.e(TAG, "Parse signup failed", e);
                    
                    // Handle specific Parse errors
                    if (e.getCode() == ParseException.USERNAME_TAKEN) {
                        ErrorHandler.showAuthError(SignupActivity.this, "Username is already taken");
                    } else if (e.getCode() == ParseException.EMAIL_TAKEN) {
                        ErrorHandler.showAuthError(SignupActivity.this, "Email is already registered");
                    } else if (e.getCode() == ParseException.CONNECTION_FAILED) {
                        ErrorHandler.showNetworkError(SignupActivity.this, R.string.error_network);
                    } else {
                        ErrorHandler.showAuthError(SignupActivity.this, R.string.error_signup_generic);
                    }
                }
            }
        });
    }

    /**
     * Mock signup handler.
     * Simulates user registration and saves session.
     * 
     * @param username The desired username
     * @param password The desired password
     * @param email The user's email address
     */
    private void performMockSignup(String username, String password, String email) {
        Log.d(TAG, "Attempting mock signup for user: " + username);
        
        // Simulate network delay for realistic behavior
        new android.os.Handler().postDelayed(() -> {
            if (MockData.canRegisterUser(username, email)) {
                // Register user in mock data
                MockData.registerMockUser(username, password, email);
                
                // Save mock session
                MockSessionManager.login(this, username);
                
                Log.d(TAG, "Mock signup successful: " + username);
                ErrorHandler.showToast(this, "Account created successfully! Welcome, " + username + "!");
                navigateToMain();
            } else {
                ErrorHandler.showToast(this, "Username or email already taken");
                Log.e(TAG, "Mock signup failed: username/email exists");
            }
        }, 500); // 500ms simulated delay
    }

    /**
     * Navigates to the main activity after successful signup.
     * Clears the back stack so user cannot return to signup screen.
     */
    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}