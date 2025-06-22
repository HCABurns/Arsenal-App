package com.example.arsenal_app.database;

// Imports.
import static com.example.arsenal_app.Activities.MainActivity.db;

import android.adservices.adselection.AdSelectionOutcome;
import android.os.Looper;

import com.example.arsenal_app.models.Race;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import android.os.Handler;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

/**
 * This class deals with any calls to the API. It's purpose is to connect to the API, retrieve the
 * data and sent it to whichever function asked for it to then be processed.
 */
public class API {

    public void allRacesApiAsync(Callback<ArrayList<Race>> callback) {
        URL url;
        try {
            url = new URL("https://general-personal-app.onrender.com/api");
        } catch (Exception e) {
            callback.onError(e);
            return;
        }

        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String idToken = task.getResult().getToken();
                        db.set_usid(idToken);
                        System.out.println("Token set! " + idToken);

                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(60, TimeUnit.SECONDS)
                                .readTimeout(90, TimeUnit.SECONDS)
                                .writeTimeout(60, TimeUnit.SECONDS)
                                .build();

                        int maxRetries = 3;
                        fetchWithRetry(client, url, idToken, maxRetries, callback);
                    } else {
                        callback.onError(task.getException());
                    }
                });
    }

    private void fetchWithRetry(OkHttpClient client, URL url, String token, int retriesLeft, Callback<ArrayList<Race>> callback) {
        System.out.println("Token: " + token);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (retriesLeft > 1) {
                    System.out.println("Attempt failed: " + e.getMessage() + ". Retrying...");
                    // Try again after delay
                    new android.os.Handler(Looper.getMainLooper()).postDelayed(() ->
                            fetchWithRetry(client, url, token, retriesLeft - 1, callback), 2000);
                } else {
                    callback.onError(e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        callback.onError(new IOException("Unexpected code " + response.code()));
                        return;
                    }

                    String body = responseBody.string();
                    Gson gson = new Gson();
                    JsonObject json = gson.fromJson(body, JsonObject.class);

                    int count = json.get("count").getAsInt();
                    JsonArray racesArray = json.getAsJsonArray("races");

                    ArrayList<Race> raceList = new ArrayList<>();
                    for (int i = 0; i < count; i++) {
                        Race race = gson.fromJson(racesArray.get(i), Race.class);
                        raceList.add(race);
                    }

                    db.races = raceList;

                    // Return result on main thread
                    new android.os.Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(raceList));
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }




    public static void main(String[] args) throws Exception {
        /*
        Testing to ensure the API functionality works correctly.
         */

        /*
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
        */
    }
}
