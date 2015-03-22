package com.tenveux.app.activities.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tenveux.app.ApplicationController;
import com.tenveux.app.R;
import com.tenveux.app.UserPreferences;
import com.tenveux.app.Utils;
import com.tenveux.app.activities.MainActivity;
import com.tenveux.app.models.User;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignIn extends ActionBarActivity {

    @InjectViews({R.id.user_name, R.id.mail, R.id.password})
    List<EditText> fields;

    @InjectView(R.id.voila_progress)
    ViewGroup mLoader;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ButterKnife.inject(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        fields.get(2).setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == EditorInfo.IME_ACTION_NEXT) {
                    //SignIn.this.submit();
                    return false;
                }

                return false;
            }

        });
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
        mLoader.setVisibility(View.VISIBLE);

        Map<String, String> data = new LinkedHashMap<>();
        String mailError = getResources().getString(R.string.error_not_email);

        for (EditText ed : fields) {
            String value = ed.getText().toString();
            String tag = ed.getTag().toString();
            if (!value.isEmpty()) {

                //Check if email
                if (tag.equals("email") && !Utils.isEmailValid(value)) {
                    Toast.makeText(this, value + mailError, Toast.LENGTH_LONG).show();
                    mLoader.setVisibility(View.GONE);
                    return;
                }

                data.put(ed.getTag().toString(), ed.getText().toString());
            }
        }

        if (data.size() > fields.size()) {
            ApplicationController.userApi().createUser(data, new Callback<User>() {

                @Override
                public void success(User user, Response response) {
                    goToMain(user);
                }

                @Override
                public void failure(RetrofitError error) {
                    if (error.getResponse() != null) {
                        Response r = error.getResponse();
                        switch (r.getStatus()) {
                            //Conflict error user already exist
                            case 409:
                                Toast.makeText(SignIn.this, R.string.error_user_exist, Toast.LENGTH_LONG).show();
                                //User user = (User) error.getBodyAs(User.class);
                                //goToMain(user);
                                return;
                            case 404:
                                mLoader.setVisibility(View.GONE);
                                error.printStackTrace();
                                return;
                        }
                    }
                }
            });
        }
    }

    private void goToMain(User user) {
        mLoader.setVisibility(View.GONE);

        if (user != null) {

            UserPreferences.savePreference(user);
            ApplicationController.getInstance().setUserToken(user.getToken());
        }

        Intent mainIntent = new Intent(SignIn.this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        SignIn.this.startActivity(mainIntent);
        SignIn.this.finish();
    }
}
