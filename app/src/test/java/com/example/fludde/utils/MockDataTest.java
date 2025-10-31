package com.example.fludde.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Comprehensive Unit Tests for MockData class.
 * Tests mock data generation, validation, and authentication.
 * 
 * @priority HIGH
 * 
 * Test Coverage:
 * - Authentication Tests (7 tests)
 * - Mock User Tests (6 tests)
 * - Mock Data Generation Tests (12 tests)
 * - Mock Profile Tests (4 tests)
 * - Mock Timeline Tests (5 tests)
 * - Data Consistency Tests (2 tests)
 */
@RunWith(RobolectricTestRunner.class)
public class MockDataTest {

    // ========== AUTHENTICATION TESTS ==========

    @Test
    public void testIsValidLogin_withValidCredentials_returnsTrue() {
        // Test known valid credentials
        assertTrue("demo/demo123 should be valid", 
                   MockData.isValidLogin("demo", "demo123"));
        assertTrue("testuser/test123 should be valid", 
                   MockData.isValidLogin("testuser", "test123"));
        assertTrue("alice/alice123 should be valid", 
                   MockData.isValidLogin("alice", "alice123"));
    }

    @Test
    public void testIsValidLogin_withInvalidUsername_returnsFalse() {
        assertFalse("Invalid username should return false", 
                    MockData.isValidLogin("nonexistent", "demo123"));
        assertFalse("Random username should return false", 
                    MockData.isValidLogin("randomuser", "password"));
    }

    @Test
    public void testIsValidLogin_withInvalidPassword_returnsFalse() {
        assertFalse("Wrong password should return false", 
                    MockData.isValidLogin("demo", "wrongpassword"));
    }

    @Test
    public void testIsValidLogin_withNullUsername_returnsFalse() {
        assertFalse("Null username should return false", 
                    MockData.isValidLogin(null, "demo123"));
    }

    @Test
    public void testIsValidLogin_withEmptyPassword_returnsFalse() {
        assertFalse("Empty password should return false", 
                    MockData.isValidLogin("demo", ""));
    }

    @Test
    public void testIsValidLogin_withNullPassword_returnsFalse() {
        assertFalse("Null password should return false", 
                    MockData.isValidLogin("demo", null));
    }

    @Test
    public void testIsValidLogin_withEmptyUsername_returnsFalse() {
        assertFalse("Empty username should return false", 
                    MockData.isValidLogin("", "demo123"));
    }

    @Test
    public void testIsValidLogin_caseSensitive() {
        // Verify that login is case-sensitive
        assertTrue("Exact case should work", 
                   MockData.isValidLogin("demo", "demo123"));
        
        // These should fail if the implementation is case-sensitive
        // If your implementation is case-insensitive, adjust these tests
        assertFalse("Different case username should fail", 
                    MockData.isValidLogin("Demo", "demo123"));
        assertFalse("All caps username should fail", 
                    MockData.isValidLogin("DEMO", "demo123"));
    }

    // ========== MOCK USER TESTS ==========

    @Test
    public void testGetMockUser_validUsername_returnsUser() {
        MockData.MockUser user = MockData.getMockUser("demo");
        assertNotNull("Should return user for valid username", user);
        assertEquals("Username should match", "demo", user.username);
    }

    @Test
    public void testGetMockUser_invalidUsername_returnsNull() {
        MockData.MockUser user = MockData.getMockUser("nonexistent");
        assertNull("Should return null for invalid username", user);
    }

    @Test
    public void testGetMockUser_nullUsername_returnsNull() {
        MockData.MockUser user = MockData.getMockUser(null);
        assertNull("Should return null for null username", user);
    }

    @Test
    public void testGetMockUser_returnsCorrectFields() {
        MockData.MockUser user = MockData.getMockUser("demo");
        assertNotNull("User should not be null", user);
        assertNotNull("Username should not be null", user.username);
        assertNotNull("Email should not be null", user.email);
        assertNotNull("Password should not be null", user.password);
        assertNotNull("Profile picture URL should not be null", user.profilePictureUrl);
        
        // Verify email format
        assertTrue("Email should contain @", user.email.contains("@"));
    }

