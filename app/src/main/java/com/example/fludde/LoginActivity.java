package com.example.fludde;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fludde.utils.ErrorHandler;

/**
 * LoginActivity - Refactored to use ErrorHandler utility and string resources.
 * 
 * Changes:
 * - All hardcoded strings moved to strings.xml
 * - All Toast.makeText() replaced with ErrorHandler methods
 * - Consistent error handling
 * - Improved logging with proper tags
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> performLogin());
    }

    private void performLogin() {
        try {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Validation
            if (username.isEmpty() || password.isEmpty()) {
                // ✅ Using ErrorHandler with string resource
                ErrorHandler.showValidationError(this, R.string.error_login_empty_fields);
                ErrorHandler.logWarning(TAG, "Login attempt with empty fields");
                return;
            }

            // Mock login logic (replace with actual authentication)
            if (username.equals("admin") && password.equals("admin")) {
                Log.d(TAG, "Login successful for user: " + username);
                navigateToMain();
            } else {
                // ✅ Using ErrorHandler with string resource
                ErrorHandler.showAuthError(this, R.string.error_login_failed);
                Log.e(TAG, "Login failed for user: " + username);
            }
        } catch (Exception e) {
            // ✅ Using ErrorHandler with string resource
            ErrorHandler.showAuthError(this, R.string.error_login_generic);
            ErrorHandler.logError(TAG, "Error during login attempt", e);
        }
    }

    private void navigateToMain() {
        try {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            Log.d(TAG, "Navigated to MainActivity");
        } catch (Exception e) {
            // ✅ Using ErrorHandler with string resource
            ErrorHandler.showToast(this, R.string.error_navigation);
            ErrorHandler.logError(TAG, "Error navigating to MainActivity", e);
        }
    }
}