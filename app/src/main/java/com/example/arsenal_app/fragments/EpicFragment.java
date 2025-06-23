package com.example.arsenal_app.fragments;

import static com.example.arsenal_app.Activities.MainActivity.api;
import static com.example.arsenal_app.Activities.MainActivity.db;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arsenal_app.Adapters.EpicAdapter;
import com.example.arsenal_app.Adapters.FutureAdapter;
import com.example.arsenal_app.R;
import com.example.arsenal_app.database.DataStatus;
import com.example.arsenal_app.models.EpicGame;
import com.example.arsenal_app.models.Game;
import com.example.arsenal_app.models.Race;

import java.util.ArrayList;

public class EpicFragment extends Fragment {

    private RecyclerView recyclerView;
    public static EpicAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_epic, container, false);

        FrameLayout frameLayout = view.findViewById(R.id.epic_recycler_frame);
        ProgressBar progressBar = view.findViewById(R.id.epic_game_progress_bar);
        RecyclerView recyclerView = view.findViewById(R.id.epic_games_recycler);

        try {
            api.allEpicGamesApiAsync(new DataStatus<EpicGame>() {
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
                    System.out.println("ERROR " +errorMessage);
                }
            });
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
        return view;
    }
}