    @Test
    public void testAllMockUsers_haveValidData() {
        // Test all known mock users
        String[] usernames = {"demo", "testuser", "alice", "bob", "charlie"};
        
        for (String username : usernames) {
            MockData.MockUser user = MockData.getMockUser(username);
            if (user != null) {
                assertNotNull("Username should not be null", user.username);
                assertNotNull("Email should not be null", user.email);
                assertNotNull("Password should not be null", user.password);
                assertFalse("Username should not be empty", user.username.isEmpty());
                assertFalse("Email should not be empty", user.email.isEmpty());
                assertFalse("Password should not be empty", user.password.isEmpty());
                assertTrue("Email should contain @", user.email.contains("@"));
            }
        }
    }

    @Test
    public void testMockUsers_uniqueUsernames() {
        String[] usernames = {"demo", "testuser", "alice", "bob", "charlie"};
        Set<String> usernameSet = new HashSet<>();
        
        for (String username : usernames) {
            MockData.MockUser user = MockData.getMockUser(username);
            if (user != null) {
                assertTrue("Usernames should be unique", usernameSet.add(user.username));
            }
        }
    }

    @Test
    public void testMockUsers_uniqueEmails() {
        String[] usernames = {"demo", "testuser", "alice", "bob", "charlie"};
        Set<String> emailSet = new HashSet<>();
        
        for (String username : usernames) {
            MockData.MockUser user = MockData.getMockUser(username);
            if (user != null) {
                assertTrue("Emails should be unique", emailSet.add(user.email));
            }
        }
    }

    // ========== MOCK DATA GENERATION TESTS - MOVIES ==========

    @Test
    public void testTmdbTrendingJson_returnsValidJson() throws Exception {
        JSONObject json = MockData.tmdbTrendingJson();
        assertNotNull("Should return non-null JSON", json);
        assertTrue("Should have 'results' key", json.has("results"));
    }

    @Test
    public void testTmdbTrendingJson_containsMovies() throws Exception {
        JSONObject json = MockData.tmdbTrendingJson();
        JSONArray results = json.getJSONArray("results");
        assertTrue("Should contain at least one movie", results.length() > 0);
    }

    @Test
    public void testTmdbTrendingJson_moviesHaveRequiredFields() throws Exception {
        JSONObject json = MockData.tmdbTrendingJson();
        JSONArray results = json.getJSONArray("results");
        
        // Test first movie
        JSONObject movie = results.getJSONObject(0);
        assertTrue("Movie should have 'id'", movie.has("id"));
        assertTrue("Movie should have 'title'", movie.has("title"));
        assertTrue("Movie should have 'overview'", movie.has("overview"));
        assertTrue("Movie should have 'poster_path'", movie.has("poster_path"));
        assertTrue("Movie should have 'release_date'", movie.has("release_date"));
        
        // Verify data types
        assertTrue("ID should be a number", movie.get("id") instanceof Integer);
        assertFalse("Title should not be empty", movie.getString("title").isEmpty());
    }

    @Test
    public void testTmdbTrendingJson_hasExpectedMovieCount() throws Exception {
        JSONObject json = MockData.tmdbTrendingJson();
        JSONArray results = json.getJSONArray("results");
        assertTrue("Should have at least 3 movies", results.length() >= 3);
    }

    // ========== MOCK DATA GENERATION TESTS - MUSIC ==========

    @Test
    public void testItunesSearchJson_returnsValidJson() throws Exception {
        JSONObject json = MockData.itunesSearchJson();
        assertNotNull("Should return non-null JSON", json);
        assertTrue("Should have 'results' key", json.has("results"));
    }

    @Test
    public void testItunesSearchJson_containsSongs() throws Exception {
        JSONObject json = MockData.itunesSearchJson();
        JSONArray results = json.getJSONArray("results");
        assertTrue("Should contain at least one song", results.length() > 0);
    }

