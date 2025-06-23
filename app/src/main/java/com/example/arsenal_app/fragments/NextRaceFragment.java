package com.example.arsenal_app.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.arsenal_app.R;
import com.example.arsenal_app.database.API;
import com.example.arsenal_app.database.DataStatus;
import com.example.arsenal_app.models.Race;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class NextRaceFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.next_race, container, false);

        // Find the views and store.
        ProgressBar progressBar = view.findViewById(R.id.loading_bar);
        LinearLayout contentLayout = view.findViewById(R.id.race_layout);
        TextView raceTitleView = view.findViewById(R.id.race_name);
        ImageView trackMap = view.findViewById(R.id.track_map_holder);
        TextView countdownView = view.findViewById(R.id.next_race_countdown);
        TextView timeView = view.findViewById(R.id.next_race_time);
        TextView dateView = view.findViewById(R.id.next_race_date);
        TextView circuitView = view.findViewById(R.id.next_race_circuit);

        // Load the data into the page via API call.
        API api = new API();
        try {
            api.allRacesApiAsync(new DataStatus<Race>() {
                @Override
                public void onDataLoaded(ArrayList<Race> races) {
                    // Sort based on closest to today.
                    int i = 0;
                    while (i < races.size()) {
                        // Change visibility now it's retrieved.
                        progressBar.setVisibility(View.GONE);
                        contentLayout.setVisibility(View.VISIBLE);

                        Race race = races.get(i);
                        // Update the information.
                        raceTitleView.setText(race.getRace());
                        circuitView.setText(race.getCircuit());

                        // Convert the base 64 to a bitmap image and set.
                        byte[] base64 = Base64.decode(race.getTrack(), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(base64, 0, base64.length);
                        trackMap.setImageBitmap(bitmap);

                        // Get time to the next race.
                        // Find the milliseconds between next race and now.
                        long milliseconds;
                        LocalDateTime nextRaceDateTime;
                        LocalDateTime now;

                        String[] dateParts = race.getDate().split("/");
                        int day = Integer.parseInt(dateParts[0]);
                        int month = Integer.parseInt(dateParts[1]);
                        int year = Integer.parseInt(dateParts[2])+2000;

                        String[] timeParts = race.getTime().split(":");
                        System.out.println(race.getRace() + " " + race.getTime());
                        int hours = Integer.parseInt(timeParts[0]);
                        int minutes = Integer.parseInt(timeParts[1]);
                        int seconds = 0;

                        milliseconds = -1;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            nextRaceDateTime = LocalDateTime.of(year, month, day, hours, minutes, seconds);
                            now = LocalDateTime.now();
                            milliseconds = Duration.between(now,nextRaceDateTime).getSeconds() * 1000;
                        }

                        // If the race is older than 2 hours long, find the next one.
                        if (milliseconds < -7200000){
                            i+=1;
                            continue;
                        }

                        // Set the countdown and decrement every second.
                        new CountDownTimer(milliseconds, 1000) {
                            @Override
                            public void onTick(long milliRemaining) {
                                // Find the remaining days, hours, minutes and seconds.
                                int days = (int) (milliRemaining / (1000.0 * 60.0 * 60.0 * 24.0));
                                milliRemaining -= days * (1000.0 * 60.0 * 60.0 * 24.0);
                                int hours = (int) (milliRemaining / (1000.0 * 60.0 * 60.0));
                                milliRemaining -= hours * (1000.0 * 60.0 * 60.0);
                                int minutes = (int) (milliRemaining / (1000.0 * 60.0));
                                milliRemaining -= minutes * (1000.0 * 60.0);
                                int seconds = (int) (milliRemaining / (1000.0));
                                // Display the correct format based on available information.
                                if (days > 0) {
                                    countdownView.setText(MessageFormat.format("{0}D {1}H {2}M {3}S",
                                            days, hours, minutes, seconds));
                                } else if (hours > 0) {
                                    countdownView.setText(MessageFormat.format("{0}H {1}M {2}S",
                                            hours, minutes, seconds));
                                } else if (minutes > 0) {
                                    countdownView.setText(MessageFormat.format("{0}M {1}S",
                                            minutes, seconds));
                                } else {
                                    countdownView.setText(MessageFormat.format("{0}S", seconds));
                                }
                            }


                            /**
                             * Once the countdown reaches 0, display a race started message.
                             */
                            @Override
                            public void onFinish() {
                                countdownView.setText(R.string.race_countdown_finished);
                            }
                        }.start();

                        // Set date and time.
                        timeView.setText(race.getFormattedTime());
                        dateView.setText(race.getDate());

                        break;
                    }
                }
                @Override
                public void onError(String e){
                    System.out.println("Error on retrieval of the races: " + e);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return view;
    }
}
