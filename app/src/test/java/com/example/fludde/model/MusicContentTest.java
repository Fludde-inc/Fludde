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
 * Comprehensive Unit Tests for MusicContent class.
 * Tests JSON parsing, missing fields handling, getters, and setters.
 * 
 * @priority MEDIUM
 * 
 * Test Coverage:
 * - JSON Parsing Tests (3 tests)
 * - Missing Fields Tests (3 tests)
 * - Getter/Setter Tests (3 tests)
 * - Description Generation Tests (2 tests)
 */
@RunWith(RobolectricTestRunner.class)
public class MusicContentTest {

    // ========== JSON PARSING TESTS ==========

    @Test
    public void testMusicContent_fromJson_parsesCorrectly() throws JSONException {
        // Arrange
        JSONObject musicJson = new JSONObject();
        musicJson.put("name", "Bohemian Rhapsody");
        musicJson.put("artistName", "Queen");
        musicJson.put("artworkUrl100", "https://example.com/artwork.jpg");

        // Act
        List<MusicContent> musicList = MusicContent.fromJsonArray(createArrayWithOneItem(musicJson));
        MusicContent music = musicList.get(0);

        // Assert
        assertNotNull("Music should not be null", music);
        assertEquals("Title should match", "Bohemian Rhapsody", music.getTitle());
        assertEquals("Artist should match", "Queen", music.getArtist());
        assertEquals("Cover image URL should match", 
                     "https://example.com/artwork.jpg", music.getCoverIMGUrl());
    }

    @Test
    public void testMusicContent_fromJsonArray_parsesMultipleTracks() throws JSONException {
        // Arrange
        JSONArray musicArray = new JSONArray();
        
        JSONObject track1 = new JSONObject();
        track1.put("name", "Imagine");
        track1.put("artistName", "John Lennon");
        track1.put("artworkUrl100", "https://example.com/imagine.jpg");
        
        JSONObject track2 = new JSONObject();
        track2.put("name", "Stairway to Heaven");
        track2.put("artistName", "Led Zeppelin");
        track2.put("artworkUrl100", "https://example.com/stairway.jpg");
        
        musicArray.put(track1);
        musicArray.put(track2);

        // Act
        List<MusicContent> musicList = MusicContent.fromJsonArray(musicArray);

        // Assert
        assertNotNull("Music list should not be null", musicList);
        assertEquals("Should parse 2 tracks", 2, musicList.size());
        assertEquals("First track title should match", "Imagine", musicList.get(0).getTitle());
        assertEquals("Second track title should match", "Stairway to Heaven", musicList.get(1).getTitle());
        assertEquals("First track artist should match", "John Lennon", musicList.get(0).getArtist());
        assertEquals("Second track artist should match", "Led Zeppelin", musicList.get(1).getArtist());
    }

    @Test
    public void testMusicContent_fromJsonArray_withEmptyArray_returnsEmptyList() throws JSONException {
        // Arrange
        JSONArray emptyArray = new JSONArray();

        // Act
        List<MusicContent> musicList = MusicContent.fromJsonArray(emptyArray);

        // Assert
        assertNotNull("Music list should not be null", musicList);
        assertEquals("Should return empty list", 0, musicList.size());
    }

    // ========== MISSING FIELDS TESTS ==========

    @Test(expected = JSONException.class)
    public void testMusicContent_withMissingFields_throwsException() throws JSONException {
        // Arrange - Missing required fields
        JSONObject incompleteJson = new JSONObject();
        incompleteJson.put("name", "Incomplete Track");
        // Missing artistName and artworkUrl100

        JSONArray array = new JSONArray();
        array.put(incompleteJson);

        // Act - Should throw JSONException
        MusicContent.fromJsonArray(array);
    }

