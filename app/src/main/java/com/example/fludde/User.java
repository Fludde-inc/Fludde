package com.example.fludde;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

//User class has column image therefore need method to retrieve since its none standard
@ParseClassName("_User")
public class User extends ParseUser {

    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_EMAIL = "email";


    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }
    public String getEmail(){return getString(KEY_EMAIL);}
    public void setImage(ParseFile parseFile){
        put(KEY_IMAGE, parseFile);
    }


}
