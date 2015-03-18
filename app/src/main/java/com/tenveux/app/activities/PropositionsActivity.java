package com.tenveux.app.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.tenveux.app.ApplicationController;
import com.tenveux.app.DepthPageTransformer;
import com.tenveux.app.R;
import com.tenveux.app.UserPreferences;
import com.tenveux.app.models.Answer;
import com.tenveux.app.models.Proposition;
import com.tenveux.app.models.User;
import com.tenveux.app.models.data.PropositionDeserializer;
import com.tenveux.app.widget.PropositionPagerAdapter;

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

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(Proposition.class, new PropositionDeserializer());
                    Gson gson = gsonBuilder.create();

                    JsonArray answers = jsonObject.get("answers").getAsJsonArray();
                    JsonArray propositions = jsonObject.get("propositions").getAsJsonArray();

                    // Type listType = new TypeToken<List<Proposition>>() {}.getType();
                    //mPropositons = new Gson().fromJson(propositions, listType);
                    mAnswersAndPropostion = new ArrayList<>();

                    for (JsonElement p : propositions) {

                        try {
                            Proposition prop = gson.fromJson(p.toString(), Proposition.class);
                            mAnswersAndPropostion.add(prop);
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    }


                    //TODO : answers
                    for (JsonElement a : answers) {


                        Answer ans = gson.fromJson(a.toString(), Answer.class);
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
        } else if (id == android.R.id.home) {
            //NavUtils.navigateUpFromSameTask(this);
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void removeProposition(final Proposition wrapper) {
        if (mViewPager.getCurrentItem() + 1 < mAnswersAndPropostion.size()) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
        }

        mViewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPagerAdapter.remove(wrapper);
            }
        }, 500);
    }

    public void removeAnswer(final Answer wrapper) {
        if (mViewPager.getCurrentItem() + 1 < mAnswersAndPropostion.size()) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
        }

        mViewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPagerAdapter.remove(wrapper);
            }
        }, 500);
    }

}
