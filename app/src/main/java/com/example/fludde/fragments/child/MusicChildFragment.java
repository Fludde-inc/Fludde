package com.example.fludde.fragments.child;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fludde.R;
import com.example.fludde.adapters.MusicChildAdapter;
import com.example.fludde.fragments.PostFragment;
import com.example.fludde.model.MusicContent;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class MusicChildFragment extends Fragment implements MusicChildAdapter.OnMusicContentListener  {

    public final String CURRENT_TOP_SONGS_URL ="https://rss.applemarketingtools.com/api/v2/us/music/most-played/25/songs.json";
    private final String SEARCH_URL ="https://api.spotify.com/v1/search";
    int musicSelectedPosition = -1;
    List<MusicContent> musicContentList;
    Button btMusicSearchGo;
    Button btMusicPost;
    EditText etMusicSearchField;
    EditText etMusicReviewPost;
    RecyclerView rvMusicHorizontalView;
    MusicChildAdapter musicContentAdapter;
    LinearLayoutManager musicHorizontalLayout;
    Fragment postFragment;




   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Need to define the child fragment layout
        return inflater.inflate(R.layout.fragment_child_music, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing
        musicContentList = new ArrayList<>();
        rvMusicHorizontalView = view.findViewById(R.id.rvMusicHorizontalView);
        etMusicSearchField = view.findViewById(R.id.etMusicSearchField);
        btMusicSearchGo = view.findViewById(R.id.btMusicSearchGo);
        etMusicReviewPost = view.findViewById(R.id.etMusicReviewPost);

        postFragment = new PostFragment();
        musicHorizontalLayout = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);

        musicContentAdapter = new MusicChildAdapter(getContext(),musicContentList,this);

        rvMusicHorizontalView.setLayoutManager(musicHorizontalLayout);
        rvMusicHorizontalView.setAdapter(musicContentAdapter);

        btMusicSearchGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query;

                if(etMusicSearchField != null)
                {
                    query = etMusicSearchField.getText().toString();
                    etMusicSearchField.setText("");
                    musicSearch(query);
                }
                else{
                    currentTopSongs();
                }
            }
        });
        

        currentTopSongs();
    }

    private void musicSearch(String query) {

       String type = "track,artist";
       String market = "ES";
       String limit = "10";
       String offset = "5";
       final String OAUTH_TOKEN = "";

       RequestParams params = new RequestParams();
       AsyncHttpClient client = new AsyncHttpClient();



    }


    private void currentTopSongs() {

            AsyncHttpClient client = new AsyncHttpClient();

            client.get(CURRENT_TOP_SONGS_URL, new JsonHttpResponseHandler() {
                @Override

                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    JSONObject jsonObject = json.jsonObject;

                    try {
                        JSONObject feed = jsonObject.getJSONObject("feed");
                        JSONArray results = feed.getJSONArray("results");
                        musicContentList.clear();
                        musicContentList.addAll(MusicContent.fromJsonArray(results));
                        musicContentAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {

                    }

                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                }
            });



    }


    @Override
    public void onMusicContentClick(int position) {
        musicSelectedPosition = position;
        Toast.makeText(getContext(), String.format("You have selected %s", musicContentList.get(position).getTitle()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMusicContentLongClick(int position) {

    }
}
