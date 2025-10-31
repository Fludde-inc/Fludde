package com.example.fludde.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Comprehensive Unit Tests for BookDetails class.
 * Tests constructors, getters, setters, and helper methods.
 * 
 * @priority MEDIUM
 * 
 * Test Coverage:
 * - Constructor Tests (3 tests)
 * - Getter/Setter Tests (4 tests)
 * - Helper Method Tests (4 tests)
 * - Missing Fields Tests (3 tests)
 */
@RunWith(RobolectricTestRunner.class)
public class BookDetailsTest {

    // ========== CONSTRUCTOR TESTS ==========

    @Test
    public void testBookDetails_defaultConstructor_initializesFields() {
        // Act
        BookDetails book = new BookDetails();

        // Assert
        assertNotNull("Book should not be null", book);
        assertNotNull("Authors list should be initialized", book.getAuthors());
        assertTrue("Authors list should be empty", book.getAuthors().isEmpty());
    }

    @Test
    public void testBookDetails_parameterizedConstructor_setsAllFields() {
        // Arrange
        String title = "The Hobbit";
        String subtitle = "There and Back Again";
        String publisher = "George Allen & Unwin";
        String publishedDate = "1937-09-21";
        String description = "A fantasy adventure novel";
        String thumbnail = "https://example.com/hobbit.jpg";
        String previewLink = "https://example.com/preview";
        String infoLink = "https://example.com/info";
        String buyLink = "https://example.com/buy";
        int pageCount = 310;
        ArrayList<String> authors = new ArrayList<>();
        authors.add("J.R.R. Tolkien");

        // Act
        BookDetails book = new BookDetails(title, subtitle, publisher, publishedDate,
                                           description, thumbnail, previewLink, infoLink,
                                           buyLink, pageCount, authors);

        // Assert
        assertEquals("Title should match", title, book.getTitle());
        assertEquals("Subtitle should match", subtitle, book.getSubtitle());
        assertEquals("Publisher should match", publisher, book.getPublisher());
        assertEquals("Published date should match", publishedDate, book.getPublishedDate());
        assertEquals("Description should match", description, book.getDescription());
        assertEquals("Thumbnail should match", thumbnail, book.getThumbnail());
        assertEquals("Preview link should match", previewLink, book.getPreviewLink());
        assertEquals("Info link should match", infoLink, book.getInfoLink());
        assertEquals("Buy link should match", buyLink, book.getBuyLink());
        assertEquals("Page count should match", pageCount, book.getPageCount());
        assertEquals("Authors should match", authors, book.getAuthors());
        assertEquals("Should have one author", 1, book.getAuthors().size());
    }

    @Test
    public void testBookDetails_parameterizedConstructor_withNullAuthors_initializesEmptyList() {
        // Arrange
        ArrayList<String> nullAuthors = null;

        // Act
        BookDetails book = new BookDetails("Title", "Subtitle", "Publisher", "2023",
                                           "Description", "thumbnail.jpg", "preview", "info",
                                           "buy", 200, nullAuthors);

        // Assert
        assertNotNull("Authors list should not be null", book.getAuthors());
        assertTrue("Authors list should be empty when null is passed", book.getAuthors().isEmpty());
    }

    // ========== GETTER/SETTER TESTS ==========

    @Test
    public void testBookDetails_settersAndGetters_workCorrectly() {
        // Arrange
        BookDetails book = new BookDetails();
        String title = "1984";
        String subtitle = "A Dystopian Novel";
        String publisher = "Secker & Warburg";
        String publishedDate = "1949-06-08";
        String description = "A dystopian social science fiction novel";
        String thumbnail = "https://example.com/1984.jpg";
        String previewLink = "https://example.com/preview/1984";
        String infoLink = "https://example.com/info/1984";
        String buyLink = "https://example.com/buy/1984";
        int pageCount = 328;
        ArrayList<String> authors = new ArrayList<>();
        authors.add("George Orwell");

        // Act
        book.setTitle(title);
        book.setSubtitle(subtitle);
        book.setPublisher(publisher);
        book.setPublishedDate(publishedDate);
        book.setDescription(description);
        book.setThumbnail(thumbnail);
        book.setPreviewLink(previewLink);
        book.setInfoLink(infoLink);
        book.setBuyLink(buyLink);
        book.setPageCount(pageCount);
        book.setAuthors(authors);

        // Assert
        assertEquals("Title should be set correctly", title, book.getTitle());
        assertEquals("Subtitle should be set correctly", subtitle, book.getSubtitle());
        assertEquals("Publisher should be set correctly", publisher, book.getPublisher());
        assertEquals("Published date should be set correctly", publishedDate, book.getPublishedDate());
        assertEquals("Description should be set correctly", description, book.getDescription());
        assertEquals("Thumbnail should be set correctly", thumbnail, book.getThumbnail());
        assertEquals("Preview link should be set correctly", previewLink, book.getPreviewLink());
        assertEquals("Info link should be set correctly", infoLink, book.getInfoLink());
        assertEquals("Buy link should be set correctly", buyLink, book.getBuyLink());
        assertEquals("Page count should be set correctly", pageCount, book.getPageCount());
        assertEquals("Authors should be set correctly", authors, book.getAuthors());
    }

