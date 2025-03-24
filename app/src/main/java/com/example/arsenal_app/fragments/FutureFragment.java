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

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class FutureFragment extends Fragment {

    private RecyclerView recyclerView;
    public static FutureAdapter futureAdapter;

    public FutureFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_future, container, false);

        recyclerView = view.findViewById(R.id.future_games_recycler);

        futureAdapter = new FutureAdapter();
        recyclerView.setAdapter(futureAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        return view;
    }
}