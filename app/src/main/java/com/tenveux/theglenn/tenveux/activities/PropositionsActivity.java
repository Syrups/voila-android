package com.tenveux.theglenn.tenveux.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.tenveux.theglenn.tenveux.ApplicationController;
import com.tenveux.theglenn.tenveux.DepthPageTransformer;
import com.tenveux.theglenn.tenveux.R;
import com.tenveux.theglenn.tenveux.UserPreferences;
import com.tenveux.theglenn.tenveux.models.Proposition;
import com.tenveux.theglenn.tenveux.models.User;
import com.tenveux.theglenn.tenveux.widget.PropositionPagerAdapter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PropositionsActivity extends ActionBarActivity {


    @InjectView(R.id.pager)
    ViewPager mViewPager;
    PropositionPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_propositions);
        ButterKnife.inject(this);

        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        User u = UserPreferences.getSessionUser();

        mViewPager.setPageTransformer(true, new DepthPageTransformer());

        ApplicationController.userApi().getPendingProposition(u.getId(), new Callback<List<Proposition>>() {
            @Override
            public void success(List<Proposition> propositions, Response response) {

                Log.d("succes", new Gson().toJson(propositions).toString());
                mPagerAdapter = new PropositionPagerAdapter(getSupportFragmentManager(), propositions);
                mViewPager.setAdapter(mPagerAdapter);

                //setContentView(mViewPager);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.propositions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void removeProposition(Proposition proposition) {
        mPagerAdapter.remove(proposition);
    }
}