    @Test
    public void testItunesSearchJson_songsHaveRequiredFields() throws Exception {
        JSONObject json = MockData.itunesSearchJson();
        JSONArray results = json.getJSONArray("results");
        
        // Test first song
        JSONObject song = results.getJSONObject(0);
        assertTrue("Song should have 'trackId'", song.has("trackId"));
        assertTrue("Song should have 'trackName'", song.has("trackName"));
        assertTrue("Song should have 'artistName'", song.has("artistName"));
        assertTrue("Song should have 'artworkUrl100'", song.has("artworkUrl100"));
        assertTrue("Song should have 'previewUrl'", song.has("previewUrl"));
        
        // Verify data types
        assertFalse("Track name should not be empty", song.getString("trackName").isEmpty());
        assertFalse("Artist name should not be empty", song.getString("artistName").isEmpty());
    }

    @Test
    public void testItunesSearchJson_hasExpectedSongCount() throws Exception {
        JSONObject json = MockData.itunesSearchJson();
        JSONArray results = json.getJSONArray("results");
        assertTrue("Should have at least 3 songs", results.length() >= 3);
    }

    // ========== MOCK DATA GENERATION TESTS - BOOKS ==========

    @Test
    public void testGoogleBooksJson_returnsValidJson() throws Exception {
        JSONObject json = MockData.googleBooksJson();
        assertNotNull("Should return non-null JSON", json);
        assertTrue("Should have 'items' key", json.has("items"));
    }

    @Test
    public void testGoogleBooksJson_containsBooks() throws Exception {
        JSONObject json = MockData.googleBooksJson();
        JSONArray items = json.getJSONArray("items");
        assertTrue("Should contain at least one book", items.length() > 0);
    }

    @Test
    public void testGoogleBooksJson_booksHaveRequiredFields() throws Exception {
        JSONObject json = MockData.googleBooksJson();
        JSONArray items = json.getJSONArray("items");
        
        // Test first book
        JSONObject book = items.getJSONObject(0);
        assertTrue("Book should have 'id'", book.has("id"));
        assertTrue("Book should have 'volumeInfo'", book.has("volumeInfo"));
        
        JSONObject volumeInfo = book.getJSONObject("volumeInfo");
        assertTrue("VolumeInfo should have 'title'", volumeInfo.has("title"));
        assertTrue("VolumeInfo should have 'authors'", volumeInfo.has("authors"));
        
        // Verify data types
        assertFalse("Title should not be empty", volumeInfo.getString("title").isEmpty());
        assertTrue("Authors should be an array", volumeInfo.get("authors") instanceof JSONArray);
    }

    @Test
    public void testGoogleBooksJson_hasExpectedBookCount() throws Exception {
        JSONObject json = MockData.googleBooksJson();
        JSONArray items = json.getJSONArray("items");
        assertTrue("Should have at least 5 books", items.length() >= 5);
    }

    // ========== MOCK PROFILE TESTS ==========

    @Test
    public void testMockUserProfile_validUser_returnsProfile() {
        MockData.MockProfile profile = MockData.mockUserProfile("demo");
        assertNotNull("Should return profile for valid user", profile);
        assertEquals("Username should match", "demo", profile.username);
    }

    @Test
    public void testMockUserProfile_invalidUser_returnsNull() {
        MockData.MockProfile profile = MockData.mockUserProfile("nonexistent");
        assertNull("Should return null for invalid user", profile);
    }

    @Test
    public void testMockUserProfile_containsUserPosts() {
        MockData.MockProfile profile = MockData.mockUserProfile("demo");
        assertNotNull("Profile should not be null", profile);
        assertNotNull("User posts should not be null", profile.userPosts);
        assertTrue("User should have at least one post", profile.userPosts.size() > 0);
    }

    @Test
    public void testMockUserProfile_profileHasAllFields() {
        MockData.MockProfile profile = MockData.mockUserProfile("demo");
        assertNotNull("Profile should not be null", profile);
        assertNotNull("Username should not be null", profile.username);
        assertNotNull("Email should not be null", profile.email);
        assertNotNull("Profile picture should not be null", profile.profilePictureUrl);
        assertNotNull("Bio should not be null", profile.bio);
        assertNotNull("User posts should not be null", profile.userPosts);
    }

