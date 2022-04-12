package com.example.fludde.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fludde.adapters.BookHorizontalAdapter;
import com.example.fludde.adapters.ContentHorizontalAdapter;
import com.example.fludde.R;
import com.example.fludde.model.BooksContent;
import com.example.fludde.model.Contents;
import com.example.fludde.model.MovieContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComposeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeFragment extends Fragment {

     private   Spinner spDropdownMenu;
    private HorizontalScrollView hsContentview;
    View v;
    LinearLayoutManager HorizontalLayout;
    List<Contents> contents;
    List<BooksContent> bookContents;
      RecyclerView rvHorizontalView;
    BookHorizontalAdapter bookContentAdapter;
    ContentHorizontalAdapter contentAdapter;
    public static  String CONTENT_CURRENT_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    private Context context;
    private  String[] dropDownItems = new String[]{"Select an Item","Movies","Books","Music"};


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Object AdapterView;

    public ComposeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ComposeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComposeFragment newInstance(String param1, String param2) {
        ComposeFragment fragment = new ComposeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v =  inflater.inflate(R.layout.fragment_compose, container, false);


        ///Spinner set up

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        spDropdownMenu = v.findViewById(R.id.spDropdownMenu);

//        ////Adapter for the Spinner
        ArrayAdapter<String> dropDownAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item,dropDownItems );
        dropDownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDropdownMenu.setAdapter(dropDownAdapter);


        spDropdownMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?>  parent, View view, int pos, long l) {

                switch (pos){

                    case 0:

                        contentDisplay();
                        return;
                    case 1:
                    default:

                        Toast.makeText(getContext(), String.format("You have select Movies"), Toast.LENGTH_SHORT).show();
                        break;
                    case 2:

                        CONTENT_CURRENT_URL = getResources().getString(R.string.ny_times_book_current_url);
                        contentDisplay("books");
                        Toast.makeText(getContext(), String.format("You have select Books"), Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(getContext(), String.format("You have select Music"), Toast.LENGTH_SHORT).show();
                        break;

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }});
        return v;
    }

    private void refreshDisplay() {

//        listView.destroyDrawingCache();
//        listView.setVisibility(ListView.INVISIBLE);
//        listView.setVisibility(ListView.VISIBLE);

 rvHorizontalView.destroyDrawingCache();
 rvHorizontalView.setVisibility(ListView.INVISIBLE);
 rvHorizontalView.setVisibility(ListView.VISIBLE);
    }

    /////////////////////////////////////DEFAULT DISPLAY DURING LOADING ////////////////////////////////////////////////////
    private void contentDisplay() {

        contents = new ArrayList<>();
        rvHorizontalView = v.findViewById(R.id.rvHorizontalView);

        HorizontalLayout = new LinearLayoutManager(this.getContext(),LinearLayoutManager.HORIZONTAL,false);

        contentAdapter = new ContentHorizontalAdapter(this.getContext(),contents);

        rvHorizontalView.setLayoutManager(HorizontalLayout);
        rvHorizontalView.setAdapter(contentAdapter);


        AsyncHttpClient client = new AsyncHttpClient();

        client.get(CONTENT_CURRENT_URL, new JsonHttpResponseHandler() {
            @Override

            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;

                try{
                    JSONArray results = jsonObject.getJSONArray("results");
                    contents.addAll(MovieContent.fromJsonArray(results));
                    contentAdapter.notifyDataSetChanged();
                }catch (JSONException e){

                }


            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });
    }
    /////////////////////////CONTENT DISPLAY FOR BOOKS AND MUSIC ///////////////////////////////////////////////////////
    private void contentDisplay(String content){


         if (content.equals("books"))
        {      // Content display inside of recycler view
            contents = new ArrayList<>();
             rvHorizontalView = v.findViewById(R.id.rvHorizontalView);

            HorizontalLayout = new LinearLayoutManager(this.getContext(),LinearLayoutManager.HORIZONTAL,false);

            contentAdapter = new ContentHorizontalAdapter(this.getContext(),contents);
            contents.clear();
            rvHorizontalView.setLayoutManager(HorizontalLayout);
            rvHorizontalView.setAdapter(contentAdapter);


            AsyncHttpClient client = new AsyncHttpClient();

            client.get(CONTENT_CURRENT_URL, new JsonHttpResponseHandler() {
                @Override

                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    JSONObject jsonObject = json.jsonObject;

                    try{
                        JSONArray results = jsonObject.getJSONArray("results");

                        contents.addAll(BooksContent.fromJsonArray(results));
                        contentAdapter.notifyDataSetChanged();
                    }catch (JSONException e){

                    }


                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                }
            });


            ///////////////////


        }
        else if (content.equals("music"))
        {

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




        }

//    public void setLiteratureList(List<? extends Contents> contentsList) {
//        if (contents == null){
//            contents = new ArrayList<>();
//        }
//        contents.clear();
//        contents.addAll(contentsList);
////        contentAdapter.notifyDataSetChanged();
//    }
}