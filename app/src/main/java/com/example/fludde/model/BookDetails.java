package com.example.fludde.model;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class BookDetails {

    // creating variables for strings,text view, image views and button.

    String title, subtitle, publisher, publishedDate, description, thumbnail, previewLink, infoLink, buyLink;

    int pageCount;

    private ArrayList<String> authors;

    TextView titleTV, subtitleTV, publisherTV, descTV, pageTV, publishDateTV;
    Button previewBtn, buyBtn;

    private ImageView bookIV;



}
