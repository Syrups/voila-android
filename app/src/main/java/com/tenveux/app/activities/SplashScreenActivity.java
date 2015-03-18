package com.tenveux.app.activities;

import com.tenveux.app.ApplicationController;
import com.tenveux.app.UserPreferences;
import com.tenveux.app.activities.home.HomeActivity;
import com.tenveux.app.activities.util.SystemUiHider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.tenveux.app.R;
import com.tenveux.app.models.User;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class SplashScreenActivity extends Activity {

    /**
     * Duration of wait *
     */
    private final int SPLASH_DISPLAY_LENGTH = 500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);
        final User user = UserPreferences.getSessionUser();

        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (ApplicationController.checkPlayServices(this)) {

            if (user != null)
                ApplicationController.registerInBackground(user.getId(), this.getApplicationContext());

             /* and close this Splash-Screen after some seconds.*/
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (user != null) {
                        goToMain(user);
                    } else {
                        goToHome();
                    }
                }
            }, SPLASH_DISPLAY_LENGTH);

        } else {
            Log.i("VOILA", "No valid Google Play Services APK found.");
        }
    }

    private void goToMain(User user) {

        ApplicationController.getInstance().setUserToken(user.getToken());

        Intent mainIntent = new Intent(this, MainActivity.class);
        this.startActivity(mainIntent);
        this.finish();
    }

    private void goToHome() {
        Intent mainIntent = new Intent(this, HomeActivity.class);
        this.startActivity(mainIntent);
        this.finish();
    }
}
