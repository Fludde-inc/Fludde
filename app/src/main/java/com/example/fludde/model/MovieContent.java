package com.example.fludde.model;

import com.example.fludde.adapters.MovieChildAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MovieContent {
    String posterPath;
    String backdropPath;
    String title;
    String overview;


    public MovieContent(JSONObject jsonObject) throws JSONException {
        posterPath = jsonObject.getString( "poster_path");
        backdropPath = jsonObject.getString( "backdrop_path");
        title = jsonObject.getString("title");
        overview = jsonObject.getString("overview");
    }

    public static List<MovieContent> fromJsonArray(JSONArray movieJsonArray) throws JSONException {
        List<MovieContent> movies = new ArrayList<>();

        for(int i =0; i < movieJsonArray.length(); i++ )
        {
            movies.add(new MovieContent(movieJsonArray.getJSONObject(i)));

        }
        return movies;
    }


    public String getImagePath() {
        return String.format("https://image.tmdb.org/t/p/w342%s", posterPath);
    }

    public String getBackdropPath(){
        return String.format("https://image.tmdb.org/t/p/w342%s", backdropPath);
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }


}

