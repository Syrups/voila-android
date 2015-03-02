package com.tenveux.theglenn.tenveux.activities;

import com.tenveux.theglenn.tenveux.ApplicationController;
import com.tenveux.theglenn.tenveux.UserPreferences;
import com.tenveux.theglenn.tenveux.activities.home.HomeActivity;
import com.tenveux.theglenn.tenveux.activities.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.tenveux.theglenn.tenveux.R;
import com.tenveux.theglenn.tenveux.models.User;

import java.util.LinkedHashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;

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
