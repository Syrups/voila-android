package com.tenveux.app.activities.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.tenveux.app.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends Activity {


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.inject(this);
    }

    @OnClick(R.id.login_button)
    void goLogin() {

        Intent mainIntent = new Intent(HomeActivity.this, LoginActivity.class);
        HomeActivity.this.startActivity(mainIntent);
        //HomeActivity.this.finish();
    }

    @OnClick(R.id.sign_in_button)
    void goSignIn() {
        Intent mainIntent = new Intent(HomeActivity.this, SignIn.class);
        HomeActivity.this.startActivity(mainIntent);
        //HomeActivity.this.finish();
    }
}
