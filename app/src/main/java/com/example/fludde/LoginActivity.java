package com.example.fludde;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);

        btnLogin.setOnClickListener(v -> attemptLogin());
        btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
            Log.d(TAG, "Navigated to SignupActivity");
        });
    }

    private void attemptLogin() {
        try {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Login attempt with empty fields");
                return;
            }

            if (username.equals("admin") && password.equals("admin")) {
                Log.d(TAG, "Login successful for user: " + username);
                navigateToMain();
            } else {
                Log.e(TAG, "Login failed for user: " + username);
                Toast.makeText(this, "Login failed. Incorrect username or password.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during login attempt", e);
            Toast.makeText(this, "An error occurred during login. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToMain() {
        try {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            Log.d(TAG, "Navigated to MainActivity");
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to MainActivity", e);
            Toast.makeText(this, "An error occurred while navigating to the main screen.", Toast.LENGTH_SHORT).show();
        }
    }
}
