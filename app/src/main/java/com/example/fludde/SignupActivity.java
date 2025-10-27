package com.example.fludde;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fludde.utils.ErrorHandler;

/**
 * SignupActivity - Refactored to use ErrorHandler utility and string resources.
 * 
 * Changes:
 * - All hardcoded strings moved to strings.xml
 * - All Toast.makeText() replaced with ErrorHandler methods
 * - Consistent error handling
 * - Improved logging with proper tags
 */
public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    private EditText etUsername;
    private EditText etPassword;
    private EditText etEmail;
    private Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        btnSignup = findViewById(R.id.btnSignup);

        btnSignup.setOnClickListener(v -> performSignup());
    }

    private void performSignup() {
        try {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            // Validation
            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                // ✅ Using ErrorHandler with string resource
                ErrorHandler.showValidationError(this, R.string.error_signup_empty_fields);
                ErrorHandler.logWarning(TAG, "Signup attempt with empty fields");
                return;
            }

            // Mock signup logic (replace with actual registration)
            if (username.equals("admin")) {
                // ✅ Using ErrorHandler with string resource
                ErrorHandler.showAuthError(this, R.string.error_signup_failed);
                Log.e(TAG, "Signup failed: Username already taken: " + username);
            } else {
                Log.d(TAG, "Signup successful for user: " + username);
                navigateToMain();
            }
        } catch (Exception e) {
            // ✅ Using ErrorHandler with string resource
            ErrorHandler.showAuthError(this, R.string.error_signup_generic);
            ErrorHandler.logError(TAG, "Error during signup attempt", e);
        }
    }

    private void navigateToMain() {
        try {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            Log.d(TAG, "Navigated to MainActivity after signup");
        } catch (Exception e) {
            // ✅ Using ErrorHandler with string resource
            ErrorHandler.showToast(this, R.string.error_navigation);
            ErrorHandler.logError(TAG, "Error navigating to MainActivity", e);
        }
    }
}