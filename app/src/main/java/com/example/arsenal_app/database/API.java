package com.example.arsenal_app.database;

// Imports.
import static com.example.arsenal_app.Activities.MainActivity.db;

import android.os.Looper;

import com.example.arsenal_app.models.Race;

import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import android.os.Handler;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

public class API {

    public ArrayList<Race> actual() throws Exception {

        URL url = new URL("https://general-personal-app.onrender.com/api");

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        int maxRetries = 1;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                Request request = new Request.Builder().url(url).build();

                try (Response response = client.newCall(request).execute()) {
                    System.out.println("Status: " + response.code());

                    String body = response.body().string();
                    System.out.println("Response: " + body);

                    Gson gson = new Gson();
                    JsonObject json = gson.fromJson(body, JsonObject.class);

                    int count = json.get("count").getAsInt();
                    System.out.println("Race Amount: " + count);

                    JsonArray racesArray = json.getAsJsonArray("races");
                    ArrayList<Race> raceList = new ArrayList<>();

                    for (int i = 0; i < racesArray.size(); i++) {
                        Race race = gson.fromJson(racesArray.get(i), Race.class);
                        raceList.add(race);
                    }
                    db.races = raceList;
                    maxRetries = -1;
                    // Callback using the data that has been read in.
                }
            } catch (Exception e) {
                System.out.println("Attempt " + attempt + " failed: " + e.getMessage());
                if (attempt == maxRetries) throw e;
                Thread.sleep(2000);
            }
        }
        return db.races;
    }

    public void get_all_races(DataStatus<Race> dataStatus) throws Exception {
        new Thread(() -> {
            try {
                ArrayList<Race> races = actual(); // your OkHttp code
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    dataStatus.onDataLoaded(races);
                });

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    dataStatus.onError(e.getMessage());
                });
            }
        }).start();
    }



    public static void main(String[] args) throws Exception {

        API api = new API();
        api.get_all_races(new DataStatus<Race>() {
            @Override
            public void onDataLoaded(ArrayList<Race> arrayList) {
                System.out.println("Successful Retrieval of the races!");
            }

            @Override
            public void onError(String errorMessage) {
                System.out.println("Error on retrieval of the races!");
            }
        });

        for(Race race : db.races){
            System.out.println(race);
        }

        }

}
