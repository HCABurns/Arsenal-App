package com.example.arsenal_app.database;

import androidx.annotation.NonNull;

import com.example.arsenal_app.models.EpicGame;
import com.example.arsenal_app.models.Game;
import com.example.arsenal_app.models.Race;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DBHelper {

    // Required variables for the database and storage.
    private ArrayList<Game> games = new ArrayList<>();
    private ArrayList<EpicGame> epicGames = new ArrayList<>();
    private ArrayList<Race> races = new ArrayList<>();
    private String usid;

    public DBHelper(){
    }

    public ArrayList<Game> getGames() {
        return games;
    }

    public void setGames(ArrayList<Game> games) {
        this.games = games;
    }

    public ArrayList<EpicGame> getEpicGames() {
        return epicGames;
    }

    public void setEpicGames(ArrayList<EpicGame> epicGames) {
        this.epicGames = epicGames;
    }

    public ArrayList<Race> getRaces() {
        return races;
    }

    public void setRaces(ArrayList<Race> races) {
        this.races = races;
    }

    public void set_usid(String idToken){
        this.usid = idToken;
    }

    public String get_usid(){
        return this.usid;
    }

}
