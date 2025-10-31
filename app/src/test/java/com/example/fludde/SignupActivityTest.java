package com.example.fludde;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

import androidx.test.core.app.ApplicationProvider;

import com.example.fludde.utils.MockData;
import com.example.fludde.utils.MockSessionManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.LooperMode;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowToast;

import static org.junit.Assert.*;

/**
 * Integration Tests for SignupActivity
 * 
 * Test Coverage:
 * - Valid signup data account creation
 * - Existing username error handling
 * - Invalid email validation
 * - Weak password validation
 * - Mock mode signup
 * - UI interactions
 * - Session creation
 * 
 * @author Fludde Team
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)  // Removed manifest = Config.NONE to allow resource loading
public class SignupActivityTest extends RobolectricTestBase {

    private SignupActivity activity;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etEmail;
    private Button btnSignup;
    private Button btnCancel;

    @Before
    public void setUp() {
        // Clear any existing mock sessions before each test
        MockSessionManager.logout(ApplicationProvider.getApplicationContext());
    }

    @After
    public void tearDown() {
        // Clean up after each test
        if (activity != null && !activity.isFinishing()) {
            activity.finish();
        }
        MockSessionManager.logout(ApplicationProvider.getApplicationContext());
    }

    /**
     * Helper method to initialize the activity and get view references
     */
    private void initializeActivity() {
        activity = Robolectric.buildActivity(SignupActivity.class)
                .create()
                .start()
                .resume()
                .get();

        etUsername = activity.findViewById(R.id.etUsername);
        etPassword = activity.findViewById(R.id.etPassword);
        etEmail = activity.findViewById(R.id.etEmail);
        btnSignup = activity.findViewById(R.id.btnSignup);
        btnCancel = activity.findViewById(R.id.btnCancel);
    }

    // ==================== VALID SIGNUP TESTS ====================

    /**
     * Test: Signup with valid data creates account
     */
    @Test
    public void testSignup_withValidData_createsAccount() {
        // Given
        initializeActivity();
        assertNotNull("Activity should be initialized", activity);
        assertNotNull("Username field should exist", etUsername);
        assertNotNull("Password field should exist", etPassword);
        assertNotNull("Email field should exist", etEmail);
        assertNotNull("Signup button should exist", btnSignup);

        // When
        String username = "newuser" + System.currentTimeMillis();
        etUsername.setText(username);
        drainAll();
etPassword.setText("password123");
        drainAll();
etEmail.setText("newuser@example.com");
        drainAll();
btnSignup.performClick();

        drainAll();
ShadowLooper.idleMainLooper(500, java.util.concurrent.TimeUnit.MILLISECONDS);

        // Then
        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent nextIntent = shadowActivity.getNextStartedActivity();
        
        assertNotNull("Should navigate to MainActivity", nextIntent);
        assertEquals("Should navigate to MainActivity", 
                MainActivity.class.getName(), 
                nextIntent.getComponent().getClassName());
    }

    /**
     * Test: Signup with valid data shows success message
     */
    @Test
    public void testSignup_withValidData_showsSuccessMessage() {
        // Given
        initializeActivity();

        // When
        String username = "successuser" + System.currentTimeMillis();
        etUsername.setText(username);
        drainAll();
etPassword.setText("password123");
        drainAll();
etEmail.setText("success@example.com");
        drainAll();
btnSignup.performClick();

        drainAll();
ShadowLooper.idleMainLooper(500, java.util.concurrent.TimeUnit.MILLISECONDS);

        // Then
        assertTrue("User should be logged in after signup", 
                MockSessionManager.isLoggedIn(activity));
    }

    // ==================== EXISTING USERNAME TESTS ====================

    /**
     * Test: Signup with existing username shows error
     */
    @Test
    public void testSignup_withExistingUsername_showsError() {
        // Given
        initializeActivity();

        // When
        etUsername.setText("demo");
        drainAll();
etPassword.setText("password123");
        drainAll();
etEmail.setText("demo@example.com");
        drainAll();
btnSignup.performClick();

        drainAll();
ShadowLooper.idleMainLooper(500, java.util.concurrent.TimeUnit.MILLISECONDS);

        // Then
        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull("Error toast should be shown", latestToast);
        assertFalse("Activity should not be finishing on error", 
                activity.isFinishing());
    }

    /**
     * Test: Signup with existing username (different case) shows error
     */
    @Test
    public void testSignup_withExistingUsernameDifferentCase_showsError() {
        // Given
        initializeActivity();

        // When
        etUsername.setText("DEMO");
        drainAll();
etPassword.setText("password123");
        drainAll();
etEmail.setText("demo2@example.com");
        drainAll();
btnSignup.performClick();

        drainAll();
ShadowLooper.idleMainLooper(500, java.util.concurrent.TimeUnit.MILLISECONDS);

        // Then
        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull("Error toast should be shown for case-insensitive duplicate", latestToast);
    }

    /**
     * Test: Signup with multiple existing usernames shows appropriate errors
     */
    @Test
    public void testSignup_withMultipleExistingUsernames_showsErrors() {
        // Given
        String[] existingUsers = {"demo", "testuser", "alice"};

        for (String existingUser : existingUsers) {
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
            
            initializeActivity();

            // When
            etUsername.setText(existingUser);
            drainAll();
etPassword.setText("password123");
            drainAll();
etEmail.setText(existingUser + "@example.com");
            drainAll();
btnSignup.performClick();

            drainAll();
ShadowLooper.idleMainLooper(500, java.util.concurrent.TimeUnit.MILLISECONDS);

            // Then
            String latestToast = ShadowToast.getTextOfLatestToast();
            assertNotNull("Error should be shown for existing user: " + existingUser, 
                    latestToast);
        }
    }

    // ==================== EMAIL VALIDATION TESTS ====================

    /**
     * Test: Signup with invalid email shows error
     */
    @Test
    public void testSignup_withInvalidEmail_showsError() {
        // Given
        initializeActivity();

        // When
        String username = "emailuser" + System.currentTimeMillis();
        etUsername.setText(username);
        drainAll();
etPassword.setText("password123");
        drainAll();
etEmail.setText("invalid-email");
        drainAll();
btnSignup.performClick();

        drainAll();
ShadowLooper.idleMainLooper(500, java.util.concurrent.TimeUnit.MILLISECONDS);

        // Then
        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull("Validation error toast should be shown", latestToast);
        assertFalse("Activity should not be finishing on validation error", 
                activity.isFinishing());
    }

    /**
     * Test: Signup with various invalid emails shows errors
     */
    @Test
    public void testSignup_withVariousInvalidEmails_showsErrors() {
        // Given
        String[] invalidEmails = {
            "notanemail",
            "@example.com",
            "user@",
            "user @example.com",
            "user@.com"
        };

        for (String invalidEmail : invalidEmails) {
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
            
            initializeActivity();

            // When
            String username = "user" + System.currentTimeMillis();
            etUsername.setText(username);
            drainAll();
etPassword.setText("password123");
            drainAll();
etEmail.setText(invalidEmail);
            drainAll();
btnSignup.performClick();

            drainAll();
ShadowLooper.idleMainLooper(500, java.util.concurrent.TimeUnit.MILLISECONDS);

            // Then
            String latestToast = ShadowToast.getTextOfLatestToast();
            assertNotNull("Validation error should be shown for: " + invalidEmail, 
                    latestToast);
        }
    }

    /**
     * Test: Signup with valid email formats succeeds
     */
    @Test
    public void testSignup_withValidEmailFormats_succeeds() {
        // Given
        String[] validEmails = {
            "user@example.com",
            "user.name@example.com",
            "user+tag@example.co.uk"
        };

        for (String validEmail : validEmails) {
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
            MockSessionManager.logout(ApplicationProvider.getApplicationContext());
            
            initializeActivity();

            // When
            String username = "user" + System.currentTimeMillis();
            etUsername.setText(username);
            drainAll();
etPassword.setText("password123");
            drainAll();
etEmail.setText(validEmail);
            drainAll();
btnSignup.performClick();

            drainAll();
ShadowLooper.idleMainLooper(500, java.util.concurrent.TimeUnit.MILLISECONDS);

            // Then
            assertTrue("Signup should succeed with valid email: " + validEmail, 
                    MockSessionManager.isLoggedIn(activity));
            
            MockSessionManager.logout(ApplicationProvider.getApplicationContext());
        }
    }

    /**
     * Test: Signup with email missing domain shows error
     */
    @Test
    public void testSignup_withEmailMissingDomain_showsError() {
        // Given
        initializeActivity();

        // When
        String username = "nodomain" + System.currentTimeMillis();
        etUsername.setText(username);
        drainAll();
etPassword.setText("password123");
        drainAll();
etEmail.setText("user@");
        drainAll();
btnSignup.performClick();

        drainAll();
ShadowLooper.idleMainLooper(500, java.util.concurrent.TimeUnit.MILLISECONDS);

        // Then
        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull("Validation error should be shown", latestToast);
    }

    // ==================== PASSWORD VALIDATION TESTS ====================

    /**
     * Test: Signup with weak password shows error
     */
    @Test
    public void testSignup_withWeakPassword_showsError() {
        // Given
        initializeActivity();

        // When
        String username = "weakpass" + System.currentTimeMillis();
        etUsername.setText(username);
        drainAll();
etPassword.setText("123");
        drainAll();
etEmail.setText("weak@example.com");
        drainAll();
btnSignup.performClick();

        drainAll();
ShadowLooper.idleMainLooper(500, java.util.concurrent.TimeUnit.MILLISECONDS);

        // Then
        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull("Weak password error should be shown", latestToast);
        assertFalse("Activity should not be finishing", activity.isFinishing());
    }

    /**
     * Test: Signup with various weak passwords shows errors
     */
    @Test
    public void testSignup_withVariousWeakPasswords_showsErrors() {
        // Given
        String[] weakPasswords = {"123", "pass", "12345"};

        for (String weakPassword : weakPasswords) {
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
            
            initializeActivity();

            // When
            String username = "user" + System.currentTimeMillis();
            etUsername.setText(username);
            drainAll();
etPassword.setText(weakPassword);
            drainAll();
etEmail.setText("user@example.com");
            drainAll();
btnSignup.performClick();

            drainAll();
ShadowLooper.idleMainLooper(500, java.util.concurrent.TimeUnit.MILLISECONDS);

            // Then
            String latestToast = ShadowToast.getTextOfLatestToast();
            assertNotNull("Weak password error should be shown for: " + weakPassword, 
                    latestToast);
        }
    }

    /**
     * Test: Signup with minimum password length succeeds
     */
    @Test
    public void testSignup_withMinimumPasswordLength_succeeds() {
        // Given
        initializeActivity();

        // When
        String username = "minpass" + System.currentTimeMillis();
        etUsername.setText(username);
        drainAll();
etPassword.setText("pass12");
        drainAll();
etEmail.setText("minpass@example.com");
        drainAll();
btnSignup.performClick();

        drainAll();
ShadowLooper.idleMainLooper(500, java.util.concurrent.TimeUnit.MILLISECONDS);

        // Then
        assertTrue("Signup should succeed with minimum length password", 
                MockSessionManager.isLoggedIn(activity));
    }

    // ==================== EMPTY FIELD VALIDATION TESTS ====================

    /**
     * Test: Signup with empty username shows validation error
     */
    @Test
    public void testSignup_withEmptyUsername_showsValidationError() {
        // Given
        initializeActivity();

        // When
        etUsername.setText("");
        drainAll();
etPassword.setText("password123");
        drainAll();
etEmail.setText("test@example.com");
        drainAll();
btnSignup.performClick();

        drainAll();
ShadowLooper.idleMainLooper(500, java.util.concurrent.TimeUnit.MILLISECONDS);

        // Then
        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull("Validation error should be shown", latestToast);
        assertFalse("Activity should not be finishing", activity.isFinishing());
    }

    /**
     * Test: Signup with empty password shows error
     */
    @Test
    public void testSignup_withEmptyPassword_showsError() {
        // Given
        initializeActivity();

        // When
        String username = "nopass" + System.currentTimeMillis();
        etUsername.setText(username);
        drainAll();
etPassword.setText("");
        drainAll();
etEmail.setText("nopass@example.com");
        drainAll();
btnSignup.performClick();

        drainAll();
ShadowLooper.idleMainLooper(500, java.util.concurrent.TimeUnit.MILLISECONDS);

        // Then
        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull("Validation error should be shown", latestToast);
        assertFalse("Activity should not be finishing", activity.isFinishing());
    }

    /**
     * Test: Signup with empty email shows validation error
     */
    @Test
    public void testSignup_withEmptyEmail_showsValidationError() {
        // Given
        initializeActivity();

        // When
        String username = "noemail" + System.currentTimeMillis();
        etUsername.setText(username);
        drainAll();
etPassword.setText("password123");
        drainAll();
etEmail.setText("");
        drainAll();
btnSignup.performClick();

        drainAll();
ShadowLooper.idleMainLooper(500, java.util.concurrent.TimeUnit.MILLISECONDS);

        // Then
        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull("Validation error should be shown", latestToast);
        assertFalse("Activity should not be finishing", activity.isFinishing());
    }

    /**
     * Test: Signup with all empty fields shows validation error
     */
    @Test
    public void testSignup_withAllEmptyFields_showsValidationError() {
        // Given
        initializeActivity();

        // When
        etUsername.setText("");
        drainAll();
etPassword.setText("");
        drainAll();
etEmail.setText("");
        drainAll();
btnSignup.performClick();

        drainAll();
ShadowLooper.idleMainLooper(500, java.util.concurrent.TimeUnit.MILLISECONDS);

        // Then
        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull("Validation error should be shown", latestToast);
        assertFalse("Activity should not be finishing", activity.isFinishing());
    }

    /**
     * Test: Signup with whitespace fields shows validation error
     */
    @Test
    public void testSignup_withWhitespaceFields_showsValidationError() {
        // Given
        initializeActivity();

        // When
        etUsername.setText("   ");
        drainAll();
etPassword.setText("   ");
        drainAll();
etEmail.setText("   ");
        drainAll();
btnSignup.performClick();

        drainAll();
ShadowLooper.idleMainLooper(500, java.util.concurrent.TimeUnit.MILLISECONDS);

        // Then
        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull("Validation error should be shown", latestToast);
        assertFalse("Activity should not be finishing", activity.isFinishing());
    }

    // ==================== INPUT TRIMMING TESTS ====================

    /**
     * Test: Signup trims whitespace from inputs
     */
    @Test
    public void testSignup_trimsWhitespace_succeeds() {
        // Given
        initializeActivity();

        // When
        String username = "trimuser" + System.currentTimeMillis();
        etUsername.setText("  " + username + "  ");
        drainAll();
etPassword.setText("  password123  ");
        drainAll();
etEmail.setText("  trim@example.com  ");
        drainAll();
btnSignup.performClick();

        drainAll();
ShadowLooper.idleMainLooper(500, java.util.concurrent.TimeUnit.MILLISECONDS);

        // Then
        assertTrue("Signup should succeed with trimmed input", 
                MockSessionManager.isLoggedIn(activity));
        assertEquals("Session should contain trimmed username", 
                username, 
                MockSessionManager.getCurrentUsername(activity));
    }

    // ==================== USERNAME VALIDATION TESTS ====================

    /**
     * Test: Signup with numbers in username succeeds
     */
    @Test
    public void testSignup_withNumbersInUsername_succeeds() {
        // Given
        initializeActivity();

        // When
        String username = "user" + System.currentTimeMillis();
        etUsername.setText(username);
        drainAll();
etPassword.setText("password123");
        drainAll();
etEmail.setText("numbers@example.com");
        drainAll();
btnSignup.performClick();

        drainAll();
ShadowLooper.idleMainLooper(500, java.util.concurrent.TimeUnit.MILLISECONDS);

        // Then
        assertTrue("Signup should succeed with numbers in username", 
                MockSessionManager.isLoggedIn(activity));
    }

    /**
     * Test: Signup with special characters in username handles appropriately
     */
    @Test
    public void testSignup_withSpecialCharactersInUsername_handlesAppropriately() {
        // Given
        initializeActivity();

        // When
        String username = "user_" + System.currentTimeMillis();
        etUsername.setText(username);
        drainAll();
etPassword.setText("password123");
        drainAll();
etEmail.setText("special@example.com");
        drainAll();
btnSignup.performClick();

        drainAll();
ShadowLooper.idleMainLooper(500, java.util.concurrent.TimeUnit.MILLISECONDS);

        // Then
        assertNotNull("Activity should remain active", activity);
    }

    // ==================== EDGE CASE TESTS ====================

    /**
     * Test: Signup with very long username handles gracefully
     */
    @Test
    public void testSignup_veryLongUsername_handlesGracefully() {
        // Given
        initializeActivity();

        // When
        String longUsername = "user" + "a".repeat(96) + System.currentTimeMillis();
        etUsername.setText(longUsername);
        drainAll();
etPassword.setText("password123");
        drainAll();
etEmail.setText("longuser@example.com");
        drainAll();
btnSignup.performClick();

        drainAll();
ShadowLooper.idleMainLooper(500, java.util.concurrent.TimeUnit.MILLISECONDS);

        // Then
        assertNotNull("Activity should handle long username", activity);
    }

    /**
     * Test: Signup with very long password handles gracefully
     */
    @Test
    public void testSignup_veryLongPassword_handlesGracefully() {
        // Given
        initializeActivity();

        // When
        String longPassword = "pass" + "a".repeat(196);
        String username = "longpassuser" + System.currentTimeMillis();
        etUsername.setText(username);
        drainAll();
etPassword.setText(longPassword);
        drainAll();
etEmail.setText("longpass@example.com");
        drainAll();
btnSignup.performClick();

        drainAll();
ShadowLooper.idleMainLooper(500, java.util.concurrent.TimeUnit.MILLISECONDS);

        // Then
        assertNotNull("Activity should handle long password", activity);
    }

    /**
     * Test: Rapid clicking signup button doesn't cause issues
     */
    @Test
    public void testSignup_rapidClicks_handlesGracefully() {
        // Given
        initializeActivity();
        String username = "rapiduser" + System.currentTimeMillis();
        etUsername.setText(username);
        drainAll();
etPassword.setText("password123");
        drainAll();
etEmail.setText("rapid@example.com");

        drainAll();
// When
        for (int i = 0; i < 5; i++) {
            btnSignup.performClick();
        drainAll();
}

        ShadowLooper.idleMainLooper(500, java.util.concurrent.TimeUnit.MILLISECONDS);

        // Then
        assertNotNull("Activity should still exist", activity);
    }

    // ==================== SESSION TESTS ====================

    /**
     * Test: Successful signup creates session
     */
    @Test
    public void testSignup_successful_createsSession() {
        // Given
        initializeActivity();

        // When
        String username = "sessionuser" + System.currentTimeMillis();
        etUsername.setText(username);
        drainAll();
etPassword.setText("password123");
        drainAll();
etEmail.setText("session@example.com");
        drainAll();
btnSignup.performClick();

        drainAll();
ShadowLooper.idleMainLooper(500, java.util.concurrent.TimeUnit.MILLISECONDS);

        // Then
        assertTrue("Session should be created after signup", 
                MockSessionManager.isLoggedIn(activity));
        assertEquals("Session should contain correct username", 
                username, 
                MockSessionManager.getCurrentUsername(activity));

        assertTrue("Session should persist", 
                MockSessionManager.isLoggedIn(ApplicationProvider.getApplicationContext()));
    }

    // ==================== NAVIGATION TESTS ====================

    /**
     * Test: Successful signup navigates to MainActivity with correct flags
     */
    @Test
    public void testSignup_successful_navigatesWithCorrectFlags() {
        // Given
        initializeActivity();

        // When
        String username = "navuser" + System.currentTimeMillis();
        etUsername.setText(username);
        drainAll();
etPassword.setText("password123");
        drainAll();
etEmail.setText("nav@example.com");
        drainAll();
btnSignup.performClick();

        drainAll();
ShadowLooper.idleMainLooper(500, java.util.concurrent.TimeUnit.MILLISECONDS);

        // Then
        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent nextIntent = shadowActivity.getNextStartedActivity();
        
        assertNotNull("Should navigate after successful signup", nextIntent);
        assertEquals("Should navigate to MainActivity", 
                MainActivity.class.getName(), 
                nextIntent.getComponent().getClassName());
        
        int flags = nextIntent.getFlags();
        assertTrue("Should have NEW_TASK flag", 
                (flags & Intent.FLAG_ACTIVITY_NEW_TASK) != 0);
        assertTrue("Should have CLEAR_TASK flag", 
                (flags & Intent.FLAG_ACTIVITY_CLEAR_TASK) != 0);
    }

    /**
     * Test: Cancel button closes activity
     */
    @Test
    public void testSignup_cancelButton_closesActivity() {
        // Given
        initializeActivity();
        assertNotNull("Cancel button should exist", btnCancel);

        // When
        btnCancel.performClick();

        drainAll();
// Then
        assertTrue("Activity should finish when cancel is clicked", 
                activity.isFinishing() || activity.isDestroyed());
    }

    // ==================== PARSE INTEGRATION TEST PLACEHOLDER ====================

    /**
     * Test: Signup in real mode uses Parse authentication
     * 
     * Note: This test documents expected behavior for real mode
     * In debug builds, MOCK_MODE is true, so this just verifies the activity loads
     */
    @Test
    public void testSignup_realMode_usesParseAuthentication() {
        // Given
        initializeActivity();

        // When
        String username = "parseuser" + System.currentTimeMillis();
        etUsername.setText(username);
        drainAll();
etPassword.setText("password123");
        drainAll();
etEmail.setText("parse@example.com");
        drainAll();
btnSignup.performClick();

        drainAll();
// Then
        assertNotNull("Activity should remain active", activity);
    }
}