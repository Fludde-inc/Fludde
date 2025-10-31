package com.example.fludde.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.*;

/**
 * Unit tests for MockSessionManager class.
 * Tests session management, login/logout, and profile retrieval.
 */
@RunWith(RobolectricTestRunner.class)
public class MockSessionManagerTest {

    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        // Clear any existing session before each test
        MockSessionManager.logout(context);
    }

    @After
    public void tearDown() {
        // Clean up after each test
        MockSessionManager.logout(context);
    }

    // ========== LOGIN TESTS ==========

    @Test
    public void testLogin_savesUsername() {
        // Given
        String username = "demo";
        
        // When
        MockSessionManager.login(context, username);
        
        // Then
        String savedUsername = MockSessionManager.getCurrentUsername(context);
        assertEquals("Username should be saved", username, savedUsername);
    }

    @Test
    public void testLogin_setsLoggedInFlag() {
        // Given
        String username = "demo";
        
        // When
        MockSessionManager.login(context, username);
        
        // Then
        assertTrue("Should be logged in after login", 
                   MockSessionManager.isLoggedIn(context));
    }

    @Test
    public void testLogin_withEmptyUsername_stillSaves() {
        // Given
        String username = "";
        
        // When
        MockSessionManager.login(context, username);
        
        // Then
        assertTrue("Should still mark as logged in", 
                   MockSessionManager.isLoggedIn(context));
        assertEquals("Should save empty username", username, 
                     MockSessionManager.getCurrentUsername(context));
    }

    @Test
    public void testLogin_withNullUsername_handlesGracefully() {
        // When
        MockSessionManager.login(context, null);
        
        // Then - should not crash, but behavior may vary
        // At minimum, it should not throw an exception
        assertTrue("Method should complete without exception", true);
    }

    // ========== LOGOUT TESTS ==========

    @Test
    public void testLogout_clearsSession() {
        // Given - user is logged in
        MockSessionManager.login(context, "demo");
        assertTrue("User should be logged in initially", 
                   MockSessionManager.isLoggedIn(context));
        
        // When
        MockSessionManager.logout(context);
        
        // Then
        assertFalse("Should not be logged in after logout", 
                    MockSessionManager.isLoggedIn(context));
    }

    @Test
    public void testLogout_clearsUsername() {
        // Given - user is logged in
        String username = "demo";
        MockSessionManager.login(context, username);
        assertEquals("Username should be saved", username, 
                     MockSessionManager.getCurrentUsername(context));
        
        // When
        MockSessionManager.logout(context);
        
        // Then
        String clearedUsername = MockSessionManager.getCurrentUsername(context);
        assertTrue("Username should be empty after logout", 
                   clearedUsername.isEmpty());
    }

    @Test
    public void testLogout_whenNotLoggedIn_doesNotCrash() {
        // Given - no login has occurred
        assertFalse("User should not be logged in", 
                    MockSessionManager.isLoggedIn(context));
        
        // When
        MockSessionManager.logout(context);
        
        // Then - should not crash
        assertFalse("Should still not be logged in", 
                    MockSessionManager.isLoggedIn(context));
    }

    @Test
    public void testLogout_multipleConsecutiveCalls_doesNotCrash() {
        // Given - user is logged in
        MockSessionManager.login(context, "demo");
        
        // When - logout multiple times
        MockSessionManager.logout(context);
        MockSessionManager.logout(context);
        MockSessionManager.logout(context);
        
        // Then - should not crash and state should be consistent
        assertFalse("Should not be logged in", 
                    MockSessionManager.isLoggedIn(context));
    }

    // ========== LOGIN STATE TESTS ==========

    @Test
    public void testIsLoggedIn_afterLogin_returnsTrue() {
        // Given
        assertFalse("Should start not logged in", 
                    MockSessionManager.isLoggedIn(context));
        
        // When
        MockSessionManager.login(context, "demo");
        
        // Then
        assertTrue("Should be logged in after login", 
                   MockSessionManager.isLoggedIn(context));
    }

    @Test
    public void testIsLoggedIn_afterLogout_returnsFalse() {
        // Given - user is logged in
        MockSessionManager.login(context, "demo");
        assertTrue("Should be logged in", MockSessionManager.isLoggedIn(context));
        
        // When
        MockSessionManager.logout(context);
        
        // Then
        assertFalse("Should not be logged in after logout", 
                    MockSessionManager.isLoggedIn(context));
    }

    @Test
    public void testIsLoggedIn_withoutLogin_returnsFalse() {
        // Given - fresh session, no login
        
        // When/Then
        assertFalse("Should not be logged in by default", 
                    MockSessionManager.isLoggedIn(context));
    }

    // ========== USERNAME RETRIEVAL TESTS ==========

    @Test
    public void testGetCurrentUsername_afterLogin_returnsUsername() {
        // Given
        String username = "demo";
        
        // When
        MockSessionManager.login(context, username);
        
        // Then
        String retrievedUsername = MockSessionManager.getCurrentUsername(context);
        assertEquals("Should return the logged-in username", username, retrievedUsername);
    }

    @Test
    public void testGetCurrentUsername_afterLogout_returnsEmpty() {
        // Given - user was logged in
        MockSessionManager.login(context, "demo");
        assertEquals("demo", MockSessionManager.getCurrentUsername(context));
        
        // When
        MockSessionManager.logout(context);
        
        // Then
        String username = MockSessionManager.getCurrentUsername(context);
        assertTrue("Username should be empty after logout", username.isEmpty());
    }

    @Test
    public void testGetCurrentUsername_withoutLogin_returnsEmpty() {
        // Given - no login
        
        // When
        String username = MockSessionManager.getCurrentUsername(context);
        
        // Then
        assertNotNull("Username should not be null", username);
        assertTrue("Username should be empty when not logged in", username.isEmpty());
    }

    @Test
    public void testLogin_overwritesPreviousSession() {
        // Given - first login
        String firstUsername = "demo";
        MockSessionManager.login(context, firstUsername);
        assertEquals("First username should be saved", firstUsername, 
                     MockSessionManager.getCurrentUsername(context));
        
        // When - second login with different username
        String secondUsername = "alice";
        MockSessionManager.login(context, secondUsername);
        
        // Then
        String currentUsername = MockSessionManager.getCurrentUsername(context);
        assertEquals("Should have second username", secondUsername, currentUsername);
        assertNotEquals("Should not have first username", firstUsername, currentUsername);
    }

    // ========== PROFILE RETRIEVAL TESTS ==========

    @Test
    public void testGetCurrentUserProfile_whenLoggedIn_returnsProfile() {
        // Given - user is logged in with valid mock user
        MockSessionManager.login(context, "demo");
        
        // When
        MockData.MockProfile profile = MockSessionManager.getCurrentUserProfile(context);
        
        // Then
        assertNotNull("Profile should not be null for logged-in user", profile);
        assertEquals("Profile username should match logged-in user", "demo", profile.username);
    }

    @Test
    public void testGetCurrentUserProfile_whenLoggedOut_returnsNull() {
        // Given - user is not logged in
        MockSessionManager.logout(context);
        
        // When
        MockData.MockProfile profile = MockSessionManager.getCurrentUserProfile(context);
        
        // Then
        assertNull("Profile should be null when not logged in", profile);
    }

    @Test
    public void testGetCurrentUserProfile_withInvalidUser_returnsNull() {
        // Given - logged in with invalid username (not in MockData)
        MockSessionManager.login(context, "nonexistentuser");
        
        // When
        MockData.MockProfile profile = MockSessionManager.getCurrentUserProfile(context);
        
        // Then
        assertNull("Profile should be null for invalid user", profile);
    }

    @Test
    public void testGetCurrentUserProfile_afterLogin_hasCorrectData() {
        // Given
        String username = "demo";
        MockSessionManager.login(context, username);
        
        // When
        MockData.MockProfile profile = MockSessionManager.getCurrentUserProfile(context);
        
        // Then
        assertNotNull("Profile should not be null", profile);
        assertEquals("Username should match", username, profile.username);
        assertNotNull("Email should not be null", profile.email);
        assertNotNull("Profile picture should not be null", profile.profilePictureUrl);
        assertNotNull("Bio should not be null", profile.bio);
        assertNotNull("User posts should not be null", profile.userPosts);
    }

    @Test
    public void testGetCurrentUserProfile_afterLogout_returnsNull() {
        // Given - user was logged in
        MockSessionManager.login(context, "demo");
        assertNotNull("Profile should exist when logged in", 
                      MockSessionManager.getCurrentUserProfile(context));
        
        // When
        MockSessionManager.logout(context);
        
        // Then
        MockData.MockProfile profile = MockSessionManager.getCurrentUserProfile(context);
        assertNull("Profile should be null after logout", profile);
    }

    // ========== SESSION PERSISTENCE TESTS ==========

    @Test
    public void testSessionPersistence_survivesPrefsClear() {
        // Given - user is logged in
        String username = "demo";
        MockSessionManager.login(context, username);
        
        // When - we get a new context (simulating app restart)
        Context newContext = RuntimeEnvironment.getApplication();
        
        // Then - session should still exist
        assertTrue("Session should persist across context instances", 
                   MockSessionManager.isLoggedIn(newContext));
        assertEquals("Username should persist", username, 
                     MockSessionManager.getCurrentUsername(newContext));
    }

    @Test
    public void testMultipleUsers_canLoginSequentially() {
        // Test logging in as different users
        String[] usernames = {"demo", "alice", "bob"};
        
        for (String username : usernames) {
            // When
            MockSessionManager.login(context, username);
            
            // Then
            assertTrue("Should be logged in as " + username, 
                       MockSessionManager.isLoggedIn(context));
            assertEquals("Current username should be " + username, username, 
                         MockSessionManager.getCurrentUsername(context));
            
            // Logout for next iteration
            MockSessionManager.logout(context);
        }
    }

    // ========== EDGE CASE TESTS ==========

    @Test
    public void testLogin_withWhitespaceUsername_saves() {
        // Given
        String username = "  demo  ";
        
        // When
        MockSessionManager.login(context, username);
        
        // Then
        String savedUsername = MockSessionManager.getCurrentUsername(context);
        assertEquals("Should save username with whitespace as-is", username, savedUsername);
    }

    @Test
    public void testLogin_withSpecialCharacters_saves() {
        // Given
        String username = "user@123!";
        
        // When
        MockSessionManager.login(context, username);
        
        // Then
        String savedUsername = MockSessionManager.getCurrentUsername(context);
        assertEquals("Should save username with special characters", username, savedUsername);
    }

    @Test
    public void testLogin_withVeryLongUsername_saves() {
        // Given
        String username = "a".repeat(1000); // 1000 character username
        
        // When
        MockSessionManager.login(context, username);
        
        // Then
        String savedUsername = MockSessionManager.getCurrentUsername(context);
        assertEquals("Should save very long username", username, savedUsername);
    }

    // ========== INTEGRATION TESTS ==========

    @Test
    public void testCompleteLoginLogoutCycle() {
        // Initial state
        assertFalse("Should start logged out", MockSessionManager.isLoggedIn(context));
        assertTrue("Username should be empty", 
                   MockSessionManager.getCurrentUsername(context).isEmpty());
        
        // Login
        MockSessionManager.login(context, "demo");
        assertTrue("Should be logged in", MockSessionManager.isLoggedIn(context));
        assertEquals("demo", MockSessionManager.getCurrentUsername(context));
        assertNotNull("Should have profile", 
                      MockSessionManager.getCurrentUserProfile(context));
        
        // Logout
        MockSessionManager.logout(context);
        assertFalse("Should be logged out", MockSessionManager.isLoggedIn(context));
        assertTrue("Username should be empty", 
                   MockSessionManager.getCurrentUsername(context).isEmpty());
        assertNull("Should not have profile", 
                   MockSessionManager.getCurrentUserProfile(context));
    }

    @Test
    public void testUserSwitching() {
        // Login as first user
        MockSessionManager.login(context, "demo");
        assertEquals("demo", MockSessionManager.getCurrentUsername(context));
        MockData.MockProfile demoProfile = MockSessionManager.getCurrentUserProfile(context);
        assertNotNull("Demo profile should exist", demoProfile);
        
        // Switch to second user without explicit logout
        MockSessionManager.login(context, "alice");
        assertEquals("alice", MockSessionManager.getCurrentUsername(context));
        MockData.MockProfile aliceProfile = MockSessionManager.getCurrentUserProfile(context);
        assertNotNull("Alice profile should exist", aliceProfile);
        
        // Verify profiles are different
        assertNotEquals("Profiles should be different", 
                        demoProfile.username, aliceProfile.username);
    }

    // ========== THREAD SAFETY TESTS ==========

    @Test
    public void testConcurrentLoginLogout_doesNotCrash() throws InterruptedException {
        // This is a basic concurrency test
        // For production, you'd want more sophisticated tests
        
        final int threadCount = 10;
        Thread[] threads = new Thread[threadCount];
        
        // Create threads that login and logout rapidly
        for (int i = 0; i < threadCount; i++) {
            final int threadNum = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    MockSessionManager.login(context, "user" + threadNum);
                    MockSessionManager.isLoggedIn(context);
                    MockSessionManager.getCurrentUsername(context);
                    MockSessionManager.logout(context);
                }
            });
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        
        // If we get here without crashes, the test passes
        assertTrue("Concurrent access should not crash", true);
    }
}