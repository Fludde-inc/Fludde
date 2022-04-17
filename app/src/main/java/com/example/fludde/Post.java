package com.example.fludde;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {

    User user = new User();
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_CONTENT_IMAGE = "contentImage";
    public static final String KEY_USER = "user";
    public  static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_IMAGE = "userImage";
    public static final String KEY_CONTENT_TITLE ="contentTitle";
    public static final String KEY_CATEGORY = "category";
    public  static final String KEY_REVIEW = "review";

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public ParseFile getContentImage() {
        return getParseFile(KEY_CONTENT_IMAGE);
    }
    public ParseFile getUserImage(){

        return getParseFile(KEY_IMAGE);
    }


    public String getCategory(){return getString(KEY_CATEGORY);}
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }
    public String getReview(){return getString(KEY_REVIEW);}
    public String getCreateAt(){return getString(KEY_CREATED_AT);}
    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }
    public void setContentImage(ParseFile parseFile){
        put(KEY_CONTENT_IMAGE, parseFile);
    }
    public void setUser(ParseUser user){
        put(KEY_USER,user);
    }
    public void setReview(String review){put(KEY_REVIEW,review);}
    public void setCategory(String category){put(KEY_CATEGORY,category);}
    public void setContentTitle(String contentTitle){put(KEY_CONTENT_TITLE,contentTitle);}
    public String getContentTitle() {
        return getString( KEY_CONTENT_TITLE);
    }




}
