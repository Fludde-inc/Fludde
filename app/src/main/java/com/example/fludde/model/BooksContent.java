package com.example.fludde.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal model for Google Books "volumes" response used by the carousel.
 */
public class BooksContent {
    private final String title;
    private final String imageURL;

    public BooksContent(String title, String imageURL) {
        this.title = title != null ? title : "";
        this.imageURL = imageURL;
    }

    public String getTitle() { return title; }
    public String getImageURL() { return imageURL; }

    /**
     * Parse a Google Books "items" array.
     */
    public static List<BooksContent> fromGoogleBooks(JSONArray items) {
        List<BooksContent> out = new ArrayList<>();
        if (items == null) return out;

        for (int i = 0; i < items.length(); i++) {
            try {
                JSONObject item = items.optJSONObject(i);
                if (item == null) continue;
                JSONObject vol = item.optJSONObject("volumeInfo");
                if (vol == null) continue;

                String title = vol.optString("title", "");
                String thumb = null;
                JSONObject imgs = vol.optJSONObject("imageLinks");
                if (imgs != null) {
                    // Prefer medium/thumbnail if available
                    thumb = coalesce(
                            imgs.optString("medium", null),
                            imgs.optString("thumbnail", null),
                            imgs.optString("smallThumbnail", null)
                    );
                    // Normalize http → https for mixed content issues
                    if (thumb != null && thumb.startsWith("http://")) {
                        thumb = "https://" + thumb.substring("http://".length());
                    }
                }
                out.add(new BooksContent(title, thumb));
            } catch (Exception ignore) { }
        }
        return out;
    }

    private static String coalesce(String a, String b, String c) {
        return a != null ? a : (b != null ? b : c);
    }
}