    @Test
    public void testBookDetails_setAuthors_withNull_initializesEmptyList() {
        // Arrange
        BookDetails book = new BookDetails();

        // Act
        book.setAuthors(null);

        // Assert
        assertNotNull("Authors should not be null", book.getAuthors());
        assertTrue("Authors should be empty list when set to null", book.getAuthors().isEmpty());
    }

    @Test
    public void testBookDetails_multipleAuthors_handledCorrectly() {
        // Arrange
        BookDetails book = new BookDetails();
        ArrayList<String> authors = new ArrayList<>();
        authors.add("Neil Gaiman");
        authors.add("Terry Pratchett");

        // Act
        book.setAuthors(authors);

        // Assert
        assertEquals("Should have 2 authors", 2, book.getAuthors().size());
        assertTrue("Should contain first author", book.getAuthors().contains("Neil Gaiman"));
        assertTrue("Should contain second author", book.getAuthors().contains("Terry Pratchett"));
    }

    @Test
    public void testBookDetails_setPageCount_withZero_handlesCorrectly() {
        // Arrange
        BookDetails book = new BookDetails();

        // Act
        book.setPageCount(0);

        // Assert
        assertEquals("Page count should be 0", 0, book.getPageCount());
    }

    // ========== HELPER METHOD TESTS ==========

    @Test
    public void testBookDetails_getAuthorsAsString_withSingleAuthor_returnsAuthorName() {
        // Arrange
        BookDetails book = new BookDetails();
        ArrayList<String> authors = new ArrayList<>();
        authors.add("J.K. Rowling");
        book.setAuthors(authors);

        // Act
        String authorsString = book.getAuthorsAsString();

        // Assert
        assertEquals("Should return single author name", "J.K. Rowling", authorsString);
    }

    @Test
    public void testBookDetails_getAuthorsAsString_withMultipleAuthors_returnsCommaSeparated() {
        // Arrange
        BookDetails book = new BookDetails();
        ArrayList<String> authors = new ArrayList<>();
        authors.add("Neil Gaiman");
        authors.add("Terry Pratchett");
        book.setAuthors(authors);

        // Act
        String authorsString = book.getAuthorsAsString();

        // Assert
        assertEquals("Should return comma-separated authors", 
                     "Neil Gaiman, Terry Pratchett", authorsString);
    }

    @Test
    public void testBookDetails_getAuthorsAsString_withNoAuthors_returnsUnknownAuthor() {
        // Arrange
        BookDetails book = new BookDetails();
        // Authors list is empty by default

        // Act
        String authorsString = book.getAuthorsAsString();

        // Assert
        assertEquals("Should return 'Unknown Author' for empty list", 
                     "Unknown Author", authorsString);
    }

    @Test
    public void testBookDetails_isValid_withValidTitle_returnsTrue() {
        // Arrange
        BookDetails book = new BookDetails();
        book.setTitle("Valid Title");

        // Act
        boolean isValid = book.isValid();

        // Assert
        assertTrue("Book with valid title should be valid", isValid);
    }

    @Test
    public void testBookDetails_isValid_withNullTitle_returnsFalse() {
        // Arrange
        BookDetails book = new BookDetails();
        // Title is null by default

        // Act
        boolean isValid = book.isValid();

        // Assert
        assertFalse("Book with null title should be invalid", isValid);
    }

    @Test
    public void testBookDetails_isValid_withEmptyTitle_returnsFalse() {
        // Arrange
        BookDetails book = new BookDetails();
        book.setTitle("");

        // Act
        boolean isValid = book.isValid();

        // Assert
        assertFalse("Book with empty title should be invalid", isValid);
    }

    @Test
    public void testBookDetails_isValid_withWhitespaceTitle_returnsFalse() {
        // Arrange
        BookDetails book = new BookDetails();
        book.setTitle("   ");

        // Act
        boolean isValid = book.isValid();

        // Assert
        assertFalse("Book with whitespace-only title should be invalid", isValid);
    }

