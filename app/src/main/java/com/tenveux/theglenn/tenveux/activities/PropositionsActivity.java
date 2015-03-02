package com.tenveux.theglenn.tenveux.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tenveux.theglenn.tenveux.ApplicationController;
import com.tenveux.theglenn.tenveux.DepthPageTransformer;
import com.tenveux.theglenn.tenveux.R;
import com.tenveux.theglenn.tenveux.UserPreferences;
import com.tenveux.theglenn.tenveux.models.Answer;
import com.tenveux.theglenn.tenveux.models.Proposition;
import com.tenveux.theglenn.tenveux.models.User;
import com.tenveux.theglenn.tenveux.widget.PropositionPagerAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PropositionsActivity extends ActionBarActivity {

    @InjectView(R.id.progressBar)
    ProgressBar mProgressBar;

    @InjectView(R.id.pager)
    ViewPager mViewPager;
    PropositionPagerAdapter mPagerAdapter;

    List<Object> mAnswersAndPropostion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_propositions);
        ButterKnife.inject(this);

        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        User u = UserPreferences.getSessionUser();

        mViewPager.setPageTransformer(true, new DepthPageTransformer());

        if (u != null)
            //TODO switch with PENDINGS
            ApplicationController.userApi().pendingall(u.getId(), new Callback<JsonObject>() {
                @Override
                public void success(JsonObject jsonObject, Response response) {

                    JsonArray answers = jsonObject.get("answers").getAsJsonArray();
                    JsonArray propositions = jsonObject.get("propositions").getAsJsonArray();

                    //TODO directly pass jsonObject
                    mAnswersAndPropostion = new ArrayList<>();

                    for (JsonElement p : propositions) {
                        Proposition prop = new Gson().fromJson(p.toString(), Proposition.class);
                        mAnswersAndPropostion.add(prop);
                    }

                    //TODO : answers
                    for (JsonElement a : answers) {
                        Answer ans = new Gson().fromJson(a.toString(), Answer.class);
                        mAnswersAndPropostion.add(ans);
                    }

                    mPagerAdapter = new PropositionPagerAdapter(getSupportFragmentManager(), mAnswersAndPropostion);
                    mViewPager.setAdapter(mPagerAdapter);

                    mProgressBar.setVisibility(View.GONE);
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


    public void removeProposition(Object wrapper) {
        mPagerAdapter.remove(wrapper);
    }

}
