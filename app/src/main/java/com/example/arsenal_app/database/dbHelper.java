package com.example.arsenal_app.database;

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

        firebaseDatabase = FirebaseDatabase.getInstance(DBInfo.key);
        games_database = firebaseDatabase.getReference(DBInfo.db_name);
        id_database = firebaseDatabase.getReference(DBInfo.db_name2).child("id");
        id_database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                id_counter = dataSnapshot.getValue(Integer.class);
                System.out.println(id_counter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

    }


    public static void main(String[] args) {

        dbHelper dbHelper = new dbHelper();
        dbHelper.id_database.setValue(0);

    }

}