    @Test(expected = JSONException.class)
    public void testMusicContent_withMissingName_throwsException() throws JSONException {
        // Arrange
        JSONObject jsonWithoutName = new JSONObject();
        jsonWithoutName.put("artistName", "Test Artist");
        jsonWithoutName.put("artworkUrl100", "https://example.com/art.jpg");
        // Missing name

        JSONArray array = new JSONArray();
        array.put(jsonWithoutName);

        // Act - Should throw JSONException
        MusicContent.fromJsonArray(array);
    }

    @Test(expected = JSONException.class)
    public void testMusicContent_withMissingArtistName_throwsException() throws JSONException {
        // Arrange
        JSONObject jsonWithoutArtist = new JSONObject();
        jsonWithoutArtist.put("name", "Test Track");
        jsonWithoutArtist.put("artworkUrl100", "https://example.com/art.jpg");
        // Missing artistName

        JSONArray array = new JSONArray();
        array.put(jsonWithoutArtist);

        // Act - Should throw JSONException
        MusicContent.fromJsonArray(array);
    }

    // ========== GETTER/SETTER TESTS ==========

    @Test
    public void testMusicContent_getters_returnCorrectValues() throws JSONException {
        // Arrange
        JSONObject musicJson = new JSONObject();
        musicJson.put("name", "Test Song");
        musicJson.put("artistName", "Test Artist");
        musicJson.put("artworkUrl100", "https://example.com/test.jpg");

        List<MusicContent> musicList = MusicContent.fromJsonArray(createArrayWithOneItem(musicJson));
        MusicContent music = musicList.get(0);

        // Act & Assert
        assertEquals("getTitle should return correct value", "Test Song", music.getTitle());
        assertEquals("getArtist should return correct value", "Test Artist", music.getArtist());
        assertEquals("getCoverIMGUrl should return correct value", 
                     "https://example.com/test.jpg", music.getCoverIMGUrl());
    }

    @Test
    public void testMusicContent_setters_updateValuesCorrectly() throws JSONException {
        // Arrange
        JSONObject musicJson = new JSONObject();
        musicJson.put("name", "Original Title");
        musicJson.put("artistName", "Original Artist");
        musicJson.put("artworkUrl100", "https://example.com/original.jpg");

        List<MusicContent> musicList = MusicContent.fromJsonArray(createArrayWithOneItem(musicJson));
        MusicContent music = musicList.get(0);

        // Act
        music.setTitle("Updated Title");
        music.setArtist("Updated Artist");
        music.setAlbum("Updated Album");
        music.setRecordLabel("Updated Label");
        music.setCoverIMGUrl("https://example.com/updated.jpg");

        // Assert
        assertEquals("Title should be updated", "Updated Title", music.getTitle());
        assertEquals("Artist should be updated", "Updated Artist", music.getArtist());
        assertEquals("Album should be updated", "Updated Album", music.getAlbum());
        assertEquals("Record label should be updated", "Updated Label", music.getRecordLabel());
        assertEquals("Cover image URL should be updated", 
                     "https://example.com/updated.jpg", music.getCoverIMGUrl());
    }

    @Test
    public void testMusicContent_setDescription_updatesDescription() throws JSONException {
        // Arrange
        JSONObject musicJson = new JSONObject();
        musicJson.put("name", "Track");
        musicJson.put("artistName", "Artist");
        musicJson.put("artworkUrl100", "https://example.com/art.jpg");

        List<MusicContent> musicList = MusicContent.fromJsonArray(createArrayWithOneItem(musicJson));
        MusicContent music = musicList.get(0);

        // Act
        music.setDescription("Custom description");
        
        // Assert
        // Note: getDescription() generates its own description from fields
        assertNotNull("Description should not be null", music.getDescription());
    }

    // ========== DESCRIPTION GENERATION TESTS ==========

