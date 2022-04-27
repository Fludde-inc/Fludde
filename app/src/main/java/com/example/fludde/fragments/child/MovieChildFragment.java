package com.example.fludde.fragments.child;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fludde.MainActivity;
import com.example.fludde.Post;
import com.example.fludde.R;
import com.example.fludde.adapters.MovieChildAdapter;
import com.example.fludde.fragments.PostFragment;
import com.example.fludde.model.MovieContent;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.InputStream;


import java.net.URL;

import java.util.ArrayList;

import java.util.List;

import java.io.IOException;

import okhttp3.Headers;

public class MovieChildFragment extends Fragment implements MovieChildAdapter.OnMovieContentListener {

    private static final String TAG = "imageconvert";
    private File coverImage;
    List<MovieContent> movieContentList;
     RecyclerView rvMovieHorizontalView;
    LinearLayoutManager HorizontalLayout;
  MovieChildAdapter movieContentAdapter;
    Button btMoviePost;
    Button btMovieSearchGo;
    EditText etMovieReviewPost;
    EditText etMovieSearchField;
     String currentPhotoPath;
    ImageView ivContentImage;
    TextView tvImageContentTitle;
    Fragment postFragment;
    int movieSelectedPosition = -1;


    public static  String CONTENT_CURRENT_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Need to define the child fragment layout

        return  inflater.inflate(R.layout.fragment_child_movie, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initializing
        movieContentList = new ArrayList<>();
        rvMovieHorizontalView = view.findViewById(R.id.rvMovieHorizontalView);
        btMoviePost = view.findViewById(R.id.btMoviePost);
        btMovieSearchGo = view.findViewById(R.id.btMovieSearchGo);
        etMovieReviewPost = view.findViewById(R.id.etMovieReviewPost);
        etMovieSearchField = view.findViewById(R.id.etMovieSearchField);
        postFragment = new PostFragment();
        // Setting view and adapters
        HorizontalLayout = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        rvMovieHorizontalView.setLayoutManager(HorizontalLayout);
        movieContentAdapter = new MovieChildAdapter(getContext(),movieContentList,this);



        rvMovieHorizontalView.setAdapter(movieContentAdapter);

btMoviePost.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        ParseUser  userPosting = ParseUser.getCurrentUser();
//        byte[] imgByteArray = new byte[0];
        MovieContent selectedMovie;
        selectedMovie = movieContentList.get(movieSelectedPosition);

        final String category = "Movie";

        String userReview = etMovieReviewPost.getText().toString();


        String selectMovieTitle = selectedMovie.getTitle().toString();
        String selectMovieDescription = selectedMovie.getOverview().toString();
        String selectMovieUrlImage = selectedMovie.getBackdropPath().toString();






   //     String contentPicName = String.format("%s"+".jpg",selectedMovie.getTitle().toString());
        byte[] bitmapBytes = new byte[0];

        String drawableRes=selectMovieUrlImage;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            URL url = new URL(drawableRes);
            Bitmap contentImage = BitmapFactory.decodeStream((InputStream) url.getContent());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            contentImage.compress(Bitmap.CompressFormat.JPEG, 76, stream);
             bitmapBytes = stream.toByteArray();
        } catch (IOException e) {
            //Log.e(TAG, e.getMessage());
        }



//        ParseFile contentImage = new ParseFile(image);


        ParseFile coverImage = new ParseFile("myImage.jpg", bitmapBytes);

        coverImage.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e!=null)
               {
                    Log.e("ImageSaving","Issue Saving profile pic");
               }
               else{
                   //Calling post method
                 postUserReview(category,userPosting,userReview,selectMovieTitle,selectMovieDescription, coverImage);
            }
        }});





        //clearing fields
       etMovieReviewPost.setText("");
       etMovieSearchField.setText("");
       movieSelectedPosition = -1;



       //need to navigate out of parent fragment back to main activity post fragment
        Intent i = new Intent(getContext(), MainActivity.class);
        startActivity(i);


    }
});
    btMovieSearchGo.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        String query;

        if(etMovieSearchField != null ){
            query = etMovieSearchField.getText().toString();
            etMovieSearchField.setText("");
            movieSearch(query);


        }
        else{
            getCurrentMovies();
        }

    }
});
getCurrentMovies();


    }



    private void movieSearch(String query) {

        final String searchURl = "https://api.themoviedb.org/3/search/movie?";
        String mKey ="a07e22bc18f5cb106bfe4cc1f83ad8ed";
        RequestParams params = new RequestParams();
        params.put("api_key",mKey);
        params.put("query",query);

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(searchURl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;

                try{
                    JSONArray results = jsonObject.getJSONArray("results");
                    movieContentList.clear();
                    movieContentList.addAll(MovieContent.fromJsonArray(results));
                    movieContentAdapter.notifyDataSetChanged();
                }catch (JSONException e){

                }



            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });


    }
//
    private void postUserReview(String category, ParseUser currentUser, String userReview, String selectMovieTitle, String selectMovieDescription, ParseFile selectImage) {
        Post reviewPost = new Post();

        reviewPost.setCategory(category);
        reviewPost.setUser(currentUser);
        reviewPost.setDescription(selectMovieDescription);
        reviewPost.setContentTitle(selectMovieTitle);
        reviewPost.setReview(userReview);
        reviewPost.setContentImage(selectImage);

        reviewPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("postIssue", "Issue Saving post");
                } else {

                }
            }
        });


 }


        private void getCurrentMovies () {
            AsyncHttpClient client = new AsyncHttpClient();

            client.get(CONTENT_CURRENT_URL, new JsonHttpResponseHandler() {
                @Override

                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    JSONObject jsonObject = json.jsonObject;

                    try {
                        JSONArray results = jsonObject.getJSONArray("results");
                        movieContentList.clear();
                        movieContentList.addAll(MovieContent.fromJsonArray(results));
                        movieContentAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {

                    }

                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                }
            });
        }




    @Override
    public void onMovieContentClick(int position) {
        movieSelectedPosition = position;
        Toast.makeText(getContext(), String.format("You have selected %s", movieContentList.get(position).getTitle()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMovieContentLongClick(int position) {
        Toast.makeText(getContext(), "this was a long click", Toast.LENGTH_LONG).show();

    }
}
