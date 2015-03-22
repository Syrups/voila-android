package com.tenveux.theglenn.tenveux.activities.menu.profile;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayout;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.tenveux.theglenn.tenveux.ApplicationController;
import com.tenveux.theglenn.tenveux.R;
import com.tenveux.theglenn.tenveux.UserPreferences;
import com.tenveux.theglenn.tenveux.models.Proposition;
import com.tenveux.theglenn.tenveux.models.User;
import com.tenveux.theglenn.tenveux.widget.ViewPagerFlowAdapter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProfileActivity extends ActionBarActivity {

    @InjectView(R.id.viewpager)
    ViewPager viewPager;

    @InjectView(R.id.user_name)
    TextView userName;


    @InjectView(R.id.sent)
    TextView sent;

    @InjectView(R.id.taken)
    TextView taken;


    @InjectView(R.id.acknowleded)
    TextView acknowleded;

    @InjectView(R.id.sent_grid)
    GridLayout mSentGrid;


    User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUser = UserPreferences.getSessionUser();

        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        ButterKnife.inject(this);
        this.loadView();

        RelativeLayout container = (RelativeLayout) findViewById(R.id.pager_container);
        container.setClipChildren(false);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            container.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        //TODO : revoir ce system
        viewPager.setOffscreenPageLimit(5);
        viewPager.setPageMargin(0);
        viewPager.setClipChildren(false);


        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return viewPager.dispatchTouchEvent(event);
            }
        });


        ApplicationController.propositionApi().popular(new Callback<List<Proposition>>() {
            @Override
            public void success(List<Proposition> propositions, Response response) {
                viewPager.setAdapter(new ViewPagerFlowAdapter(ProfileActivity.this, propositions));
                viewPager.invalidate();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });

        ApplicationController.userApi().findByID(mUser.getId(), new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                mUser = user;
                // UserPreferences.savePreference(mUser);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProfileActivity.this.loadView();
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }


    void loadView() {

        final String style = "<font>%s</font> <br/> <small><font color=#767575>%s</font></small>";

        String s = String.format(style, mUser.getSent(), "Sent");
        String t = String.format(style, mUser.getTaken(), "Taken");
        String a = String.format(style, mUser.getTaken(), "Accepted");

        //span.setSpan(new RelativeSizeSpan(0.8f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        userName.setText(mUser.getName());

        sent.setText(Html.fromHtml(s));
        taken.setText(Html.fromHtml(t));
        acknowleded.setText(Html.fromHtml(a));

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            //NavUtils.navigateUpFromSameTask(this);
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
