package com.example.fludde.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class BooksContent {
    String imageURL;
    String title;
    String summary;
    String author;
    JSONArray isbn13 ;

    public BooksContent(JSONObject jsonObject) throws JSONException {


        title = jsonObject.getString("title");
        summary = jsonObject.getString("description");
        author = jsonObject.getString("author");
        imageURL = jsonObject.getString("book_image");
//        isbn13 = jsonObject.getJSONArray("isbn13");
    }


    public static List<BooksContent> fromJsonArray(JSONArray booksJsonArray) throws JSONException {
        List<BooksContent> allbooks = new ArrayList<>();

        for(int i =0; i < booksJsonArray.length(); i++ )
        {
//            JSONObject resultobj = booksJsonArray.getJSONObject(i);
//            JSONObject bookObj = resultobj.getJSONObject("books");


            allbooks.add(new BooksContent(booksJsonArray.getJSONObject(i)));
        }
        return allbooks;
    }



    public String getImageURL(){
        return imageURL;
    }

    private String verifyImage(String bookISBN) {
        String bookURL;

        bookURL = String.format("https://storage.googleapis.com/du-prd/books/images/%s",bookISBN,".jpg");


        return  bookURL;
    }

    public String getTitle() {
        return title;
    }
    public String getSummary(){return summary;}
    public String getAuthor(){return author;}


}
