package com.tenveux.theglenn.tenveux.activities.menu.profile;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.gson.JsonElement;
import com.tenveux.theglenn.tenveux.ApplicationController;
import com.tenveux.theglenn.tenveux.R;
import com.tenveux.theglenn.tenveux.models.Proposition;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        ButterKnife.inject(this);

        RelativeLayout container = (RelativeLayout) findViewById(R.id.pager_container);
        container.setClipChildren(false);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            container.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        viewPager.setOffscreenPageLimit(5);
        viewPager.setPageMargin(15);
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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
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
        }

        return super.onOptionsItemSelected(item);
    }
}
