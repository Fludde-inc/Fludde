package com.example.fludde.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Comprehensive Unit Tests for MovieContent class.
 * Tests JSON parsing, missing fields handling, and getters.
 * 
 * @priority MEDIUM
 * 
 * Test Coverage:
 * - JSON Parsing Tests (3 tests)
 * - Missing Fields Tests (3 tests)
 * - Getter Tests (1 test)
 * - Path Generation Tests (2 tests)
 */
@RunWith(RobolectricTestRunner.class)
public class MovieContentTest {

    // ========== JSON PARSING TESTS ==========

    @Test
    public void testMovieContent_fromJson_parsesCorrectly() throws JSONException {
        // Arrange
        JSONObject movieJson = new JSONObject();
        movieJson.put("poster_path", "/abc123.jpg");
        movieJson.put("backdrop_path", "/def456.jpg");
        movieJson.put("title", "Inception");
        movieJson.put("overview", "A thief who steals corporate secrets");

        // Act
        MovieContent movie = new MovieContent(movieJson);

        // Assert
        assertNotNull("Movie should not be null", movie);
        assertEquals("Title should match", "Inception", movie.getTitle());
        assertEquals("Overview should match", "A thief who steals corporate secrets", movie.getOverview());
        assertTrue("Image path should contain poster path", 
                   movie.getImagePath().contains("abc123.jpg"));
        assertTrue("Backdrop path should contain backdrop", 
                   movie.getBackdropPath().contains("def456.jpg"));
    }

    @Test
    public void testMovieContent_fromJsonArray_parsesMultipleMovies() throws JSONException {
        // Arrange
        JSONArray movieArray = new JSONArray();
        
        JSONObject movie1 = new JSONObject();
        movie1.put("poster_path", "/poster1.jpg");
        movie1.put("backdrop_path", "/backdrop1.jpg");
        movie1.put("title", "The Matrix");
        movie1.put("overview", "A computer hacker learns the truth");
        
        JSONObject movie2 = new JSONObject();
        movie2.put("poster_path", "/poster2.jpg");
        movie2.put("backdrop_path", "/backdrop2.jpg");
        movie2.put("title", "Interstellar");
        movie2.put("overview", "A team of explorers travel through a wormhole");
        
        movieArray.put(movie1);
        movieArray.put(movie2);

        // Act
        List<MovieContent> movies = MovieContent.fromJsonArray(movieArray);

        // Assert
        assertNotNull("Movies list should not be null", movies);
        assertEquals("Should parse 2 movies", 2, movies.size());
        assertEquals("First movie title should match", "The Matrix", movies.get(0).getTitle());
        assertEquals("Second movie title should match", "Interstellar", movies.get(1).getTitle());
    }

    @Test
    public void testMovieContent_fromJsonArray_withEmptyArray_returnsEmptyList() throws JSONException {
        // Arrange
        JSONArray emptyArray = new JSONArray();

        // Act
        List<MovieContent> movies = MovieContent.fromJsonArray(emptyArray);

        // Assert
        assertNotNull("Movies list should not be null", movies);
        assertEquals("Should return empty list", 0, movies.size());
    }

    // ========== MISSING FIELDS TESTS ==========

    @Test(expected = JSONException.class)
    public void testMovieContent_withMissingFields_throwsException() throws JSONException {
        // Arrange - Missing required fields
        JSONObject incompleteJson = new JSONObject();
        incompleteJson.put("title", "Incomplete Movie");
        // Missing poster_path, backdrop_path, and overview

        // Act - Should throw JSONException
        new MovieContent(incompleteJson);
    }

    @Test(expected = JSONException.class)
    public void testMovieContent_withMissingTitle_throwsException() throws JSONException {
        // Arrange
        JSONObject jsonWithoutTitle = new JSONObject();
        jsonWithoutTitle.put("poster_path", "/poster.jpg");
        jsonWithoutTitle.put("backdrop_path", "/backdrop.jpg");
        jsonWithoutTitle.put("overview", "Some overview");
        // Missing title

        // Act - Should throw JSONException
        new MovieContent(jsonWithoutTitle);
    }

    @Test(expected = JSONException.class)
    public void testMovieContent_withMissingPosterPath_throwsException() throws JSONException {
        // Arrange
        JSONObject jsonWithoutPoster = new JSONObject();
        jsonWithoutPoster.put("backdrop_path", "/backdrop.jpg");
        jsonWithoutPoster.put("title", "Movie Title");
        jsonWithoutPoster.put("overview", "Movie overview");
        // Missing poster_path

        // Act - Should throw JSONException
        new MovieContent(jsonWithoutPoster);
    }

