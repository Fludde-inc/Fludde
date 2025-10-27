package com.example.fludde.model;

import java.util.ArrayList;

/**
 * Model class representing detailed information about a book.
 * This class contains only data fields (no UI components).
 */
public class BookDetails {

    // Book information fields
    private String title;
    private String subtitle;
    private String publisher;
    private String publishedDate;
    private String description;
    private String thumbnail;
    private String previewLink;
    private String infoLink;
    private String buyLink;
    private int pageCount;
    private ArrayList<String> authors;

    // Default constructor
    public BookDetails() {
        this.authors = new ArrayList<>();
    }

    // Constructor with all fields
    public BookDetails(String title, String subtitle, String publisher, String publishedDate,
                       String description, String thumbnail, String previewLink, 
                       String infoLink, String buyLink, int pageCount, ArrayList<String> authors) {
        this.title = title;
        this.subtitle = subtitle;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
        this.thumbnail = thumbnail;
        this.previewLink = previewLink;
        this.infoLink = infoLink;
        this.buyLink = buyLink;
        this.pageCount = pageCount;
        this.authors = authors != null ? authors : new ArrayList<>();
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getPreviewLink() {
        return previewLink;
    }

    public String getInfoLink() {
        return infoLink;
    }

    public String getBuyLink() {
        return buyLink;
    }

    public int getPageCount() {
        return pageCount;
    }

    public ArrayList<String> getAuthors() {
        return authors;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setPreviewLink(String previewLink) {
        this.previewLink = previewLink;
    }

    public void setInfoLink(String infoLink) {
        this.infoLink = infoLink;
    }

    public void setBuyLink(String buyLink) {
        this.buyLink = buyLink;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public void setAuthors(ArrayList<String> authors) {
        this.authors = authors != null ? authors : new ArrayList<>();
    }

    // Helper method to get formatted author names
    public String getAuthorsAsString() {
        if (authors == null || authors.isEmpty()) {
            return "Unknown Author";
        }
        return String.join(", ", authors);
    }

    // Helper method to check if book has valid data
    public boolean isValid() {
        return title != null && !title.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "BookDetails{" +
                "title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", publisher='" + publisher + '\'' +
                ", publishedDate='" + publishedDate + '\'' +
                ", pageCount=" + pageCount +
                ", authors=" + authors +
                '}';
    }
}