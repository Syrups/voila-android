package com.tenveux.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.tenveux.app.models.User;

/**
 * Created by theGlenn on 13/10/2014.
 */
public class UserPreferences {

    public static final String PREFS_NAME = "UserPrefs";
    private static Gson gson = new Gson();
    private static Context context = ApplicationController.getInstance().getApplicationContext();
    public static final String EMPTY = "{error : 1}";


    public String getPrefrence() {

        // Restore preferences
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        String user = settings.getString(PREFS_NAME, "{error : 1}");

        return user;
    }

    public static User getSessionUser() {
        // Restore preferences
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        String user = settings.getString("User", EMPTY);

        Log.i("getting", user);

        return user.equals(EMPTY) ? null : gson.fromJson(user, User.class);
    }


    public static void savePreference(User user) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("User", gson.toJson(user));

        Log.i("saving", gson.toJson(user));

        // Commit the edits!
        editor.apply();
    }

    public static void savePreference(User user, SharedPreferences.OnSharedPreferenceChangeListener l) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("User", gson.toJson(user));

        Log.i("saving", gson.toJson(user));

        // Commit the edits!
        boolean done = editor.commit();
        Log.d("commit", "_" + done);
        settings.registerOnSharedPreferenceChangeListener(l);
    }
}
