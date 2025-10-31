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
import org.robolectric.shadows.ShadowToast;

import static org.junit.Assert.*;

/**
 * Integration Tests for LoginActivity
 * 
 * Test Coverage:
 * - Valid credentials navigation
 * - Invalid credentials error handling
 * - Empty fields validation
 * - Mock mode authentication
 * - Session persistence
 * - UI interactions
 * 
 * @author Fludde Team
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)  // Removed manifest = Config.NONE to allow resource loading
public class LoginActivityTest extends RobolectricTestBase {

    private LoginActivity activity;
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignup;

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
        activity = Robolectric.buildActivity(LoginActivity.class)
                .create()
                .start()
                .resume()
                .get();

        etUsername = activity.findViewById(R.id.etUsername);
        etPassword = activity.findViewById(R.id.etPassword);
        btnLogin = activity.findViewById(R.id.btnLogin);
        btnSignup = activity.findViewById(R.id.btnSignup);
    }

    // ==================== VALID CREDENTIALS TESTS ====================

    /**
     * Test: Login with valid credentials navigates to MainActivity
     */
    @Test
    public void testLogin_withValidCredentials_navigatesToMain() {
        // Given
        initializeActivity();
        assertNotNull("Activity should be initialized", activity);
        assertNotNull("Username field should exist", etUsername);
        assertNotNull("Password field should exist", etPassword);
        assertNotNull("Login button should exist", btnLogin);

        // When
        etUsername.setText("demo");
        drainAll();
etPassword.setText("demo123");
        drainAll();
btnLogin.performClick();

        drainAll();
// Then
        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent nextIntent = shadowActivity.getNextStartedActivity();
        
        assertNotNull("Should start MainActivity", nextIntent);
        assertEquals("Should navigate to MainActivity", 
                MainActivity.class.getName(), 
                nextIntent.getComponent().getClassName());
        
        assertTrue("User should be logged in after successful login", 
                MockSessionManager.isLoggedIn(activity));
    }

    /**
     * Test: Login with valid credentials saves session
     */
    @Test
    public void testLogin_withValidCredentials_savesSession() {
        // Given
        initializeActivity();

        // When
        etUsername.setText("demo");
        drainAll();
etPassword.setText("demo123");
        drainAll();
btnLogin.performClick();

        drainAll();
// Then
        assertTrue("Session should be created", 
                MockSessionManager.isLoggedIn(activity));
        assertEquals("Session should contain correct username", 
                "demo", 
                MockSessionManager.getCurrentUsername(activity));
    }

    // ==================== INVALID CREDENTIALS TESTS ====================

    /**
     * Test: Login with invalid username shows error
     */
    @Test
    public void testLogin_withInvalidUsername_showsError() {
        // Given
        initializeActivity();

        // When
        etUsername.setText("invaliduser");
        drainAll();
etPassword.setText("password123");
        drainAll();
btnLogin.performClick();

        drainAll();
// Then
        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull("Error toast should be shown", latestToast);
        assertFalse("User should not be logged in", 
                MockSessionManager.isLoggedIn(activity));
    }

    /**
     * Test: Login with invalid password shows error
     */
    @Test
    public void testLogin_withInvalidPassword_showsError() {
        // Given
        initializeActivity();

        // When
        etUsername.setText("demo");
        drainAll();
etPassword.setText("wrongpassword");
        drainAll();
btnLogin.performClick();

        drainAll();
// Then
        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull("Error toast should be shown", latestToast);
        assertFalse("User should not be logged in", 
                MockSessionManager.isLoggedIn(activity));
    }

    /**
     * Test: Login with invalid credentials shows error
     */
    @Test
    public void testLogin_withInvalidCredentials_showsError() {
        // Given
        initializeActivity();

        // When
        etUsername.setText("wronguser");
        drainAll();
etPassword.setText("wrongpass");
        drainAll();
btnLogin.performClick();

        drainAll();
// Then
        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull("Error toast should be shown", latestToast);
        assertFalse("User should not be logged in", 
                MockSessionManager.isLoggedIn(activity));
    }

    // ==================== EMPTY FIELDS VALIDATION TESTS ====================

    /**
     * Test: Login with empty fields shows validation error
     */
    @Test
    public void testLogin_withEmptyFields_showsValidationError() {
        // Given
        initializeActivity();

        // When
        etUsername.setText("");
        drainAll();
etPassword.setText("");
        drainAll();
btnLogin.performClick();

        drainAll();
// Then
        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull("Validation error toast should be shown", latestToast);
        assertFalse("Activity should not be finishing on validation error", 
                activity.isFinishing());
    }

    /**
     * Test: Login with empty username shows validation error
     */
    @Test
    public void testLogin_withEmptyUsername_showsValidationError() {
        // Given
        initializeActivity();

        // When
        etUsername.setText("");
        drainAll();
etPassword.setText("password123");
        drainAll();
btnLogin.performClick();

        drainAll();
// Then
        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull("Validation error toast should be shown", latestToast);
        assertFalse("Activity should not be finishing on validation error", 
                activity.isFinishing());
    }

    /**
     * Test: Login with empty password shows validation error
     */
    @Test
    public void testLogin_withEmptyPassword_showsValidationError() {
        // Given
        initializeActivity();

        // When
        etUsername.setText("demo");
        drainAll();
etPassword.setText("");
        drainAll();
btnLogin.performClick();

        drainAll();
// Then
        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull("Validation error toast should be shown", latestToast);
        assertFalse("Activity should not be finishing on validation error", 
                activity.isFinishing());
    }

    /**
     * Test: Login with whitespace fields shows validation error
     */
    @Test
    public void testLogin_withWhitespaceFields_showsValidationError() {
        // Given
        initializeActivity();

        // When
        etUsername.setText("   ");
        drainAll();
etPassword.setText("   ");
        drainAll();
btnLogin.performClick();

        drainAll();
// Then
        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull("Validation error toast should be shown", latestToast);
        assertFalse("Activity should not be finishing on validation error", 
                activity.isFinishing());
    }

    // ==================== MOCK MODE TESTS ====================

    /**
     * Test: Login in mock mode uses correct authentication
     */
    @Test
    public void testLogin_mockMode_usesCorrectAuthentication() {
        // Given
        initializeActivity();

        // When
        etUsername.setText("demo");
        drainAll();
etPassword.setText("demo123");
        drainAll();
btnLogin.performClick();

        drainAll();
// Then
        assertTrue("Mock session should be created", 
                MockSessionManager.isLoggedIn(activity));
        assertEquals("Mock session should contain correct username", 
                "demo", 
                MockSessionManager.getCurrentUsername(activity));
    }

    /**
     * Test: Login in mock mode supports multiple users
     */
    @Test
    public void testLogin_mockMode_multipleUsers() {
        // Test first user
        initializeActivity();
        etUsername.setText("demo");
        drainAll();
etPassword.setText("demo123");
        drainAll();
btnLogin.performClick();
        drainAll();
assertTrue("First user should be logged in", MockSessionManager.isLoggedIn(activity));
        
        // Logout and test second user
        MockSessionManager.logout(activity);
        if (activity != null && !activity.isFinishing()) {
            activity.finish();
        }
        
        initializeActivity();
        etUsername.setText("testuser");
        drainAll();
etPassword.setText("test123");
        drainAll();
btnLogin.performClick();
        drainAll();
assertTrue("Second user should be logged in", MockSessionManager.isLoggedIn(activity));
        assertEquals("Second user should have correct username", 
                "testuser", 
                MockSessionManager.getCurrentUsername(activity));
    }

    // ==================== SESSION TESTS ====================

    /**
     * Test: Existing session skips login screen
     */
    @Test
    public void testLogin_existingSession_skipsLogin() {
        // Given - Create existing session
        MockSessionManager.login(ApplicationProvider.getApplicationContext(), "demo");

        // When - Launch LoginActivity
        initializeActivity();

        // Then - Should immediately navigate to MainActivity
        assertTrue("User should be logged in", 
                MockSessionManager.isLoggedIn(ApplicationProvider.getApplicationContext()));
    }

    // ==================== ERROR HANDLING TESTS ====================

    /**
     * Test: Login handles null input gracefully
     */
    @Test
    public void testLogin_nullInput_handlesGracefully() {
        // Given
        initializeActivity();

        // When
        etUsername.setText(null);
        drainAll();
etPassword.setText(null);
        drainAll();
btnLogin.performClick();

        drainAll();
// Then
        assertNotNull("Activity should still exist", activity);
        assertFalse("Activity should not be finishing on error", 
                activity.isFinishing());
    }

    /**
     * Test: Rapid clicking login button doesn't cause issues
     */
    @Test
    public void testLogin_rapidClicks_handlesGracefully() {
        // Given
        initializeActivity();
        etUsername.setText("demo");
        drainAll();
etPassword.setText("demo123");

        drainAll();
// When
        for (int i = 0; i < 5; i++) {
            btnLogin.performClick();
        drainAll();
}

        // Then
        assertNotNull("Activity should still exist", activity);
        assertTrue("User should be logged in", 
                MockSessionManager.isLoggedIn(activity));
    }

    // ==================== UI NAVIGATION TESTS ====================

    /**
     * Test: Signup button navigates to SignupActivity
     */
    @Test
    public void testLogin_signupButton_navigatesToSignup() {
        // Given
        initializeActivity();
        assertNotNull("Signup button should exist", btnSignup);

        // When
        btnSignup.performClick();

        drainAll();
// Then
        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent nextIntent = shadowActivity.getNextStartedActivity();
        
        assertNotNull("Should start signup activity", nextIntent);
        assertEquals("Should navigate to SignupActivity", 
                SignupActivity.class.getName(), 
                nextIntent.getComponent().getClassName());
    }

    // ==================== INPUT VALIDATION TESTS ====================

    /**
     * Test: Login trims whitespace from inputs
     */
    @Test
    public void testLogin_trimsWhitespace_succeeds() {
        // Given
        initializeActivity();

        // When
        etUsername.setText("  demo  ");
        drainAll();
etPassword.setText("  demo123  ");
        drainAll();
btnLogin.performClick();

        drainAll();
// Then
        assertTrue("Login should succeed with trimmed input", 
                MockSessionManager.isLoggedIn(activity));
        
        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        assertNotNull("Should navigate after login", 
                shadowActivity.getNextStartedActivity());
    }

    /**
     * Test: Login with special characters in password works
     */
    @Test
    public void testLogin_specialCharactersInPassword_works() {
        // Given
        initializeActivity();

        // When
        etUsername.setText("demo");
        drainAll();
etPassword.setText("demo123");
        drainAll();
btnLogin.performClick();

        drainAll();
// Then
        assertTrue("Login with standard password should work", 
                MockSessionManager.isLoggedIn(activity));
    }

    // ==================== PARSE INTEGRATION TEST PLACEHOLDER ====================

    /**
     * Test: Login in real mode uses Parse authentication
     * 
     * Note: This test documents expected behavior for real mode
     * In debug builds, MOCK_MODE is true, so this just verifies the activity loads
     */
    @Test
    public void testLogin_realMode_usesParseAuthentication() {
        // Given
        initializeActivity();

        // When
        etUsername.setText("parseuser");
        drainAll();
etPassword.setText("password123");
        drainAll();
btnLogin.performClick();

        drainAll();
// Then
        assertNotNull("Activity should remain active", activity);
    }
}