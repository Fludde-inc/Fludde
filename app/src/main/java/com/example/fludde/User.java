package com.example.fludde;

import com.parse.ParseUser;
import com.parse.ParseException;

/**
 * User class extending ParseUser
 * No @ParseClassName annotation needed - ParseUser already maps to "_User" table
 */
public class User extends ParseUser {
    
    // Constructor
    public User() {
        super();
    }
    
    // Custom getter and setter methods for additional fields
    
    /**
     * Get user's full name
     */
    public String getFullName() {
        return getString("fullName");
    }
    
    /**
     * Set user's full name
     */
    public void setFullName(String fullName) {
        put("fullName", fullName);
    }
    
    /**
     * Get user's phone number
     */
    public String getPhoneNumber() {
        return getString("phoneNumber");
    }
    
    /**
     * Set user's phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        put("phoneNumber", phoneNumber);
    }
    
    /**
     * Get user's profile picture URL
     */
    public String getProfilePictureUrl() {
        return getString("profilePictureUrl");
    }
    
    /**
     * Set user's profile picture URL
     */
    public void setProfilePictureUrl(String url) {
        put("profilePictureUrl", url);
    }
    
    /**
     * Check if user has completed profile setup
     */
    public boolean isProfileComplete() {
        return getBoolean("profileComplete");
    }
    
    /**
     * Set profile completion status
     */
    public void setProfileComplete(boolean complete) {
        put("profileComplete", complete);
    }
    
    /**
     * Get the current logged-in user as a User object
     */
    public static User getCurrentUser() {
        return (User) ParseUser.getCurrentUser();
    }
}