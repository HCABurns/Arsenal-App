package com.example.arsenal_app.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import com.example.arsenal_app.R;
import com.example.arsenal_app.database.dbHelper;
import com.example.arsenal_app.fragments.HomeFragment;
import com.example.arsenal_app.fragments.RecentFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;


/**
 * Class for storing the main page.
 *
 * An activity is for the entire page. Fragments are UI for dynamic data.
 */
public class MainActivity extends AppCompatActivity {

    private static dbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this); // Crucial: Initialize Firebase FIRST
        dbHelper db = new dbHelper();

        // Create fragments that can be used for filling the fragment container.
        HomeFragment homeFragment = new HomeFragment();
        RecentFragment recentFragment = new RecentFragment();

        //Create a fragment manager and swap the empty frame to the home fragment frame.
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, homeFragment).commit();

        //Create an item to get the bottom navigation. Set the active item to an invisible item
        //for aesthetics.
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.invisible);

        //Create an onclick to replace the frame with the correct required frame.
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home_nav_bar) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, homeFragment)
                        .commit();
                return true;
            }
            else if (item.getItemId() == R.id.recent_nav_bar) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, recentFragment)
                        .commit();
                return true;
            }
            else{
                return false;
            }
        });
    }
}