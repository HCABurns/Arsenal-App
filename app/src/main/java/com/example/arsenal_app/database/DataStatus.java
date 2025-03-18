package com.example.arsenal_app.database;

import com.example.arsenal_app.models.Game;

import java.util.ArrayList;

/**
 * This is the interface for the Callback using the database.
 *
 * OnDataLoaded - This will be called by the dbHelper if the data has been successfully read in.
 *                the fragment will then do whatever is required to the data to display it.
 *
 * onError - This is called by the dbHelper if the data has not been read in - For whatever reason.
 */
public interface DataStatus {
    void onDataLoaded(ArrayList<Game> dataList); // Called when data is ready
    void onError(String errorMessage); // Called if there is an error
}