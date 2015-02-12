package com.tenveux.theglenn.tenveux.activities.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tenveux.theglenn.tenveux.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends Activity {

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
