package com.example.fludde.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MusicContent {

    private String title;
    private  String artist;
    private String album;
    private String description;
    private String recordLabel;
    private String coverIMGUrl;

    private MusicContent(JSONObject jsonObject) throws JSONException{
        title = jsonObject.getString("name");
        artist = jsonObject.getString("artistName");
//        album = jsonObject.getString("")
       // recordLabel =
        coverIMGUrl = jsonObject.getString("artworkUrl100");

    }
    public static List<MusicContent> fromJsonArray(JSONArray results) throws JSONException {
        List<MusicContent> musics = new ArrayList<>();

        for(int i =0; i < results.length(); i++ )
        {
            musics.add(new MusicContent(results.getJSONObject(i)));

        }
        return musics;
    }

    public String getCoverIMGUrl() {
        return coverIMGUrl;
    }

    public void setCoverIMGUrl(String coverIMGUrl) {
        this.coverIMGUrl = coverIMGUrl;
    }





    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDescription() {

       description = String.format("%s %s %s %s",title, artist, album, recordLabel);

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public String getRecordLabel() {
        return recordLabel;
    }

    public void setRecordLabel(String recordLabel) {
        this.recordLabel = recordLabel;
    }
}
