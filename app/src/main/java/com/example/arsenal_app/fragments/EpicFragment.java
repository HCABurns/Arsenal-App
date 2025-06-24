package com.example.arsenal_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arsenal_app.Adapters.EpicAdapter;
import com.example.arsenal_app.R;
import com.example.arsenal_app.database.DataRepository;
import com.example.arsenal_app.database.DataStatus;
import com.example.arsenal_app.models.EpicGame;


import java.util.ArrayList;

public class EpicFragment extends Fragment {
    public static EpicAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_epic, container, false);
        ProgressBar progressBar = view.findViewById(R.id.epic_game_progress_bar);
        RecyclerView recyclerView = view.findViewById(R.id.epic_games_recycler);
        DataRepository.getInstance().loadAllEpicGames(
                "epic_games",
                "epic_games" , EpicGame.class, new DataStatus<EpicGame>() {
            @Override
            public void onDataLoaded(ArrayList<EpicGame> dataList) {
                progressBar.setVisibility(View.GONE);

                adapter = new EpicAdapter();
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                        LinearLayoutManager.VERTICAL, false));
            }

            @Override
            public void onError(String errorMessage) {
                System.out.println("ERROR " + errorMessage);
            }
        }, DataRepository.getInstance().getDbHelper()::setEpicGames);
        return view;
    }
}
