package com.example.arsenal_app.database;

// Imports.
import android.os.Looper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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

    // List to store all callbacks while waiting for token retrieval.
    private final List<APICallback<String>> tokenCallbacksHolder = new ArrayList<>();
    // Flag to indicate if the token is being fetched.
    private boolean isFetchingToken = false;
    // Client object to perform HTTP requests to the API server.
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(0, TimeUnit.SECONDS)
            .build();
    // Handler object to allow for return on the main thread.
    private final android.os.Handler mainHandler = new android.os.Handler(Looper.getMainLooper());


    /**
     * Function to ensure the user has a valid token before an API call. Uses a list of callback
     * objects to ensure only one instance tries to access the token and improving efficiency.
     *
     * @param apiCallback Interface implementation to be used upon successful retrieval of the
     *                    users token.
     * @param dataStatus  Interface implementation to be used as a callback in the event of an
     *                    error to verify the users token.
     * @param <T> Class type of the data that is requested.
     */
    public <T> void getValidToken(APICallback<String> apiCallback, DataStatus<T> dataStatus) {

        // If the userID is already
        if (DataRepository.getInstance().getDbHelper().get_usid() == null) {
            // Check if the token is already being retrieved. - Add to holder if true otherwise
            // set flag and run the code to get the token.
            if (isFetchingToken){
                tokenCallbacksHolder.add(apiCallback);
                return;
            }
            isFetchingToken = true;
            FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                    .addOnCompleteListener(task -> {
                        // Upon token being retrieved, store and return via callback(s).
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            DataRepository.getInstance().getDbHelper().set_usid(idToken);

                            for (APICallback<String> apiCallback1 : tokenCallbacksHolder){
                                apiCallback1.onSuccess(idToken);
                            }
                            apiCallback.onSuccess(idToken);
                            isFetchingToken = false;
                        } else {
                            dataStatus.onError("Failure to verify user.");
                        }
                    });
        } else {
            // Return the already cached token.
            apiCallback.onSuccess(DataRepository.getInstance().getDbHelper().get_usid());
        }
    }

    /**
     * This function will connect to the URL and attempt to retrieve data.
     *
     * @param url The API url to be connected to.
     * @param token The users token to be sent as a header to the API for authenticated.
     * @param retriesLeft Integer of the number of retries left upon
     * @param callback Callback object to return the data from the URL.
     */
    private void fetchWithRetry(URL url, String token, int retriesLeft, BodyCallBack callback) {
        // Create the request.
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        // Create a call to the URL and retrieve the data and return via callback.
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
                    callback.onError("CLIENT NEW CALL ERROR: " + e);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                if (retriesLeft > 1) {
                    System.out.println("Attempt failed: " + e.getMessage() + ". Retrying...");
                    // Try again after delay.
                    new android.os.Handler(Looper.getMainLooper()).postDelayed(() ->
                            fetchWithRetry(url, token, retriesLeft - 1, callback), 2000);
                } else {
                    callback.onError("Fetch with retry error: " + e);
                }
            }
        });
    }

    /**
     * Function will connect to the url and retrieve the json data from the API and return it as
     * an ArrayList of Objects of type <T>.
     *
     * @param APIUrl URL of the API.
     * @param jsonArrayKey Key of the information.
     * @param clazz This is the class relating to the object to be created with the data.
     * @param callback Callback object to return the arrayList once the data has been read.
     * @param functionSetter Consumer object of the function that will cache the data once it
     *                       has been read.
     * @param <T> Type of the object.
     */
    public <T> void fetchData(String APIUrl,
                              String jsonArrayKey,
                              Class<T> clazz,
                              DataStatus<T> callback,
                              Consumer<ArrayList<T>> functionSetter) {
        // Get URL object.
        URL url = null;
        try {
            url = new URL(APIUrl);
        } catch (Exception e) {
            callback.onError(e.toString());
        }
        URL finalUrl = url;

        // Get the token of the user.
        getValidToken(new APICallback<String>() {
            @Override
            public void onSuccess(String usid) {
                int maxRetries = 2;
                // Fetch the information from the URL.
                fetchWithRetry(finalUrl, usid, maxRetries, new BodyCallBack() {
                    @Override
                    public void onSuccess(String body) {
                        // Set information to be an ArrayList of objects of clazz.
                        Gson gson = new Gson();
                        JsonObject json = gson.fromJson(body, JsonObject.class);

                        if (finalUrl.toString().contains("/football/")){
                            DataRepository.getInstance().getDbHelper().setTeam_base64(json.get("team_base64").toString());
                            System.out.println(DataRepository.getInstance().getDbHelper().getTeam_base64().length());
                        }

                        int count = json.get("count").getAsInt();
                        JsonArray itemArray = json.getAsJsonArray(jsonArrayKey);

                        ArrayList<T> arrayList = new ArrayList<>();
                        for (int i = 0; i < count; i++) {
                            T item = gson.fromJson(itemArray.get(i), clazz);
                            arrayList.add(item);
                        }

                        // Cache the data.
                        functionSetter.accept(arrayList);

                        // Return result on main thread. Main thread must deal with the data
                        // as only the main thread can deal with the views it created.
                        mainHandler.post(() -> callback.onDataLoaded(arrayList));
                    }
                    @Override
                    public void onError(String error) {
                        System.out.println("API BodyCallBack Error: "+error);
                    }
                });
            }
            @Override
            public void onError(Exception e) {
                System.out.println("API On Error: " + e);
            }
        }, callback);
    }
}
