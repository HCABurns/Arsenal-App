package com.example.arsenal_app.database;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.arsenal_app.Activities.MainActivity;

public class SettingsManager {

    private SharedPreferences prefs;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    SettingsManager(){
    }

    public void setPrefs(Context context){

        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        listener = (prefs, key) -> {
            System.out.println("Preference changed: " + key);

            if ("default_team".equals(key)) {
                String newTeam = prefs.getString("default_team", null);
                if (newTeam != null) {
                    System.out.println("Team changed to: " + newTeam);
                    // Your logic here
                }
            }
        };

        prefs.registerOnSharedPreferenceChangeListener(listener);

    }

    public String getTeam(){
        return prefs.getString("default_team", "default_value");
    }

    public void cleanup() {
        if (prefs != null && listener != null) {
            prefs.unregisterOnSharedPreferenceChangeListener(listener);
        }
    }

}
