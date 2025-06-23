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
    public ArrayList<Game> games = new ArrayList<>();
    public ArrayList<EpicGame> epicGames = new ArrayList<>();
    public ArrayList<Race> races = new ArrayList<>();

    private FirebaseDatabase firebaseDatabase;

    private DatabaseReference footballGamesDatabase;
    private DatabaseReference epicGamesDatabase;
    private String usid;

    public DBHelper(){
        // Define the references to the database.
        firebaseDatabase = FirebaseDatabase.getInstance(DBInfo.key);
        footballGamesDatabase = firebaseDatabase.getReference(DBInfo.dbName);
        epicGamesDatabase = firebaseDatabase.getReference(DBInfo.dbName2);
    }


    /**
     * This is a function to retrieve all the games and return with a callback.
     * This means the data can be waited on instead of trying to access without being loaded.
     * Once the data is loaded it will run a function in the Fragment so the data can be displayed.
     *
     * @param dataStatus Implementation of the DataStatus interface that will deal with data
     *                   when it has been read in or in the event of an error occuring.
     */
    public void fetchData(DataStatus<Game> dataStatus) {
        // Retrieve the data from the database.
        footballGamesDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Empty array list.
                games.clear();

                // Populate the array list with games retrieved from the database.
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Game game = ds.getValue(Game.class);
                    games.add(game);
                }

                // APICallback using the data that has been read in.
                dataStatus.onDataLoaded(games);
            }

            /**
             * APICallback with an error message if an error occurs.
             * @param databaseError A description of the error that occurred.
             */
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // APICallback with the error message.
                dataStatus.onError(databaseError.getMessage());
            }
        });

    }

    public void fetchEpicData(DataStatus<EpicGame> dataStatus) {
        // Retrieve the data from the database.
        epicGamesDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Empty array list.
                epicGames.clear();

                // Populate the array list with games retrieved from the database.
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    EpicGame game = ds.getValue(EpicGame.class);
                    epicGames.add(game);
                    System.out.println(game.toString());
                }

                // APICallback using the data that has been read in.
                dataStatus.onDataLoaded(epicGames);
            }

            /**
             * APICallback with an error message if an error occurs.
             * @param databaseError A description of the error that occurred.
             */
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // APICallback with the error message.
                dataStatus.onError(databaseError.getMessage());
            }
        });

    }


    /**
     * Function to asynchronously retrieve all the data from the database.
     */
    public void setGames() {
        footballGamesDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                games.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Game game = ds.getValue(Game.class);
                    games.add(game);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void set_usid(String idToken){
        this.usid = idToken;
    }

    public String get_usid(){
        return this.usid;
    }

}