    // ========== MOCK TIMELINE TESTS ==========

    @Test
    public void testMockTimelinePosts_returnsNonEmptyList() {
        List<MockData.MockPost> posts = MockData.mockTimelinePosts();
        assertNotNull("Timeline posts should not be null", posts);
        assertFalse("Timeline posts should not be empty", posts.isEmpty());
    }

    @Test
    public void testMockTimelinePosts_containsVariedContentTypes() {
        List<MockData.MockPost> posts = MockData.mockTimelinePosts();
        
        Set<String> contentTypes = new HashSet<>();
        for (MockData.MockPost post : posts) {
            contentTypes.add(post.contentType);
        }
        
        // Verify that we have multiple content types
        assertTrue("Should have at least 2 different content types", contentTypes.size() >= 2);
        
        // Common content types should include Movie, Book, or Music
        boolean hasExpectedTypes = contentTypes.contains("Movie") || 
                                   contentTypes.contains("Book") || 
                                   contentTypes.contains("Music");
        assertTrue("Should contain at least one of: Movie, Book, or Music", hasExpectedTypes);
    }

    @Test
    public void testMockTimelinePosts_postsHaveRequiredFields() {
        List<MockData.MockPost> posts = MockData.mockTimelinePosts();
        
        for (MockData.MockPost post : posts) {
            assertNotNull("Post ID should not be null", post.id);
            assertNotNull("Username should not be null", post.username);
            assertNotNull("User image should not be null", post.userImageUrl);
            assertNotNull("Content type should not be null", post.contentType);
            assertNotNull("Title should not be null", post.title);
            assertNotNull("Review text should not be null", post.reviewText);
            
            // Verify non-empty strings
            assertFalse("Post ID should not be empty", post.id.isEmpty());
            assertFalse("Username should not be empty", post.username.isEmpty());
            assertFalse("Content type should not be empty", post.contentType.isEmpty());
            assertFalse("Title should not be empty", post.title.isEmpty());
        }
    }

    @Test
    public void testMockTimelinePosts_postsHaveTimestamps() {
        List<MockData.MockPost> posts = MockData.mockTimelinePosts();
        
        for (MockData.MockPost post : posts) {
            assertNotNull("Timestamp should not be null", post.timestamp);
            assertFalse("Timestamp should not be empty", post.timestamp.isEmpty());
            
            // Basic timestamp format check (should contain "ago" or be a date)
            assertTrue("Timestamp should be a relative time or date", 
                       post.timestamp.contains("ago") || 
                       post.timestamp.contains("min") || 
                       post.timestamp.contains("hour") ||
                       post.timestamp.contains("day"));
        }
    }

    @Test
    public void testMockTimelinePosts_hasMinimumPosts() {
        List<MockData.MockPost> posts = MockData.mockTimelinePosts();
        assertTrue("Timeline should have at least 3 posts", posts.size() >= 3);
    }

    // ========== DATA CONSISTENCY TESTS ==========

    @Test
    public void testAllMockData_jsonDoesNotThrowException() {
        try {
            MockData.tmdbTrendingJson();
            MockData.itunesSearchJson();
            MockData.googleBooksJson();
            // If we get here, no exceptions were thrown
            assertTrue("All JSON methods should execute without exceptions", true);
        } catch (Exception e) {
            fail("Mock data JSON methods should not throw exceptions: " + e.getMessage());
        }
    }

    @Test
    public void testMockDataSources_allReturnData() throws Exception {
        // Verify all data sources return non-empty results
        JSONObject movies = MockData.tmdbTrendingJson();
        JSONObject music = MockData.itunesSearchJson();
        JSONObject books = MockData.googleBooksJson();
        
        assertTrue("Movies should have results", movies.getJSONArray("results").length() > 0);
        assertTrue("Music should have results", music.getJSONArray("results").length() > 0);
        assertTrue("Books should have items", books.getJSONArray("items").length() > 0);
    }
}