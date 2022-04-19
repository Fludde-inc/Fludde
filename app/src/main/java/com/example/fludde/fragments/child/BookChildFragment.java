package com.example.fludde.fragments.child;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fludde.BuildConfig;
import com.example.fludde.MainActivity;
import com.example.fludde.Post;
import com.example.fludde.R;
import com.example.fludde.adapters.BookChildAdapter;
import com.example.fludde.model.BooksContent;
import com.example.fludde.netclients.NYTClient;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class BookChildFragment extends Fragment implements BookChildAdapter.OnBookContentListener {

    EditText etBookSearchField;
    RecyclerView rvBookHorizontalView;
    Button btBookSearchGo;
    EditText etBookReviewPost;
    Button btBookPost;
    LinearLayoutManager HorizontalLayout;
//    List<BooksContent> booksContentList;
    List<BooksContent> nyTimeList;
    BookChildAdapter bookAdapter;
    int bookSelectedPosition = -1;
    NYTClient bookClient;
//    List<Book> aTopBook;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Need to define the child fragment layout
        return inflater.inflate(R.layout.fragment_child_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        booksContentList = new ArrayList<>();
        nyTimeList = new ArrayList<>();
        // assigning elements
        etBookReviewPost = view.findViewById(R.id.etBookReviewPost);
        etBookSearchField = view.findViewById(R.id.etBookSearchField);
        rvBookHorizontalView = view.findViewById(R.id.rvBookHorizontalView);
        btBookSearchGo = view.findViewById(R.id.btBookSearchGo);
        btBookPost = view.findViewById(R.id.btBookPost);
        //creating horizontal view
        HorizontalLayout = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        //setting adapter
        bookAdapter = new BookChildAdapter(getContext(),nyTimeList,this);
        //adding and attaching
        rvBookHorizontalView.setLayoutManager(HorizontalLayout);
        rvBookHorizontalView.setAdapter(bookAdapter);




        btBookPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String category = "Book";
                ParseUser userPosting = ParseUser.getCurrentUser();
                BooksContent selectedBook;
                selectedBook = nyTimeList.get(bookSelectedPosition);


                String userReview = etBookReviewPost.getText().toString();


                String selectBookTitle = selectedBook.getTitle().toString();
                String selectBookDescription = selectedBook.getSummary().toString();
                String selectBookUrlImage = selectedBook.getImageURL().toString();

                byte[] bitmapBytes = new byte[0];

                String drawableRes=selectBookUrlImage;
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
                            postUserReview(category,userPosting,userReview,selectBookTitle,selectBookDescription, coverImage);
                        }
                    }});


                //clearing fields
                etBookReviewPost.setText("");
                etBookSearchField.setText("");
                bookSelectedPosition = -1;



                //need to navigate out of parent fragment back to main activity post fragment
                Intent i = new Intent(getContext(), MainActivity.class);
                startActivity(i);


            }
        });

        btBookSearchGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query;

                if(etBookSearchField != null)
                {
                    query = etBookSearchField.getText().toString();
                    etBookSearchField.setText("");
                    bookSearch(query);
                }
                else{
                    retrieveTopTen();
                }

            }
        });

retrieveTopTen();
    }

    private void postUserReview(String category, ParseUser currentUser, String userReview, String selectBookTitle, String selectBookDescription, ParseFile coverImage) {
        Post reviewPost = new Post();

        reviewPost.setCategory(category);
        reviewPost.setUser(currentUser);
        reviewPost.setReview(userReview);
        reviewPost.setContentTitle(selectBookTitle);
        reviewPost.setDescription(selectBookDescription);
        reviewPost.setContentImage(coverImage);

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



    private void bookSearch(String query)  {



        final String searchUrl = "https://api.nytimes.com/svc/books/v3/reviews.json";
        String myKey = BuildConfig.NY_TIMES_API_KEY;
        RequestParams params = new RequestParams();
        params.put("title",query);
        params.put("api-key", myKey);


        AsyncHttpClient client = new AsyncHttpClient();

        client.get(searchUrl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");

                    nyTimeList.clear();
                    nyTimeList.addAll(BooksContent.fromJsonArray(results));
                    bookAdapter.notifyDataSetChanged();
                }catch (JSONException e){

                }
                Toast.makeText(getContext(), "it worked "+ statusCode,Toast.LENGTH_SHORT).show();
                // Access a JSON object response with `json.jsonObject`
//                Log.d("DEBUG OBJECT", json.jsonObject.toString());
            }


            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });

    }

    private void retrieveTopTen() {

        String myKey = BuildConfig.NY_TIMES_API_KEY;
        RequestParams params = new RequestParams();
        params.put("api-key", myKey);
//        params.put("offset", "40");
//https://api.nytimes.com/svc/books/v3/lists/current/hardcover-fiction.json?api-key=w2uK0OgfCp9kftPXBlb3IS3rv8a3l7sd
        AsyncHttpClient client = new AsyncHttpClient();


        client.get(String.format("https://api.nytimes.com/svc/books/v3/lists/current/hardcover-fiction.json?"),params, new JsonHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // Access a JSON array response with `json.jsonArray`
//                Log.d("DEBUG ARRAY", json.jsonArray.toString());
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONObject results = jsonObject.getJSONObject("results");
                    JSONArray books = results.getJSONArray("books");



                    nyTimeList.addAll(BooksContent.fromJsonArray(books));
                    bookAdapter.notifyDataSetChanged();
                }catch (JSONException e){

                }
                Toast.makeText(getContext(), "it worked "+ statusCode,Toast.LENGTH_SHORT).show();
                // Access a JSON object response with `json.jsonObject`
//                Log.d("DEBUG OBJECT", json.jsonObject.toString());
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e("jsnfail","it failed, "+statusCode);

            }
        });



    }

    @Override
    public void onBookContentClick(int position) {
        bookSelectedPosition = position;
      Toast.makeText(getContext(), String.format("You have clicked %s", nyTimeList.get(position).getTitle()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBookContentLongClick(int position) {

    }


}
