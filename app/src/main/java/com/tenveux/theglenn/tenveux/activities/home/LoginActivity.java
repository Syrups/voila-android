package com.tenveux.theglenn.tenveux.activities.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.tenveux.theglenn.tenveux.ApplicationController;
import com.tenveux.theglenn.tenveux.R;
import com.tenveux.theglenn.tenveux.UserPreferences;
import com.tenveux.theglenn.tenveux.activities.MainActivity;
import com.tenveux.theglenn.tenveux.models.User;
import com.tenveux.theglenn.tenveux.widget.VoilaLoaderImageVIew;
import com.tenveux.theglenn.tenveux.widget.VoilaSeekBar;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends ActionBarActivity {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };


    @InjectView(R.id.logo)
    ImageView logo;

    @InjectView(R.id.login_button)
    Button mLoginButton;

    @InjectViews({R.id.user_name, R.id.password})
    List<EditText> mFieldsEditText;

    @InjectView(R.id.voila_progress)
    VoilaLoaderImageVIew mLoader;

    /**
     * Duration of wait *
     */
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    //private static final String TAG = "FacebookT";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.inject(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);


        /* and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // For scenarios where the main activity is launched and user
                // session is not null, the session state change notification
                // may not be triggered. Trigger it if it's open/closed.
                User user = UserPreferences.getSessionUser();
                if (user != null) {
                    goToMain(user);
                } else {
                    mLoginButton.setVisibility(View.VISIBLE);
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //NavUtils.navigateUpFromSameTask(this);
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }


    private void goToMain(User user) {

        if (user != null) {

            UserPreferences.savePreference(user);
            ApplicationController.getInstance().setUserToken(user.getToken());
        }

        ApplicationController.getInstance().setIsUserLoggedIn(true);

        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        LoginActivity.this.startActivity(mainIntent);
        LoginActivity.this.finish();
    }

    @OnClick(R.id.login_button)
    void login() {

        Map<String, String> datas = new LinkedHashMap<>();

        for (EditText ed : mFieldsEditText) {
            datas.put(ed.getTag().toString(), ed.getText().toString());
        }

        ApplicationController.userApi().authenticate(datas, new Callback<User>() {

            @Override
            public void success(User u, retrofit.client.Response response) {
                Log.d("logged", u.getName());
                goToMain(u);
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse() != null) {

                    User user = (User) error.getBodyAs(User.class);

                    Log.d("ERROR", error.getMessage());

                    switch (error.getResponse().getStatus()) {
                        case 409:
                            goToMain(user);
                            return;
                        case 404:
                            //Log.d("ERROR", gson.toJson(body));
                            error.printStackTrace();
                            return;
                    }
                }
            }
        });
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            /*mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });*/

            mLoader.setVisibility(show ? View.VISIBLE : View.GONE);
            //mLoader.animate();
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoader.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    void showKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.tenveux.theglenn.tenveux",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
}



