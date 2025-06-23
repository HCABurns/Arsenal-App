package com.example.arsenal_app.database;

// Imports.
import static com.example.arsenal_app.Activities.MainActivity.db;

import android.os.Looper;

import com.example.arsenal_app.models.EpicGame;
import com.example.arsenal_app.models.Game;
import com.example.arsenal_app.models.Race;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    /**
     * Function to ensure the user has a valid token before an API call.
     *
     * @param apiCallback
     * @param dataStatus  Interface implementation to be used as a callback in the event of an
     *                    error to verify the users token.
     * @param <T>
     */
    public <T> void getValidToken(APICallback<String> apiCallback, DataStatus<T> dataStatus) {
        if (db.get_usid() == null) {
            FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            db.set_usid(idToken);
                            apiCallback.onSuccess(idToken);
                        } else {
                            dataStatus.onError("Failure to verify user.");
                        }
                    });
        } else {
            apiCallback.onSuccess(db.get_usid());
        }
    }

    private void fetchWithRetry(OkHttpClient client, URL url, String token, int retriesLeft, BodyCallBack callback) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        callback.onError(("Unexpected code " + response.code()));
                        return;
                    }

                    String body = responseBody.string();
                    callback.onSuccess(body);

                } catch (Exception e) {
                    callback.onError(e.toString());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                if (retriesLeft > 1) {
                    System.out.println("Attempt failed: " + e.getMessage() + ". Retrying...");
                    // Try again after delay
                    new android.os.Handler(Looper.getMainLooper()).postDelayed(() ->
                            fetchWithRetry(client, url, token, retriesLeft - 1, callback), 2000);
                } else {
                    callback.onError(e.toString());
                }
            }
        });
    }

    public void allRacesApiAsync(DataStatus<Race> callback) {
        URL url;
        try {
            url = new URL("https://general-personal-app.onrender.com/api/f1");
        } catch (Exception e) {
            callback.onError(e.toString());
            return;
        }
        getValidToken(new APICallback<String>() {
            @Override
            public void onSuccess(String usid) {
                int maxRetries = 3;
                fetchWithRetry(client, url, usid, maxRetries, new BodyCallBack() {
                    @Override
                    public void onSuccess(String body) {
                        Gson gson = new Gson();
                        JsonObject json = gson.fromJson(body, JsonObject.class);

                        int count = json.get("count").getAsInt();
                        JsonArray racesArray = json.getAsJsonArray("races");

                        ArrayList<Race> raceList = new ArrayList<>();
                        for (int i = 0; i < count; i++) {
                            Race race = gson.fromJson(racesArray.get(i), Race.class);
                            raceList.add(race);
                        }
                        db.setRaces(raceList);

                        // Return result on main thread
                        new android.os.Handler(Looper.getMainLooper()).post(() -> callback.onDataLoaded(raceList));
                    }

                    @Override
                    public void onError(String error) {
                        callback.onError(error);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e.toString());
            }
        }, callback);
    }


    public void allEpicGamesApiAsync(DataStatus<EpicGame> callback) {
        URL url;
        try {
            url = new URL("https://general-personal-app.onrender.com/api/epic_games");
        } catch (Exception e) {
            callback.onError(e.toString());
            return;
        }
        getValidToken(new APICallback<String>() {
            @Override
            public void onSuccess(String usid) {
                int maxRetries = 3;
                fetchWithRetry(client, url, usid, maxRetries, new BodyCallBack() {
                    @Override
                    public void onSuccess(String body) {
                        Gson gson = new Gson();
                        JsonObject json = gson.fromJson(body, JsonObject.class);

                        int count = json.get("count").getAsInt();
                        JsonArray epicGamesArray = json.getAsJsonArray("epic_games");

                        ArrayList<EpicGame> epicGamesList = new ArrayList<>();
                        for (int i = 0; i < count; i++) {
                            EpicGame game = gson.fromJson(epicGamesArray.get(i), EpicGame.class);
                            epicGamesList.add(game);
                        }
                        db.setEpicGames(epicGamesList);

                        // Return result on main thread
                        new android.os.Handler(Looper.getMainLooper()).post(() -> callback.onDataLoaded(epicGamesList));
                    }

                    @Override
                    public void onError(String error) {
                        callback.onError(error);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e.toString());
            }
        }, callback);
    }


    public void allFootballGamesApiAsync(DataStatus<Game> callback) {
        URL url;
        try {
            url = new URL("https://general-personal-app.onrender.com/api/football");
        } catch (Exception e) {
            callback.onError(e.toString());
            return;
        }
        getValidToken(new APICallback<String>() {
            @Override
            public void onSuccess(String usid) {
                int maxRetries = 3;
                fetchWithRetry(client, url, usid, maxRetries, new BodyCallBack() {
                    @Override
                    public void onSuccess(String body) {
                        Gson gson = new Gson();
                        JsonObject json = gson.fromJson(body, JsonObject.class);

                        int count = json.get("count").getAsInt();
                        JsonArray footballGamesArray = json.getAsJsonArray("football");

                        ArrayList<Game> footballGamesList = new ArrayList<>();
                        for (int i = 0; i < count; i++) {
                            Game game = gson.fromJson(footballGamesArray.get(i), Game.class);
                            footballGamesList.add(game);
                        }
                        db.setGames(footballGamesList);

                        // Return result on main thread
                        new android.os.Handler(Looper.getMainLooper()).post(() -> callback.onDataLoaded(footballGamesList));
                    }

                    @Override
                    public void onError(String error) {
                        callback.onError(error);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e.toString());
            }
        }, callback);
    }
}
