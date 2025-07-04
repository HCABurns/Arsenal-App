package com.example.arsenal_app.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.arsenal_app.R;
import com.example.arsenal_app.database.DataRepository;
import com.example.arsenal_app.fragments.EpicFragment;
import com.example.arsenal_app.fragments.HomeFragment;
import com.example.arsenal_app.fragments.FutureFragment;
import com.example.arsenal_app.fragments.NextRaceFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/**
 * Class for storing the main page.
 * An activity is for the entire page. Fragments are UI for dynamic data.
 */
public class MainActivity extends AppCompatActivity {

    // Required variables.
    private Fragment previousFragment = null;
    private TextView title;
    /**
     * Executed on startup and sets the banner and navigation.
     * @param savedInstanceState ?
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get currentUser, if not then start the login activity.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            // No logged-in user -> redirect to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Prevent user from returning here
            return; // Don't proceed to load UI
        }

        // Set user preferences.
        DataRepository.getInstance().getSettingsManager().setPrefs(MainActivity.this);

        // Sets the banner and navigation bar.
        setContentView(R.layout.activity_main);
        // Initialize the Firebase app.
        FirebaseApp.initializeApp(this);



        // Get the title bar and sets in data repo.
        title = findViewById(R.id.pageTitle);

        // Create fragments that can be used for filling the fragment container.
        HomeFragment homeFragment = new HomeFragment();
        FutureFragment futureFragment = new FutureFragment();
        EpicFragment epicFragment = new EpicFragment();
        NextRaceFragment nextRaceFragment = new NextRaceFragment();

        // Create an item to get the bottom navigation. Set the active item to an invisible item
        // for aesthetics.
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.invisible);

        // Create a fragment manager and swap the empty frame to the home fragment frame.
        // *New* - Using add, hide and commit - Does not call onCreateView for each Fragment when
        // loading but instead will just store, hide and show each page.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, homeFragment);
        transaction.add(R.id.fragment_container, epicFragment).hide(epicFragment);
        transaction.add(R.id.fragment_container, futureFragment).hide(futureFragment);
        transaction.add(R.id.fragment_container, nextRaceFragment).hide(nextRaceFragment).commit();
        previousFragment = homeFragment;

        //Create an onclick to replace the frame with the correct required frame.
        bottomNavigationView.setOnItemSelectedListener(item -> {
            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            if (item.getItemId() == R.id.home_nav_bar) {
                // Set new title.
                title.setText("Next Game Information");
                // Change Fragment.
                t.hide(previousFragment).show(homeFragment).commit();
                previousFragment = homeFragment;
                return true;
            }
            else if (item.getItemId() == R.id.next_nav_bar) {
                title.setText("Future Games");
                t.hide(previousFragment).show(futureFragment).commit();
                previousFragment = futureFragment;
                return true;
            }
            else if (item.getItemId() == R.id.epic_game_nav_bar){
                title.setText((String)"Free Epic Games");
                t.hide(previousFragment).show(epicFragment).commit();
                previousFragment = epicFragment;
                return true;
            }
            else if (item.getItemId() == R.id.next_race_nav_bar){
                title.setText((String)"F1 Information");
                t.hide(previousFragment).show(nextRaceFragment).commit();
                previousFragment = nextRaceFragment;
                return true;
            }
            else {
                return false;
            }
        });

        Button settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });


    }
}