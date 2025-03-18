package com.example.arsenal_app.Adapters;

import static androidx.core.content.ContextCompat.startActivity;

import static com.example.arsenal_app.Activities.MainActivity.db;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arsenal_app.R;
import com.example.arsenal_app.fragments.HomeFragment;
import com.example.arsenal_app.models.Game;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private ArrayList<Game> games = db.games;

    public HomeAdapter(){

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView competition;
        public ViewHolder(View view) {
            super(view);

            competition = view.findViewById(R.id.next_match_competition);
        }
    }

    @NonNull
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.ViewHolder holder, int position) {

        // Bind the data to the UI

        holder.competition.setText(games.get(0).getCompetition());


    }

    @Override
    public int getItemCount() {
        return games.size();
    }
}
