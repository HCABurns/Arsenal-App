package com.example.arsenal_app.database;

import com.example.arsenal_app.models.EpicGame;
import com.example.arsenal_app.models.Game;
import com.example.arsenal_app.models.Race;

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

    public void loadAllEpicGames(DataStatus<EpicGame> callback) {
        if (dbHelper.getGames() != null && !dbHelper.getEpicGames().isEmpty()) {
            callback.onDataLoaded(dbHelper.getEpicGames());
        } else {
            api.allEpicGamesApiAsync(callback);
        }
    }

    public void loadAllFootballGames(DataStatus<Game> callback) {
        if (dbHelper.getGames() != null && !dbHelper.getEpicGames().isEmpty()) {
            callback.onDataLoaded(dbHelper.getGames());
        } else {
            api.allFootballGamesApiAsync(callback);
        }
    }

    public void loadAllRaces(DataStatus<Race> callback) {
        if (dbHelper.getGames() != null && !dbHelper.getEpicGames().isEmpty()) {
            callback.onDataLoaded(dbHelper.getRaces());
        } else {
            api.allRacesApiAsync(callback);
        }
    }
}
