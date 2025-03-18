package com.example.arsenal_app.fragments;

import static com.example.arsenal_app.Activities.MainActivity.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.arsenal_app.R;
import com.example.arsenal_app.database.DataStatus;
import com.example.arsenal_app.models.Game;

import java.util.ArrayList;
import android.util.Base64;

/**
 * A simple {@link Fragment} subclass. The subclass implements the required routines and will
 * include the information for the next game for arsenal.
 */
public class HomeFragment extends Fragment {

    // Define the required variables for this fragment.
    //private ArrayList<Game> games = db.games;
    private TextView competitionView;
    private TextView opponentView;
    private TextView dateView;
    private TextView timeView;
    private TextView stadiumView;
    private ImageView opponentBadgeView;
    private ProgressBar progressBar;

    public HomeFragment() {
        // Required empty public constructor
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
        opponentBadgeView = view.findViewById(R.id.next_opponent_opponentBadge);

        // Load the data into the page.
        load();

        return view;
    }

    /**
     * This is a function that will load the data from the database to the page.
     * 1. Call fetchData - This will access the database and return with a callback.
     * 2. Callback is received and executed: onDataLoaded if returned data otherwise onError.
     * Following is for personal learning:
     * DataStatus is an Anonymous class implementation of an interface.
     * db.fetchData only has access to the interface (onDataLoaded and onError)
     * It can't access the public string as it only accesses the interface - The public string is
     * is a local scope variable. A getter or callback would be used to access.
     */
    private void load(){
        db.fetchData(new DataStatus() {
            public String fetchCantAccessMe="";
            @Override
            public void onDataLoaded(ArrayList<Game> dataList) {
                // Set the loading bar to invisible.
                progressBar.setVisibility(View.INVISIBLE);

                // Get next match.
                Game game = dataList.get(0);

                // Update all the views with the information.
                competitionView.setText(game.getCompetition());
                opponentView.setText(game.getOpponent());
                stadiumView.setText(game.getStadium());
                dateView.setText(game.getDate());
                timeView.setText(game.getTime());

                // Convert the base 64 to a bitmap image and set.
                byte[] base64 = Base64.decode(game.getBadge_base64(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(base64, 0, base64.length);
                opponentBadgeView.setImageBitmap(bitmap);
            }

            /**
             * Callback function to display the error message to the user.
             * @param errorMessage - Error message to be displayed to the user.
             */
            @Override
            public void onError(String errorMessage) {
                // Set the top line of text to the error message.
                competitionView.setText(errorMessage);
                this.fetchCantAccessMe=""; //Added to remove warning
            }
        });
    }
}