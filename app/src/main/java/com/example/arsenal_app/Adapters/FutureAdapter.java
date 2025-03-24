package com.example.arsenal_app.Adapters;

import static com.example.arsenal_app.Activities.MainActivity.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arsenal_app.R;
import com.example.arsenal_app.fragments.FutureFragment;
import com.example.arsenal_app.models.Game;

import java.util.ArrayList;

public class FutureAdapter extends RecyclerView.Adapter<FutureAdapter.ViewHolder> {

    private ArrayList<Game> games = db.games;

    public FutureAdapter(){

        System.out.println("AA: " + games.size());

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView competition;
        private TextView opponent;
        private TextView date;
        private TextView time;
        private TextView stadium;
        private ImageView badge;
        public ViewHolder(View view) {
            super(view);
            // Find the relevant text views in the .
            competition = view.findViewById(R.id.future_games_competition);
            opponent = view.findViewById(R.id.future_games_opponent);
            stadium = view.findViewById(R.id.future_games_stadium);
            date = view.findViewById(R.id.future_games_date);
            time = view.findViewById(R.id.future_games_time);
            badge = view.findViewById(R.id.future_games_badge);
        }
    }

    @NonNull
    @Override
    public FutureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Get view for the single item.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_game, parent, false);

        // Return the view holder.
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FutureAdapter.ViewHolder holder, int position) {
        // Bind the data to the UI
        holder.competition.setText(games.get(position).getCompetition());
        holder.opponent.setText(games.get(position).getOpponent());
        holder.stadium.setText(games.get(position).getStadium());
        holder.date.setText(games.get(position).getDate());
        holder.time.setText(games.get(position).getTime());

        // Convert the base 64 to a bitmap image and set.
        byte[] base64 = Base64.decode(games.get(position).getBadge_base64(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(base64, 0, base64.length);
        holder.badge.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return games.size();
    }
}
