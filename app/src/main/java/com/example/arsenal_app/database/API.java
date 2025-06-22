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

/**
 * This class deals with any calls to the API. It's purpose is to connect to the API, retrieve the
 * data and sent it to whichever function asked for it to then be processed.
 */
public class API {

    public ArrayList<Race> all_races_api() throws Exception {

        // URL to the api to return all the data.
        URL url = new URL("https://general-personal-app.onrender.com/api");

        // Set connect time to be long due to Render cold-start if the API hasn't been called for
        // a period of time.
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        // TODO: Convert the retries to be in the Fragment code, allow for display of {tried 1/ 3}...
        // Allow for 3 tries to connect to the API.
        int maxRetries = 1;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                // Create a request object.
                Request request = new Request.Builder().url(url).build();

                // Call the API and wait for a response.
                try (Response response = client.newCall(request).execute()) {
                    // Convert the return code to JSON.
                    String body = response.body().string();
                    Gson gson = new Gson();
                    JsonObject json = gson.fromJson(body, JsonObject.class);

                    // Get the information from the JSON.
                    int count = json.get("count").getAsInt();
                    JsonArray racesArray = json.getAsJsonArray("races");

                    // Convert the information to Race objects and store in ArrayList and the db.
                    ArrayList<Race> raceList = new ArrayList<>();
                    for (int i = 0; i < count; i++) {
                        Race race = gson.fromJson(racesArray.get(i), Race.class);
                        raceList.add(race);
                    }
                    db.races = raceList;
                    maxRetries = -1;

                }
            } catch (Exception e) {
                // In the event it fails, wait and try again until maxRetires. Otherwise raise exception.
                System.out.println("Attempt " + attempt + " failed: " + e.getMessage());
                if (attempt == maxRetries) throw e;
                Thread.sleep(2000);
            }
        }
        // Return the races.
        return db.races;
    }

    /**
     * Function to get all race information via API.
     * @param dataStatus Object used for callback.
     */
    public void get_all_races(DataStatus<Race> dataStatus) {
        // Start a new background thread to perform the network operation.
        new Thread(() -> {
            try {
                // Perform the network API call to retrieve the list of races.
                ArrayList<Race> races = all_races_api();

                // Create a Handler tied to the main (UI) thread.
                // This allows of posting a task that updates UI components or triggers callbacks safely.
                Handler handler = new Handler(Looper.getMainLooper());

                // Post the success callback to run on the main thread.
                handler.post(() -> {
                    dataStatus.onDataLoaded(races);
                });

            } catch (Exception e) {
                // If an error occurs, also post the error callback to the main thread.
                new Handler(Looper.getMainLooper()).post(() -> {
                    dataStatus.onError(e.getMessage());
                });
            }
        }).start();
    }



    public static void main(String[] args) throws Exception {
        /*
        Testing to ensure the API functionality works correctly.
         */

        // Create API object.
        API api = new API();

        // Call the function to get the information. DataStatus is used for callback once the data
        // has been loaded or failed to load.
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
        }
}
