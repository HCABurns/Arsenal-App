package com.example.arsenal_app.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.arsenal_app.Adapters.FutureAdapter;
import com.example.arsenal_app.R;
import com.example.arsenal_app.database.DataRepository;
import com.example.arsenal_app.database.DataStatus;
import com.example.arsenal_app.models.EpicGame;
import com.example.arsenal_app.models.Game;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class FutureFragment extends Fragment {

    private RecyclerView recyclerView;
    public static FutureAdapter futureAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_future, container, false);
        DataRepository.getInstance().loadAllFootballGames(
        "https://general-personal-app.onrender.com/api/football",
                "football" , Game.class,new DataStatus<Game>() {
            @Override
            public void onDataLoaded(ArrayList<Game> dataList) {
                DataRepository.getInstance().getDbHelper().setGames(dataList);
                recyclerView = view.findViewById(R.id.future_games_recycler);
                futureAdapter = new FutureAdapter();
                recyclerView.setAdapter(futureAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                        LinearLayoutManager.VERTICAL, false));
            }

            @Override
            public void onError(String errorMessage) {

            }
        }, DataRepository.getInstance().getDbHelper()::setGames);
        return view;
    }
}