package com.tenveux.theglenn.tenveux;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.*;
import com.facebook.*;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.*;
import com.facebook.widget.LoginButton;
import com.google.gson.Gson;
import com.tenveux.theglenn.tenveux.apimodel.CreateUserResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.QueryMap;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    @InjectView(R.id.login_progress)
    View mProgressView;

    @InjectView(R.id.logo)
    ImageView logo;

    @InjectView(R.id.login_button)
    LoginButton mLoginButton;


    public boolean isLogged = false;

    /**
     * Duration of wait *
     */
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    private static final String TAG = "FacebookT";
    private UiLifecycleHelper uiHelper;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.


        // mProgressView = findViewById(R.id.login_progress);

        ButterKnife.inject(this);

        showKeyHash();

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        mLoginButton.setReadPermissions(Arrays.asList("user_likes", "user_status", "user_friends"));

        /* and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // For scenarios where the main activity is launched and user
                // session is not null, the session state change notification
                // may not be triggered. Trigger it if it's open/closed.
                Session session = Session.getActiveSession();
                if (session != null &&
                        (session.isOpened() || session.isClosed())) {
                    onSessionStateChange(session, session.getState(), null);
                } else {
                    mLoginButton.setVisibility(View.VISIBLE);
                }

            }
        }, SPLASH_DISPLAY_LENGTH);


        uiHelper.onResume();


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

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private void goToMain(CreateUserResponse user) {

        if (user != null) {

            UserPreferences.savePreference(user);
            ApplicationController.getInstance().setUserToken(user.getToken());
        }

        ApplicationController.getInstance().setIsUserLoggedIn(true);

        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        LoginActivity.this.startActivity(mainIntent);
        LoginActivity.this.finish();
    }

    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (state.isOpened() && !isLogged) {
            Log.i(TAG, "Logged in...");
            // Request user data and show the results
            isLogged = true;

            boolean connection = true;

            if (connection)
                Request.newMeRequest(session, new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (user != null) {

                            Map<String, String> datas = new LinkedHashMap<String, String>();
                            datas.put("facebook_id", user.getId());
                            datas.put("name", user.getName());

                            Log.d("Me", "ME?");
                            ApplicationController.api().createUser(datas, new Callback<CreateUserResponse>() {

                                @Override
                                public void success(CreateUserResponse o, retrofit.client.Response response) {
                                    goToMain(o);
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    if (error.getResponse() != null) {
                                        CreateUserResponse body = (CreateUserResponse) error.getBodyAs(CreateUserResponse.class);
                                        Log.d("ERROR", error.getMessage());

                                        switch (body.getCode()) {
                                            case 409:
                                                goToMain(body);
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
                    }
                }).executeAsync();
            else
                goToMain(null);


        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
            mLoginButton.setVisibility(View.VISIBLE);

        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
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

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private String buildUserInfoDisplay(GraphUser user) {
        StringBuilder userInfo = new StringBuilder("");

        // Example: typed access (name)
        // - no special permissions required
        userInfo.append(String.format("Name: %s\n\n",
                user.getName()));

        // Example: typed access (birthday)
        // - requires user_birthday permission
        userInfo.append(String.format("Birthday: %s\n\n",
                user.getBirthday()));

        // Example: partially typed access, to location field,
        // name key (location)
        // - requires user_location permission
        userInfo.append(String.format("Location: %s\n\n",
                user.getLocation().getProperty("name")));

        // Example: access via property name (locale)
        // - no special permissions required
        userInfo.append(String.format("Locale: %s\n\n",
                user.getProperty("locale")));

        // Example: access via key for array (languages)
        // - requires user_likes permission
        JSONArray languages = (JSONArray) user.getProperty("languages");
        if (languages.length() > 0) {
            ArrayList<String> languageNames = new ArrayList<String>();
            for (int i = 0; i < languages.length(); i++) {
                JSONObject language = languages.optJSONObject(i);
                // Add the language name to a list. Use JSON
                // methods to get access to the name field.
                languageNames.add(language.optString("name"));
            }
            userInfo.append(String.format("Languages: %s\n\n",
                    languageNames.toString()));
        }

        return userInfo.toString();
    }

}



