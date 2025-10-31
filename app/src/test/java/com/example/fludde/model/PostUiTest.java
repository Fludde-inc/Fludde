package com.example.fludde.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

/**
 * Comprehensive Unit Tests for PostUi class.
 * Tests constructor, null handling, getters, equals, and hashCode.
 * 
 * @priority MEDIUM
 * 
 * Test Coverage:
 * - Constructor Tests (2 tests)
 * - Getter Tests (1 test)
 * - Equals Tests (1 test)
 * - HashCode Tests (1 test)
 */
@RunWith(RobolectricTestRunner.class)
public class PostUiTest {

    // ========== CONSTRUCTOR TESTS ==========

    @Test
    public void testPostUi_constructor_setsAllFields() {
        // Arrange
        String category = "Movies";
        String description = "A great sci-fi film";
        String title = "Interstellar";
        String review = "Amazing visuals and story";
        String contentImageUrl = "https://example.com/poster.jpg";
        String userName = "john_doe";
        String userImageUrl = "https://example.com/avatar.jpg";

        // Act
        PostUi postUi = new PostUi(category, description, title, review, 
                                    contentImageUrl, userName, userImageUrl);

        // Assert
        assertEquals("Category should match", category, postUi.getCategory());
        assertEquals("Description should match", description, postUi.getDescription());
        assertEquals("Title should match", title, postUi.getTitle());
        assertEquals("Review should match", review, postUi.getReview());
        assertEquals("Content image URL should match", contentImageUrl, postUi.getContentImageUrl());
        assertEquals("User name should match", userName, postUi.getUserName());
        assertEquals("User image URL should match", userImageUrl, postUi.getUserImageUrl());
    }

    @Test
    public void testPostUi_withNullValues_handlesGracefully() {
        // Act - All null values should be converted to empty strings
        PostUi postUi = new PostUi(null, null, null, null, null, null, null);

        // Assert - All fields should be empty strings, not null
        assertNotNull("Category should not be null", postUi.getCategory());
        assertNotNull("Description should not be null", postUi.getDescription());
        assertNotNull("Title should not be null", postUi.getTitle());
        assertNotNull("Review should not be null", postUi.getReview());
        assertNotNull("Content image URL should not be null", postUi.getContentImageUrl());
        assertNotNull("User name should not be null", postUi.getUserName());
        assertNotNull("User image URL should not be null", postUi.getUserImageUrl());
        
        assertEquals("Category should be empty string", "", postUi.getCategory());
        assertEquals("Description should be empty string", "", postUi.getDescription());
        assertEquals("Title should be empty string", "", postUi.getTitle());
        assertEquals("Review should be empty string", "", postUi.getReview());
        assertEquals("Content image URL should be empty string", "", postUi.getContentImageUrl());
        assertEquals("User name should be empty string", "", postUi.getUserName());
        assertEquals("User image URL should be empty string", "", postUi.getUserImageUrl());
    }

    // ========== GETTER TESTS ==========

    @Test
    public void testPostUi_getters_returnCorrectValues() {
        // Arrange
        String category = "Books";
        String description = "A fantasy epic";
        String title = "The Hobbit";
        String review = "An adventure for all ages";
        String contentImageUrl = "https://example.com/book.jpg";
        String userName = "jane_smith";
        String userImageUrl = "https://example.com/user.jpg";

        PostUi postUi = new PostUi(category, description, title, review, 
                                    contentImageUrl, userName, userImageUrl);

        // Act & Assert
        assertEquals("getCategory should return correct value", category, postUi.getCategory());
        assertEquals("getDescription should return correct value", description, postUi.getDescription());
        assertEquals("getTitle should return correct value", title, postUi.getTitle());
        assertEquals("getReview should return correct value", review, postUi.getReview());
        assertEquals("getContentImageUrl should return correct value", contentImageUrl, postUi.getContentImageUrl());
        assertEquals("getUserName should return correct value", userName, postUi.getUserName());
        assertEquals("getUserImageUrl should return correct value", userImageUrl, postUi.getUserImageUrl());
    }

    // ========== EQUALS TESTS ==========

    @Test
    public void testPostUi_equals_worksCorrectly() {
        // Arrange
        PostUi post1 = new PostUi("Movies", "Description", "Title", "Review", 
                                   "content.jpg", "user1", "avatar.jpg");
        PostUi post2 = new PostUi("Movies", "Description", "Title", "Review", 
                                   "content.jpg", "user1", "avatar.jpg");
        PostUi post3 = new PostUi("Books", "Different", "Other", "Another", 
                                   "other.jpg", "user2", "other_avatar.jpg");

        // Note: PostUi doesn't override equals(), so it uses reference equality
        // This tests the default Object.equals() behavior
        
        // Assert
        assertTrue("Object should equal itself", post1.equals(post1));
        assertFalse("Different instances should not be equal (default behavior)", post1.equals(post2));
        assertFalse("Different values should not be equal", post1.equals(post3));
        assertFalse("Should not equal null", post1.equals(null));
        assertFalse("Should not equal different type", post1.equals("String"));
    }

    // ========== HASHCODE TESTS ==========

    @Test
    public void testPostUi_hashCode_consistent() {
        // Arrange
        PostUi postUi = new PostUi("Music", "Description", "Title", "Review", 
                                    "content.jpg", "user1", "avatar.jpg");

        // Act
        int hashCode1 = postUi.hashCode();
        int hashCode2 = postUi.hashCode();

        // Assert
        assertEquals("HashCode should be consistent across multiple calls", 
                     hashCode1, hashCode2);
        
        // Note: PostUi doesn't override hashCode(), so it uses Object.hashCode()
        // which is based on memory address and should be consistent for the same object
        assertTrue("HashCode should be non-zero", hashCode1 != 0);
    }
}