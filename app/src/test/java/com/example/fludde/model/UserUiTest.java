package com.example.fludde.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

/**
 * Comprehensive Unit Tests for UserUi class.
 * Tests constructor, null/empty handling, and getters.
 * 
 * @priority MEDIUM
 * 
 * Test Coverage:
 * - Constructor Tests (3 tests)
 */
@RunWith(RobolectricTestRunner.class)
public class UserUiTest {

    // ========== CONSTRUCTOR TESTS ==========

    @Test
    public void testUserUi_constructor_setsAllFields() {
        // Arrange
        String username = "john_doe";
        String imageUrl = "https://example.com/avatar.jpg";

        // Act
        UserUi userUi = new UserUi(username, imageUrl);

        // Assert
        assertEquals("Username should match", username, userUi.getUsername());
        assertEquals("Image URL should match", imageUrl, userUi.getImageUrl());
    }

    @Test
    public void testUserUi_withEmptyUsername_handlesGracefully() {
        // Arrange
        String emptyUsername = "";
        String imageUrl = "https://example.com/avatar.jpg";

        // Act
        UserUi userUi = new UserUi(emptyUsername, imageUrl);

        // Assert
        assertNotNull("Username should not be null", userUi.getUsername());
        assertEquals("Empty username should be preserved", "", userUi.getUsername());
        assertEquals("Image URL should match", imageUrl, userUi.getImageUrl());
    }

    @Test
    public void testUserUi_withNullImageUrl_handlesGracefully() {
        // Arrange
        String username = "jane_smith";
        String nullImageUrl = null;

        // Act
        UserUi userUi = new UserUi(username, nullImageUrl);

        // Assert
        assertEquals("Username should match", username, userUi.getUsername());
        assertNotNull("Image URL should not be null", userUi.getImageUrl());
        assertEquals("Null image URL should become empty string", "", userUi.getImageUrl());
    }

    // ========== NULL HANDLING TESTS ==========

    @Test
    public void testUserUi_withBothNullValues_handlesGracefully() {
        // Act
        UserUi userUi = new UserUi(null, null);

        // Assert
        assertNotNull("Username should not be null", userUi.getUsername());
        assertNotNull("Image URL should not be null", userUi.getImageUrl());
        assertEquals("Null username should become empty string", "", userUi.getUsername());
        assertEquals("Null image URL should become empty string", "", userUi.getImageUrl());
    }

    // ========== GETTER TESTS ==========

    @Test
    public void testUserUi_getters_returnCorrectValues() {
        // Arrange
        String username = "alice_wonder";
        String imageUrl = "https://example.com/profile.png";
        UserUi userUi = new UserUi(username, imageUrl);

        // Act & Assert
        assertEquals("getUsername should return correct value", username, userUi.getUsername());
        assertEquals("getImageUrl should return correct value", imageUrl, userUi.getImageUrl());
    }

    // ========== EDGE CASE TESTS ==========

    @Test
    public void testUserUi_withWhitespaceUsername_preservesWhitespace() {
        // Arrange
        String whitespaceUsername = "   user   ";
        String imageUrl = "https://example.com/avatar.jpg";

        // Act
        UserUi userUi = new UserUi(whitespaceUsername, imageUrl);

        // Assert
        assertEquals("Whitespace in username should be preserved", 
                     whitespaceUsername, userUi.getUsername());
    }

    @Test
    public void testUserUi_withSpecialCharactersInUsername_handlesCorrectly() {
        // Arrange
        String specialUsername = "user@123!#$";
        String imageUrl = "https://example.com/avatar.jpg";

        // Act
        UserUi userUi = new UserUi(specialUsername, imageUrl);

        // Assert
        assertEquals("Special characters should be preserved", 
                     specialUsername, userUi.getUsername());
    }

    @Test
    public void testUserUi_withEmptyImageUrl_handlesGracefully() {
        // Arrange
        String username = "user123";
        String emptyImageUrl = "";

        // Act
        UserUi userUi = new UserUi(username, emptyImageUrl);

        // Assert
        assertEquals("Username should match", username, userUi.getUsername());
        assertEquals("Empty image URL should be preserved", "", userUi.getImageUrl());
    }
}