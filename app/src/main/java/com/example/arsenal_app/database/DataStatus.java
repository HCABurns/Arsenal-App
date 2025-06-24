package com.example.arsenal_app.database;

import java.util.ArrayList;

/**
 * This is the interface for the callback using the database.
 * OnDataLoaded - This will be called by the dbHelper if the data has been successfully read in.
 *                the fragment will then do whatever is required to the data to display it.
 * onError - This is called by the dbHelper if the data has not been read in - For whatever reason.
 */
public interface DataStatus<T> {

    /**
     * Function that would be run when data has been returned.
     *
     * @param dataList - ArrayList containing Game objects.
     */

    void onDataLoaded(ArrayList<T> dataList);

    /**
     * Function that would run if there is an error connecting to the database or storing the data.
     *
     * @param errorMessage - Message that will be output to the user.
     */
    void onError(String errorMessage);
}