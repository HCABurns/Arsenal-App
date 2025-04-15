package com.example.arsenal_app.fragments;

import static com.example.arsenal_app.Activities.MainActivity.db;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arsenal_app.Adapters.EpicAdapter;
import com.example.arsenal_app.Adapters.FutureAdapter;
import com.example.arsenal_app.R;
import com.example.arsenal_app.database.DataStatus;
import com.example.arsenal_app.models.EpicGame;
import com.example.arsenal_app.models.Game;

import java.util.ArrayList;

public class EpicFragment extends Fragment {

    private RecyclerView recyclerView;
    public static EpicAdapter adapter;

    public EpicFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        load();
        View view = inflater.inflate(R.layout.fragment_epic, container, false);
        recyclerView = view.findViewById(R.id.epic_games_recycler);
        adapter = new EpicAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        System.out.println("wdawd");



        return view;
    }

    private void load(){
        System.out.println("Loading...");
        db.fetchEpicData(new DataStatus<EpicGame>() {
            @Override
            public void onDataLoaded(ArrayList<EpicGame> dataList) {
                System.out.println("Loaded");
                System.out.println(dataList.size());
            }

            @Override
            public void onError(String errorMessage) {
                System.out.println(errorMessage);

            }
        });

    }
}
