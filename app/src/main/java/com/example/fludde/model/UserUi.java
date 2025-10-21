package com.example.fludde.model;

/** Minimal UI model for search results so we don't depend on Parse in mock mode. */
public final class UserUi {
    private final String username;
    private final String imageUrl;

    public UserUi(String username, String imageUrl) {
        this.username = username == null ? "" : username;
        this.imageUrl = imageUrl == null ? "" : imageUrl;
    }

    public String getUsername() { return username; }
    public String getImageUrl() { return imageUrl; }
}
