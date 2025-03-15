package com.example.arsenal_app.database;

import androidx.annotation.NonNull;

import com.example.arsenal_app.models.Game;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class dbHelper {

    private String key = DBInfo.key;

    public ArrayList<Game> games = new ArrayList<>();

    private FirebaseDatabase firebaseDatabase;

    private DatabaseReference games_database;

    private DatabaseReference id_database;
    private int id_counter = 0;

    public dbHelper(){
        System.out.println(DBInfo.key);
        firebaseDatabase = FirebaseDatabase.getInstance(key);
        games_database = firebaseDatabase.getReference(DBInfo.db_name);
        id_database = firebaseDatabase.getReference(DBInfo.db_name2);
        id_database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(id_counter);
                id_counter = dataSnapshot.getValue(Integer.class);
                System.out.println(id_counter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
        //id_database.setValue(10000);
        //System.out.println("Sorted");

        //setGames();
        System.out.println("Finished");
        System.out.println(games.size());

    }

    public void setGames() {
        games_database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                games.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    System.out.println(ds);
                    Game game = ds.getValue(Game.class);
                    games.add(game);
                    System.out.println("Game added: " + games.size());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


    }
}
