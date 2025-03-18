package com.example.arsenal_app.fragments;

import static com.example.arsenal_app.Activities.MainActivity.db;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.arsenal_app.R;
import com.example.arsenal_app.database.DataStatus;
import com.example.arsenal_app.models.Game;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private ArrayList<Game> games = db.games;
    private TextView competitionView;
    private TextView opponentView;
    private TextView dateView;
    private TextView timeView;
    private TextView stadiumView;
    private ProgressBar progressBar;




    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Find the views and store.
        progressBar = view.findViewById(R.id.progressBar);
        competitionView = view.findViewById(R.id.next_match_competition);
        opponentView = view.findViewById(R.id.next_match_opponent);
        dateView = view.findViewById(R.id.next_match_date);
        timeView = view.findViewById(R.id.next_match_time);
        stadiumView = view.findViewById(R.id.next_match_stadium);

        // Load the data into the page.
        load();

        return view;
    }

    /**
     * This is a function that will load the data from the database to the page.
     * 1. Call fetchData - This will access the database and return with a callback.
     * 2. Callback is received and executed: onDataLoaded if returned data otherwise onError.
     *
     * DataStatus is an implementation of an interface.
     */
    private void load(){
        db.fetchData(new DataStatus() {
            @Override
            public void onDataLoaded(ArrayList<Game> dataList) {
                // Set the loading bar to invisible.
                progressBar.setVisibility(View.INVISIBLE);

                // Get next match.
                Game game = games.get(0);

                // Update all the views with the information.
                competitionView.setText(game.getCompetition());
                opponentView.setText(game.getOpponent());
                stadiumView.setText(game.getStadium());
                dateView.setText(game.getDate());
                timeView.setText(game.getTime());



            }

            @Override
            public void onError(String errorMessage) {

                competitionView.setText(errorMessage);

            }
        });
    }
}