package com.example.arsenal_app.Adapters;

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
import com.example.arsenal_app.database.DataRepository;
import com.example.arsenal_app.models.EpicGame;

import java.util.ArrayList;

public class EpicAdapter extends RecyclerView.Adapter<EpicAdapter.ViewHolder> {

    private ArrayList<EpicGame> games = DataRepository.getInstance().getDbHelper().getEpicGames();

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView time;

        private ImageView cover;
        public ViewHolder(View view) {
            super(view);
            // Find the relevant text views in the .
            name = view.findViewById(R.id.epic_game_name);
            time = view.findViewById(R.id.epic_game_time);
            cover = view.findViewById(R.id.epic_game_cover);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        // Get view for the single item.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.epic_game, parent, false);

        // Return the view holder.
        return new EpicAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        holder.name.setText(games.get(i).getName());
        holder.time.setText(games.get(i).getTime());
        String b64 = games.get(i).getCover_base64();

        // Decode base64 to bytes
        byte[] base64 = Base64.decode(b64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(base64, 0, base64.length);
        holder.cover.setImageBitmap(bitmap);

    }

    @Override
    public int getItemCount() {
        return games.size();
    }

}
