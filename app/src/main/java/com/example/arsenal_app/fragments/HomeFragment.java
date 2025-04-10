package com.example.arsenal_app.fragments;

import static com.example.arsenal_app.Activities.MainActivity.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.arsenal_app.R;
import com.example.arsenal_app.database.DataStatus;
import com.example.arsenal_app.models.Game;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import android.util.Base64;

/**
 * A simple {@link Fragment} subclass. The subclass implements the required routines and will
 * include the information for the next game for arsenal.
 */
public class HomeFragment extends Fragment {

    // Define the required variables for this fragment.
    //private ArrayList<Game> games = db.games;
    private TextView competitionView;
    private TextView opponentView;
    private TextView dateView;
    private TextView timeView;
    private TextView stadiumView;
    private TextView countdownView;
    private ImageView opponentBadgeView;
    private ProgressBar progressBar;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Find the views and store.
        progressBar = view.findViewById(R.id.progressBar);
        competitionView = view.findViewById(R.id.next_match_competition);
        opponentView = view.findViewById(R.id.next_match_opponent);
        dateView = view.findViewById(R.id.next_match_date);
        timeView = view.findViewById(R.id.next_match_time);
        stadiumView = view.findViewById(R.id.next_match_stadium);
        countdownView = view.findViewById(R.id.next_match_countdown);
        opponentBadgeView = view.findViewById(R.id.next_opponent_opponentBadge);

        // Load the data into the page.
        load();

        return view;
    }

    /**
     * This is a function that will load the data from the database to the page.
     * 1. Call fetchData - This will access the database and return with a callback.
     * 2. Callback is received and executed: onDataLoaded if returned data otherwise onError.
     * Following is for personal learning:
     * DataStatus is an Anonymous class implementation of an interface.
     * db.fetchData only has access to the interface (onDataLoaded and onError)
     * It can't access the public string as it only accesses the interface - The public string is
     * is a local scope variable. A getter or callback would be used to access.
     */
    private void load(){
        db.fetchData(new DataStatus() {
            public String fetchCantAccessMe="";
            @Override
            public void onDataLoaded(ArrayList<Game> dataList) {
                // Set the loading bar to invisible.
                progressBar.setVisibility(View.INVISIBLE);

                int i = 0;
                long milliseconds = -1;

                while (true) {
                    // Get next match.
                    Game game = dataList.get(i);

                    // Update all the views with the information. (Note: Date is done separate)
                    competitionView.setText(game.getCompetition());
                    opponentView.setText(game.getOpponent());
                    stadiumView.setText(game.getStadium());
                    dateView.setText(game.getDateFormatted());
                    timeView.setText(game.getTimeFormatted());

                    // Convert the base 64 to a bitmap image and set.
                    byte[] base64 = Base64.decode(game.getBadge_base64(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(base64, 0, base64.length);
                    opponentBadgeView.setImageBitmap(bitmap);

                    // Find the milliseconds between next game and now.
                    LocalDateTime pastDateTime;
                    LocalDateTime now;

                    String[] dateParts = game.getDate().split("-");
                    int year = Integer.parseInt(dateParts[0]);
                    int month = Integer.parseInt(dateParts[1]);
                    int day = Integer.parseInt(dateParts[2]);
                    // todo: Decide on if leaving in 24h format or change to 12h with am/pm

                    String[] timeParts = game.getTime().split(":");
                    int hours = Integer.parseInt(timeParts[0]);
                    int minutes = Integer.parseInt(timeParts[1]);
                    int seconds = Integer.parseInt(timeParts[2]);

                    milliseconds = -1;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        pastDateTime = LocalDateTime.of(year, month, day, hours, minutes, seconds);
                        now = LocalDateTime.now();
                        milliseconds = Duration.between(now, pastDateTime).getSeconds() * 1000;
                    }

                    System.out.println(milliseconds);
                    if (milliseconds > -7200000){
                        break;
                    }
                    i+=1;
                }

                // Set the countdown and decrement every second.
                new CountDownTimer(milliseconds, 1000){
                    @Override
                    public void onTick(long milliRemaining) {
                        // Find the remaining days, hours, minutes and seconds.
                        int days = (int) (milliRemaining / (1000 * 60 * 60 * 24));
                        milliRemaining -= days * (1000 * 60 * 60 * 24);
                        int hours = (int) (milliRemaining / (1000 * 60 * 60));
                        milliRemaining -= hours * (1000 * 60 * 60);
                        int minutes = (int) (milliRemaining / (1000 * 60));
                        milliRemaining -= minutes * (1000 * 60);
                        int seconds = (int) (milliRemaining / (1000));

                        // Display the correct format based on available information.
                        if (days > 0) {
                            countdownView.setText(MessageFormat.format("{0}D {1}H {2}M {3}S",
                                    days, hours, minutes, seconds));
                        } else if (hours > 0){
                            countdownView.setText(MessageFormat.format("{0}H {1}M {2}S",
                                    hours, minutes, seconds));
                        } else if (minutes > 0) {
                            countdownView.setText(MessageFormat.format("{0}M {1}S",
                                    minutes, seconds));
                        } else{
                            countdownView.setText(MessageFormat.format("{0}S", seconds));
                        }
                    }

                    /**
                     * Once the countdown reaches 0, display a game started message.
                     */
                    @Override
                    public void onFinish() {
                        countdownView.setText(R.string.countdown_finished);
                    }
                }.start();
            }

            /**
             * Callback function to display the error message to the user.
             * @param errorMessage - Error message to be displayed to the user.
             */
            @Override
            public void onError(String errorMessage) {
                // Set the top line of text to the error message.
                competitionView.setText(errorMessage);
                this.fetchCantAccessMe=""; //Added to remove warning
            }
        });
    }
}