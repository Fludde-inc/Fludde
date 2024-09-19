package com.example.fludde;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

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

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        btnSignup = findViewById(R.id.btnSignup);
        btnCancel = findViewById(R.id.btnCancel);

        btnSignup.setOnClickListener(v -> attemptSignup());
        btnCancel.setOnClickListener(v -> {
            finish();
            Log.d(TAG, "Signup cancelled and activity closed");
        });
    }

    private void attemptSignup() {
        try {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Signup attempt with empty fields");
                return;
            }

            if (username.equals("admin")) {
                Log.e(TAG, "Signup failed: Username already taken: " + username);
                Toast.makeText(this, "Signup failed. Username already taken.", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Signup successful for user: " + username);
                navigateToMain();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during signup attempt", e);
            Toast.makeText(this, "An error occurred during signup. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToMain() {
        try {
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            Log.d(TAG, "Navigated to MainActivity after signup");
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to MainActivity", e);
            Toast.makeText(this, "An error occurred while navigating to the main screen.", Toast.LENGTH_SHORT).show();
        }
    }
}