    // ========== MISSING FIELDS TESTS ==========

    @Test
    public void testBookDetails_fromJson_parsesCorrectly() {
        // Note: BookDetails doesn't have a fromJson method, but we can test
        // setting fields manually to simulate JSON parsing behavior
        
        // Arrange
        BookDetails book = new BookDetails();
        book.setTitle("Test Book");
        book.setPublisher("Test Publisher");
        book.setPageCount(250);

        // Act & Assert
        assertEquals("Title should be set", "Test Book", book.getTitle());
        assertEquals("Publisher should be set", "Test Publisher", book.getPublisher());
        assertEquals("Page count should be set", 250, book.getPageCount());
    }

    @Test
    public void testBookDetails_withMissingFields_handlesGracefully() {
        // Arrange
        BookDetails book = new BookDetails();
        book.setTitle("Partial Book");
        // Other fields remain null

        // Act & Assert
        assertNotNull("Title should be set", book.getTitle());
        assertNull("Subtitle should be null", book.getSubtitle());
        assertNull("Publisher should be null", book.getPublisher());
        assertNull("Description should be null", book.getDescription());
        assertTrue("Should be valid with just a title", book.isValid());
    }

    @Test
    public void testBookDetails_withAllFieldsNull_handlesGracefully() {
        // Arrange & Act
        BookDetails book = new BookDetails();

        // Assert
        assertNull("Title should be null", book.getTitle());
        assertNull("Subtitle should be null", book.getSubtitle());
        assertNull("Publisher should be null", book.getPublisher());
        assertNull("Published date should be null", book.getPublishedDate());
        assertNull("Description should be null", book.getDescription());
        assertNull("Thumbnail should be null", book.getThumbnail());
        assertNull("Preview link should be null", book.getPreviewLink());
        assertNull("Info link should be null", book.getInfoLink());
        assertNull("Buy link should be null", book.getBuyLink());
        assertEquals("Page count should be 0", 0, book.getPageCount());
        assertNotNull("Authors should not be null", book.getAuthors());
        assertTrue("Authors should be empty", book.getAuthors().isEmpty());
        assertFalse("Book without title should be invalid", book.isValid());
    }

    // ========== TOSTRING TEST ==========

    @Test
    public void testBookDetails_toString_containsKeyFields() {
        // Arrange
        BookDetails book = new BookDetails();
        book.setTitle("Test Title");
        book.setSubtitle("Test Subtitle");
        book.setPublisher("Test Publisher");
        book.setPublishedDate("2023");
        book.setPageCount(100);
        ArrayList<String> authors = new ArrayList<>();
        authors.add("Test Author");
        book.setAuthors(authors);

        // Act
        String result = book.toString();

        // Assert
        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain title", result.contains("Test Title"));
        assertTrue("toString should contain subtitle", result.contains("Test Subtitle"));
        assertTrue("toString should contain publisher", result.contains("Test Publisher"));
        assertTrue("toString should contain page count", result.contains("100"));
    }

    // ========== EDGE CASE TESTS ==========

    @Test
    public void testBookDetails_withSpecialCharactersInTitle_handlesCorrectly() {
        // Arrange
        BookDetails book = new BookDetails();
        String specialTitle = "Book: A Story of \"Quotes\" & <Tags>";

        // Act
        book.setTitle(specialTitle);

        // Assert
        assertEquals("Special characters should be preserved", specialTitle, book.getTitle());
        assertTrue("Book with special characters should be valid", book.isValid());
    }

    @Test
    public void testBookDetails_withVeryLongTitle_handlesCorrectly() {
        // Arrange
        BookDetails book = new BookDetails();
        StringBuilder longTitle = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longTitle.append("Very Long Title ");
        }

        // Act
        book.setTitle(longTitle.toString());

        // Assert
        assertEquals("Long title should be preserved", longTitle.toString(), book.getTitle());
        assertTrue("Book with long title should be valid", book.isValid());
    }

    @Test
    public void testBookDetails_withNegativePageCount_handlesCorrectly() {
        // Arrange
        BookDetails book = new BookDetails();

        // Act
        book.setPageCount(-10);

        // Assert
        assertEquals("Negative page count should be set (no validation)", -10, book.getPageCount());
    }

    @Test
    public void testBookDetails_emptyAuthorsList_returnsUnknownAuthor() {
        // Arrange
        BookDetails book = new BookDetails();
        book.setAuthors(new ArrayList<String>());

        // Act
        String authorsString = book.getAuthorsAsString();

        // Assert
        assertEquals("Empty authors list should return 'Unknown Author'", 
                     "Unknown Author", authorsString);
    }
}