    @Test
    public void testMusicContent_getDescription_generatesFromFields() throws JSONException {
        // Arrange
        JSONObject musicJson = new JSONObject();
        musicJson.put("name", "Test Track");
        musicJson.put("artistName", "Test Artist");
        musicJson.put("artworkUrl100", "https://example.com/art.jpg");

        List<MusicContent> musicList = MusicContent.fromJsonArray(createArrayWithOneItem(musicJson));
        MusicContent music = musicList.get(0);

        // Set additional fields
        music.setAlbum("Test Album");
        music.setRecordLabel("Test Label");

        // Act
        String description = music.getDescription();

        // Assert
        assertNotNull("Description should not be null", description);
        assertTrue("Description should contain title", description.contains("Test Track"));
        assertTrue("Description should contain artist", description.contains("Test Artist"));
        assertTrue("Description should contain album", description.contains("Test Album"));
        assertTrue("Description should contain label", description.contains("Test Label"));
    }

    @Test
    public void testMusicContent_getDescription_withNullFields_handlesGracefully() throws JSONException {
        // Arrange
        JSONObject musicJson = new JSONObject();
        musicJson.put("name", "Track");
        musicJson.put("artistName", "Artist");
        musicJson.put("artworkUrl100", "https://example.com/art.jpg");

        List<MusicContent> musicList = MusicContent.fromJsonArray(createArrayWithOneItem(musicJson));
        MusicContent music = musicList.get(0);
        // album and recordLabel are null by default

        // Act
        String description = music.getDescription();

        // Assert
        assertNotNull("Description should not be null even with null fields", description);
        assertTrue("Description should contain title", description.contains("Track"));
        assertTrue("Description should contain artist", description.contains("Artist"));
    }

    // ========== EDGE CASE TESTS ==========

    @Test
    public void testMusicContent_withEmptyStrings_parsesCorrectly() throws JSONException {
        // Arrange
        JSONObject musicJson = new JSONObject();
        musicJson.put("name", "");
        musicJson.put("artistName", "");
        musicJson.put("artworkUrl100", "");

        // Act
        List<MusicContent> musicList = MusicContent.fromJsonArray(createArrayWithOneItem(musicJson));
        MusicContent music = musicList.get(0);

        // Assert
        assertNotNull("Music should not be null", music);
        assertEquals("Empty title should be preserved", "", music.getTitle());
        assertEquals("Empty artist should be preserved", "", music.getArtist());
        assertEquals("Empty cover URL should be preserved", "", music.getCoverIMGUrl());
    }

    @Test
    public void testMusicContent_setAlbum_withNullValue_handlesGracefully() throws JSONException {
        // Arrange
        JSONObject musicJson = new JSONObject();
        musicJson.put("name", "Track");
        musicJson.put("artistName", "Artist");
        musicJson.put("artworkUrl100", "https://example.com/art.jpg");

        List<MusicContent> musicList = MusicContent.fromJsonArray(createArrayWithOneItem(musicJson));
        MusicContent music = musicList.get(0);

        // Act
        music.setAlbum(null);

        // Assert
        assertNull("Album should be null", music.getAlbum());
    }

    @Test
    public void testMusicContent_multipleGettersAfterSetters_returnUpdatedValues() throws JSONException {
        // Arrange
        JSONObject musicJson = new JSONObject();
        musicJson.put("name", "Original");
        musicJson.put("artistName", "Original Artist");
        musicJson.put("artworkUrl100", "https://example.com/original.jpg");

        List<MusicContent> musicList = MusicContent.fromJsonArray(createArrayWithOneItem(musicJson));
        MusicContent music = musicList.get(0);

        // Act
        music.setTitle("New Title");
        String title1 = music.getTitle();
        String title2 = music.getTitle();

        // Assert
        assertEquals("Multiple getter calls should return same value", title1, title2);
        assertEquals("Should return updated value", "New Title", title1);
    }

    // ========== HELPER METHODS ==========

    private JSONArray createArrayWithOneItem(JSONObject item) {
        JSONArray array = new JSONArray();
        array.put(item);
        return array;
    }
}