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
import com.example.arsenal_app.models.EpicGame;

import java.util.ArrayList;

public class EpicAdapter extends RecyclerView.Adapter<EpicAdapter.ViewHolder> {

    private ArrayList<EpicGame> games = db.epicGames;

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

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


}
