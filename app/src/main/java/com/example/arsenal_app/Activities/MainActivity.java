package com.example.arsenal_app.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import com.example.arsenal_app.R;
import com.example.arsenal_app.database.DBHelper;
import com.example.arsenal_app.fragments.EpicFragment;
import com.example.arsenal_app.fragments.HomeFragment;
import com.example.arsenal_app.fragments.FutureFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;


/**
 * Class for storing the main page.
 * An activity is for the entire page. Fragments are UI for dynamic data.
 */
public class MainActivity extends AppCompatActivity {

    // Required variables.
    public static DBHelper db = new DBHelper();
    private Fragment previousFragment = null;

    /**
     * Executed on startup and sets the banner and navigation.
     * @param savedInstanceState ?
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the Firebase app.
        FirebaseApp.initializeApp(this);

        // Create fragments that can be used for filling the fragment container.
        HomeFragment homeFragment = new HomeFragment();
        FutureFragment futureFragment = new FutureFragment();
        EpicFragment epicFragment = new EpicFragment();

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
        transaction.add(R.id.fragment_container, futureFragment).hide(futureFragment).commit();
        previousFragment = homeFragment;

        //Create an onclick to replace the frame with the correct required frame.
        bottomNavigationView.setOnItemSelectedListener(item -> {
            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            if (item.getItemId() == R.id.home_nav_bar) {
                t.hide(previousFragment).show(homeFragment).commit();
                previousFragment = homeFragment;
                return true;
            }
            else if (item.getItemId() == R.id.recent_nav_bar) {
                t.hide(previousFragment).show(futureFragment).commit();
                previousFragment = futureFragment;
                return true;
            }
            else if (item.getItemId() == R.id.epic_game_nav_bar){
                t.hide(previousFragment).show(epicFragment).commit();
                previousFragment = epicFragment;
                return true;
            }
            else {
                return false;
            }
        });
    }
}