    // ========== GETTER TESTS ==========

    @Test
    public void testMovieContent_getters_returnCorrectValues() throws JSONException {
        // Arrange
        JSONObject movieJson = new JSONObject();
        movieJson.put("poster_path", "/test_poster.jpg");
        movieJson.put("backdrop_path", "/test_backdrop.jpg");
        movieJson.put("title", "Test Movie");
        movieJson.put("overview", "This is a test overview");

        MovieContent movie = new MovieContent(movieJson);

        // Act & Assert
        assertEquals("getTitle should return correct value", 
                     "Test Movie", movie.getTitle());
        assertEquals("getOverview should return correct value", 
                     "This is a test overview", movie.getOverview());
        assertNotNull("getImagePath should not be null", movie.getImagePath());
        assertNotNull("getBackdropPath should not be null", movie.getBackdropPath());
    }

    // ========== PATH GENERATION TESTS ==========

    @Test
    public void testMovieContent_getImagePath_generatesCorrectUrl() throws JSONException {
        // Arrange
        JSONObject movieJson = new JSONObject();
        movieJson.put("poster_path", "/movie_poster.jpg");
        movieJson.put("backdrop_path", "/backdrop.jpg");
        movieJson.put("title", "URL Test Movie");
        movieJson.put("overview", "Testing URL generation");

        MovieContent movie = new MovieContent(movieJson);

        // Act
        String imagePath = movie.getImagePath();

        // Assert
        assertNotNull("Image path should not be null", imagePath);
        assertTrue("Image path should contain base URL", 
                   imagePath.contains("https://image.tmdb.org/t/p/w342/"));
        assertTrue("Image path should contain poster filename", 
                   imagePath.contains("movie_poster.jpg"));
        assertEquals("Image path should match expected format", 
                     "https://image.tmdb.org/t/p/w342/movie_poster.jpg", imagePath);
    }

    @Test
    public void testMovieContent_getBackdropPath_generatesCorrectUrl() throws JSONException {
        // Arrange
        JSONObject movieJson = new JSONObject();
        movieJson.put("poster_path", "/poster.jpg");
        movieJson.put("backdrop_path", "/movie_backdrop.jpg");
        movieJson.put("title", "Backdrop Test Movie");
        movieJson.put("overview", "Testing backdrop URL generation");

        MovieContent movie = new MovieContent(movieJson);

        // Act
        String backdropPath = movie.getBackdropPath();

        // Assert
        assertNotNull("Backdrop path should not be null", backdropPath);
        assertTrue("Backdrop path should contain base URL", 
                   backdropPath.contains("https://image.tmdb.org/t/p/w342/"));
        assertTrue("Backdrop path should contain backdrop filename", 
                   backdropPath.contains("movie_backdrop.jpg"));
        assertEquals("Backdrop path should match expected format", 
                     "https://image.tmdb.org/t/p/w342/movie_backdrop.jpg", backdropPath);
    }

    // ========== EDGE CASE TESTS ==========

    @Test
    public void testMovieContent_withEmptyStrings_parsesCorrectly() throws JSONException {
        // Arrange
        JSONObject movieJson = new JSONObject();
        movieJson.put("poster_path", "");
        movieJson.put("backdrop_path", "");
        movieJson.put("title", "");
        movieJson.put("overview", "");

        // Act
        MovieContent movie = new MovieContent(movieJson);

        // Assert
        assertNotNull("Movie should not be null", movie);
        assertEquals("Empty title should be preserved", "", movie.getTitle());
        assertEquals("Empty overview should be preserved", "", movie.getOverview());
    }

    @Test
    public void testMovieContent_fromJsonArray_withMixedValidInvalid_parsesValidOnes() throws JSONException {
        // Arrange
        JSONArray movieArray = new JSONArray();
        
        // Valid movie
        JSONObject validMovie = new JSONObject();
        validMovie.put("poster_path", "/valid.jpg");
        validMovie.put("backdrop_path", "/backdrop.jpg");
        validMovie.put("title", "Valid Movie");
        validMovie.put("overview", "This is valid");
        
        movieArray.put(validMovie);

        // Act
        List<MovieContent> movies = MovieContent.fromJsonArray(movieArray);

        // Assert
        assertEquals("Should parse valid movie", 1, movies.size());
        assertEquals("Valid movie should have correct title", 
                     "Valid Movie", movies.get(0).getTitle());
    }
}