package com.example.fludde.netclients;
import com.example.fludde.R;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class NYTClient {


    private final String nyURL = "https://api.nytimes.com/svc/books/v3/lists/best-sellers/history.json?api-key=";

    private AsyncHttpClient nyclient;

    public NYTClient(){this.nyclient = new AsyncHttpClient();}
    private String getNyURL(String key, String par){return nyURL+key+par;}

    public void getNYTop(final String query, JsonHttpResponseHandler handler){
        try{
            String nyKey = "w2uK0OgfCp9kftPXBlb3IS3rv8a3l7sd";
            String par = "&offset=";
            String url = getNyURL(nyKey,par);
            nyclient.get(url+ URLEncoder.encode(query, "utf-8"),handler);

        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }


}
