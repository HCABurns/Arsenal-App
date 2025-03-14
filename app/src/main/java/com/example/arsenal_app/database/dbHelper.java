package com.example.arsenal_app.database;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class dbHelper {

    private String key = DBInfo.key;

    private FirebaseDatabase firebaseDatabase;

    private DatabaseReference games_database;

    private DatabaseReference id_database;
    private int id_counter = 0;

    public dbHelper(){
        System.out.println(DBInfo.key);
        firebaseDatabase = FirebaseDatabase.getInstance(DBInfo.key);
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
        id_database.setValue(10000);
        System.out.println("Sorted");

    }
}
