package com.example.fludde.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BooksContent {
    String imageURL;
    String title;
    String summary;
    String author;
    String isbn;
    JSONArray isbn13 ;
    final String googleUrl ="https://storage.googleapis.com/du-prd/books/images/";
    final String coverFormat = ".jpg";


    public BooksContent(JSONObject jsonObject) throws JSONException {

        if (jsonObject.has("book_title"))
        {
            title = jsonObject.getString("book_title");
            summary = jsonObject.getString("summary");
            author = jsonObject.getString("book_author");
            isbn13 = jsonObject.getJSONArray("isbn13");


            for (int i = 0; i< isbn13.length(); i++ ){
                if( isbn13.length() > 0){
                    testIsbnImage(i);
                }
                   isbn = isbn13.getString(0);

//                isbn =  i


            }

        imageURL = getImageFromIsbn();

        }
        else{
            title = jsonObject.getString("title");
            summary = jsonObject.getString("description");
            author = jsonObject.getString("author");
            imageURL = jsonObject.getString("book_image");
        }



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


//    public String getIsbn13(JSONArray isbn13) throws JSONException {
//
//      int isbnNum = 0;
//
//        for (int i = 0; i< isbn13.length(); i++ ){
//            if( isbn13.length() > 0){
//                testIsbnImage(i);
//            }
//        JSONObject   isbnObj = isbn13.getJSONObject(0);
//
//
//        }
//    isbn =  String.format(String.valueOf(isbnNum));
//        return isbn;
//    }

    private void testIsbnImage(int i) {



//        imageURL = testImage;

    }
    private String getImageFromIsbn() {
        String testImage =String.format("https://storage.googleapis.com/du-prd/books/images/%s.jpg",isbn);

        return testImage;
    }

//    public Boolean testImage(String url){
//        try {
//            BufferedImage image = ImageIO.read(new URL(url));
//            //BufferedImage image = ImageIO.read(new URL("http://someimage.jpg"));
//            if(image != null){
//                return true;
//            } else{
//                return false;
//            }
//
//        } catch (MalformedURLException e) {
//            // TODO Auto-generated catch block
//            System.err.println("URL error with image");
//            e.printStackTrace();
//            return false;
//        } catch (IOException e) {
//            System.err.println("IO error with image");
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            return false;
//        }
//        return false;
//    }

}
