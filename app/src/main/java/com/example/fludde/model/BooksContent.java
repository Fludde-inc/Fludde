package com.example.fludde.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/** Minimal model for the Books tab (title + cover). */
public class BooksContent {
    private final String title;
    private final String imageURL;

    public BooksContent(String title, String imageURL) {
        this.title = title;
        this.imageURL = imageURL;
    }

    public static List<BooksContent> fromJsonArray(JSONArray items) throws JSONException {
        List<BooksContent> out = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.optJSONObject(i);
            if (item == null) continue;

            JSONObject info = item.optJSONObject("volumeInfo");
            if (info == null) continue;

            String title = info.optString("title", "");
            String img = null;

            JSONObject images = info.optJSONObject("imageLinks");
            if (images != null) {
                // Prefer 'thumbnail'; fallback to 'smallThumbnail'
                img = images.optString("thumbnail", null);
                if (img == null) img = images.optString("smallThumbnail", null);
                // Ensure https urls for Glide
                if (img != null && img.startsWith("http://")) {
                    img = img.replace("http://", "https://");
                }
            }

            out.add(new BooksContent(title, img));
        }
        return out;
    }

    public String getTitle() { return title; }
    public String getImageURL() { return imageURL; }
}
