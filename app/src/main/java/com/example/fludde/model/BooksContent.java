package com.example.fludde.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class BooksContent implements Contents {
    String imageURL;
    String title;
    String summary;
    String author;
    JSONArray isbn13 ;

    public BooksContent(JSONObject jsonObject) throws JSONException {

//        posterPath = jsonObject.getString( "");
//        backdropPath = jsonObject.getString( "backdrop_path");
        title = jsonObject.getString("book_title");
        summary = jsonObject.getString("summary");
        author = jsonObject.getString("book_author");
        isbn13 = jsonObject.getJSONArray("isbn13");
    }

    public static List<BooksContent> fromJsonArray(JSONArray booksJsonArray) throws JSONException {
        List<BooksContent> books = new ArrayList<>();

        for(int i =0; i < booksJsonArray.length(); i++ )
        {
            books.add(new BooksContent(booksJsonArray.getJSONObject(i)));

        }
        return books;
    }
    public String getImageURL(){

        String bookISBN;
        int isbnSize = isbn13.length();

        if (isbnSize >=0)
        {
            for (int j =0; j < isbn13.length();j++)
            {
                bookISBN = isbn13.toString();
                if(bookISBN != null)
                {
                    imageURL = verifyImage(bookISBN);

                }
            }
        }

        return null;
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

    @Override
    public int getType() {
        return Contents.TYPE_BOOKS;
    }
}
