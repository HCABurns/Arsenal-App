package com.example.arsenal_app.database;

// Imports.
import android.os.Looper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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

    private final android.os.Handler mainHandler = new android.os.Handler(Looper.getMainLooper());



    /**
     * Function to ensure the user has a valid token before an API call.
     *
     * @param apiCallback
     * @param dataStatus  Interface implementation to be used as a callback in the event of an
     *                    error to verify the users token.
     * @param <T>
     */
    public <T> void getValidToken(APICallback<String> apiCallback, DataStatus<T> dataStatus) {
        if (DataRepository.getInstance().getDbHelper().get_usid() == null) {
            FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            DataRepository.getInstance().getDbHelper().set_usid(idToken);
                            apiCallback.onSuccess(idToken);
                        } else {
                            dataStatus.onError("Failure to verify user.");
                        }
                    });
        } else {
            apiCallback.onSuccess(DataRepository.getInstance().getDbHelper().get_usid());
        }
    }

    private void fetchWithRetry(OkHttpClient client, URL url, String token, int retriesLeft, BodyCallBack callback) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response){
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

    public <T> void fetchData(String urlString,
                              String jsonArrayKey,
                              Class<T> clazz,
                              DataStatus<T> callback,
                              Consumer<ArrayList<T>> functionSetter) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (Exception e) {
            callback.onError(e.toString());
        }
        URL finalUrl = url;
        getValidToken(new APICallback<String>() {
            @Override
            public void onSuccess(String usid) {
                int maxRetries = 3;
                fetchWithRetry(client, finalUrl, usid, maxRetries, new BodyCallBack() {
                    @Override
                    public void onSuccess(String body) {
                        Gson gson = new Gson();
                        JsonObject json = gson.fromJson(body, JsonObject.class);

                        int count = json.get("count").getAsInt();
                        JsonArray itemArray = json.getAsJsonArray(jsonArrayKey);

                        ArrayList<T> arrayList = new ArrayList<>();
                        for (int i = 0; i < count; i++) {
                            T item = gson.fromJson(itemArray.get(i), clazz);
                            arrayList.add(item);
                        }
                        functionSetter.accept(arrayList);

                        // Return result on main thread
                        mainHandler.post(() -> callback.onDataLoaded(arrayList));
                    }
                    @Override
                    public void onError(String error) {

                    }
                });
            }
            @Override
            public void onError(Exception e) {

            }
        }, callback);
    }
}
