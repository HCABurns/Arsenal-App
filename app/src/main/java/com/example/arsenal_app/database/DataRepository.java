package com.example.arsenal_app.database;

import com.example.arsenal_app.models.Game;
import com.example.arsenal_app.models.Race;

import java.util.ArrayList;
import java.util.function.Consumer;

public class DataRepository {

    public static DataRepository instance;
    private final DBHelper dbHelper;
    private final API api;

    private DataRepository() {
        dbHelper = new DBHelper();
        api = new API();
    }

    public static synchronized DataRepository getInstance() {
        if (instance == null) {
            instance = new DataRepository();
        }
        return instance;
    }

    public DBHelper getDbHelper() {
        return dbHelper;
    }

    public API getApi() {
        return api;
    }

    public <T> void loadAllEpicGames(String endPoint,String jsonArrayKey, Class<T> clazz, DataStatus<T> callback, Consumer<ArrayList<T>> functionSetter) {
        if (dbHelper.getEpicGames() != null && !dbHelper.getEpicGames().isEmpty()) {
            System.out.println("EpicGames in DB");
            callback.onDataLoaded((ArrayList<T>) DataRepository.getInstance().getDbHelper().getEpicGames());
        } else {
            System.out.println("Fetching EpicGames from DB: ");
            api.fetchData(endPoint, jsonArrayKey, clazz, callback, functionSetter);
        }
    }

    public <T> void loadAllFootballGames(String endPoint,String jsonArrayKey, Class<T> clazz, DataStatus<T> callback, Consumer<ArrayList<T>> functionSetter) {
        if (dbHelper.getGames() != null && !dbHelper.getGames().isEmpty()) {
            callback.onDataLoaded((ArrayList<T>) DataRepository.getInstance().getDbHelper().getGames());
        } else {
            api.fetchData(endPoint, jsonArrayKey, clazz, callback, functionSetter);
        }
    }

    public <T> void loadAllRaces(String endPoint,String jsonArrayKey, Class<T> clazz, DataStatus<T> callback, Consumer<ArrayList<T>> functionSetter) {
        if (dbHelper.getRaces() != null && !dbHelper.getRaces().isEmpty()) {
            callback.onDataLoaded((ArrayList<T>) DataRepository.getInstance().getDbHelper().getRaces());
        } else {
            api.fetchData(endPoint, jsonArrayKey, clazz, callback, functionSetter);
        }
    }
}
