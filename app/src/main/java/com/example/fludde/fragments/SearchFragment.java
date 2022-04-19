package com.example.fludde.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fludde.R;
import com.example.fludde.User;
import com.example.fludde.adapters.SearchFragmentAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    List<ParseUser> allUsers;
    private RecyclerView rvUserQuery;
    SearchFragmentAdapter searchFragmentAdapter;
    EditText etSearchFieldUser;
    Button btSearch;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvUserQuery = view.findViewById(R.id.rvUserQuery);
        etSearchFieldUser = view.findViewById(R.id.etSearchFieldUser);
        btSearch = view.findViewById(R.id.btSearch);

        allUsers = new ArrayList<>();
        searchFragmentAdapter = new SearchFragmentAdapter(getContext(), allUsers);

        rvUserQuery.setAdapter(searchFragmentAdapter);
        rvUserQuery.setLayoutManager(new LinearLayoutManager(getContext()));

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userName = etSearchFieldUser.getText().toString();

                lookForUser(userName);
            }
        });

    }

//    private void lookForUser(String userName) {
//        ParseQuery<User> query =  ParseQuery.getQuery(User.class);
//        query.whereEqualTo(User.KEY_USER, userName.toString());
//        query.findInBackground(new FindCallback<User>() {
//            @Override
//            public void done(List<User> users, ParseException e) {
//                if (e == null) {
//                    // The query was successful, returns the users that matches
//                    // the criteria.
//                    for(User aUser : users) {
//                        aUser.getUsername().toString();
//                    }
//                } else {
//                    // Something went wrong.
//                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//                allUsers.addAll(users);
//                searchFragmentAdapter.notifyDataSetChanged();
//
//            }
//        });


    private void lookForUser(String userName) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", userName);
        query.findInBackground((users, e) -> {
            if (e == null) {
                // The query was successful, returns the users that matches
                // the criteria.
                for(ParseUser aUser : users) {
                    aUser.getUsername().toString();
                }
            } else {
                // Something went wrong.

            }
            allUsers.addAll(users);
          searchFragmentAdapter.notifyDataSetChanged();
        });;
    }

}