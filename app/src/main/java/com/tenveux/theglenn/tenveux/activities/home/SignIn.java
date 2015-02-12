package com.tenveux.theglenn.tenveux.activities.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.tenveux.theglenn.tenveux.ApplicationController;
import com.tenveux.theglenn.tenveux.R;
import com.tenveux.theglenn.tenveux.UserPreferences;
import com.tenveux.theglenn.tenveux.activities.MainActivity;
import com.tenveux.theglenn.tenveux.models.User;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectViews;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SignIn extends ActionBarActivity {

    @InjectViews({R.id.user_name, R.id.mail, R.id.password})
    List<EditText> fields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ButterKnife.inject(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
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

    @OnClick(R.id.button)
    void submit() {

        Map<String, String> datas = new LinkedHashMap<>();

        for (EditText ed : fields) {
            datas.put(ed.getTag().toString(), ed.getText().toString());
        }

        ApplicationController.userApi().createUser(datas, new Callback<User>() {

            @Override
            public void success(User user, Response response) {
                Log.d("done", user.getName());
                goToMain(user);
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse() != null) {
                    Response r = error.getResponse();


                    Log.d("ERROR", error.getMessage());

                    switch (r.getStatus()) {
                        case 409:
                            User user = (User) error.getBodyAs(User.class);

                            goToMain(user);
                            return;
                        case 404:
                            JsonElement body = (JsonElement) error.getBodyAs(JsonElement.class);
                            Gson gson = new Gson();

                            Log.e("ERROR", gson.toJson(body));

                            error.printStackTrace();
                            return;
                    }
                }
            }
        });
    }

    private void goToMain(User user) {

        if (user != null) {

            UserPreferences.savePreference(user);
            ApplicationController.getInstance().setUserToken(user.getToken());
        }

        ApplicationController.getInstance().setIsUserLoggedIn(true);

        Intent mainIntent = new Intent(SignIn.this, MainActivity.class);
        SignIn.this.startActivity(mainIntent);
        SignIn.this.finish();
    }

}
