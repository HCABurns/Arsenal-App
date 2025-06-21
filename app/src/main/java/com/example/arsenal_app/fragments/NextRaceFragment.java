package com.example.arsenal_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.arsenal_app.R;
import com.example.arsenal_app.database.API;
import com.example.arsenal_app.database.DataStatus;
import com.example.arsenal_app.models.Race;

import java.util.ArrayList;

public class NextRaceFragment extends Fragment {

    private ProgressBar progressBar;

    public NextRaceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.next_race, container, false);

        // Find the views and store.
        ProgressBar progressBar = view.findViewById(R.id.loading_bar);
        LinearLayout contentLayout = view.findViewById(R.id.race_layout);
        TextView raceTitle = view.findViewById(R.id.race_name);

        // Load the data into the page.
        API api = new API();
        try {
            api.get_all_races(new DataStatus<Race>() {
                @Override
                public void onDataLoaded(ArrayList<Race> races) {
                    System.out.println("Successful Retrieval of the races!");

                    // Sort based on closest to today.

                    // Change visibility now it's retrieved.
                    progressBar.setVisibility(View.GONE);
                    contentLayout.setVisibility(View.VISIBLE);

                    // Update the information.
                    raceTitle.setText(races.get(0).race);
                }

                @Override
                public void onError(String errorMessage) {
                    System.out.println("Error on retrieval of the races!");
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Inflate the layout for this fragment
        //view = inflater.inflate(R.layout.next_race, container, false);
        System.out.println("------------------ VIEW HAS BEEN RETURED FOR NEXT FACE FRAGMENT");
        return view;
    }

    public void load(){



    }

}
