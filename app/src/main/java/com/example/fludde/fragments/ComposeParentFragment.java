package com.example.fludde.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.fludde.R;
import com.example.fludde.fragments.child.BookChildFragment;
import com.example.fludde.fragments.child.MovieChildFragment;
import com.example.fludde.fragments.child.MusicChildFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComposeParentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeParentFragment extends Fragment {
    private Spinner spDropdownMenu;
    private  String[] dropDownItems = new String[]{"Movies","Books","Music"};

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ComposeParentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ComposeParentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComposeParentFragment newInstance(String param1, String param2) {
        ComposeParentFragment fragment = new ComposeParentFragment();
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
        return inflater.inflate(R.layout.fragment_compose_parent, container, false);

            }


    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


        ///Spinner set up

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        spDropdownMenu = view.findViewById(R.id.spDropdownMenu);

      ////Adapter for the Spinner
        ArrayAdapter<String> dropDownAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item,dropDownItems );
        dropDownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDropdownMenu.setAdapter(dropDownAdapter);


        spDropdownMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?>  parent, View view, int pos, long l) {


                switch (pos){


                    case 0:
                    default:
                            insertMovieChildFragment();
//                        aChildFragment = new MovieChildFragment();
                        Toast.makeText(getContext(), String.format("You have select Movies"), Toast.LENGTH_SHORT).show();
                        break;
                    case 1:

                            insertBookChildFragment();
                        Toast.makeText(getContext(), String.format("You have select Books"), Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        insertMusicChildFragment();
                        Toast.makeText(getContext(), String.format("You have select Music"), Toast.LENGTH_SHORT).show();
                        break;

                }


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }});

    }


    // Embeds the child fragment dynamically


    private void insertMovieChildFragment(){
        Fragment childFragment = new MovieChildFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.child_fragment_container, childFragment).commit();

    }
    private void insertBookChildFragment() {
        Fragment childFragment = new BookChildFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.child_fragment_container, childFragment).commit();
    }

    private void insertMusicChildFragment() {
        Fragment musicChildFragment = new MusicChildFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.child_fragment_container, musicChildFragment).commit();
    }


}