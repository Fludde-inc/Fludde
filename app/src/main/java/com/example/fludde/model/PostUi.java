package com.example.fludde.model;

/** Minimal UI model so the feed can render with or without Parse. */
public final class PostUi {
    private final String category;
    private final String description;
    private final String title;
    private final String review;
    private final String contentImageUrl;
    private final String userName;
    private final String userImageUrl;

    public PostUi(String category, String description, String title, String review,
                  String contentImageUrl, String userName, String userImageUrl) {
        this.category = nz(category);
        this.description = nz(description);
        this.title = nz(title);
        this.review = nz(review);
        this.contentImageUrl = nz(contentImageUrl);
        this.userName = nz(userName);
        this.userImageUrl = nz(userImageUrl);
    }

    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getTitle() { return title; }
    public String getReview() { return review; }
    public String getContentImageUrl() { return contentImageUrl; }
    public String getUserName() { return userName; }
    public String getUserImageUrl() { return userImageUrl; }

    private static String nz(String s) { return s == null ? "" : s; }
}
