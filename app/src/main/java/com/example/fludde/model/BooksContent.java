package com.example.fludde.model;

public class BooksContent {
    private final String title;
    private final String imageURL;

    public BooksContent(String title, String imageURL) {
        this.title = title;
        this.imageURL = imageURL;
    }

    public String getTitle() {
        return title;
    }

    public String getImageURL() {
        return imageURL;
    }
}
