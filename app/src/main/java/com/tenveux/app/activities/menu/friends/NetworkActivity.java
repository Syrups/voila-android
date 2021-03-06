package com.tenveux.app.activities.menu.friends;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tenveux.app.ApplicationController;
import com.tenveux.app.R;
import com.tenveux.app.UserPreferences;
import com.tenveux.app.models.User;
import com.tenveux.app.widget.FriendsArrayApdater;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NetworkActivity extends ActionBarActivity {

    @InjectView(android.R.id.list)
    ListView mUsersList;

    @InjectView(R.id.search_edit_text)
    EditText mSearchEditext;


    @InjectView(R.id.profile_background)
    ImageView mProfileBG;


    private FriendsArrayApdater mUsersAdapter;
    private List<User> mFriends;

    private TextView mFriendCounter;
    private int mRequestCount;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);

        User mUser = UserPreferences.getSessionUser();

        if (getIntent() != null) {
            mRequestCount = getIntent().getIntExtra("numberOfFriendRequest", 0);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        ButterKnife.inject(this);


        Picasso.with(this)
                .load(mUser.getAvatar())
                .resize(100, 100)
                .centerCrop()
                .placeholder(R.drawable.ic_user_placeholder)
                .into(mProfileBG);

        mFriends = new ArrayList<>();
        mUsersAdapter = new FriendsArrayApdater(this, R.layout.list_item_friends, this.mFriends);
        mUsersList.setAdapter(mUsersAdapter);
        mUsersList.setItemsCanFocus(false);


        if (savedInstanceState == null) {

            /*getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();*/
        }


        if (mFriendCounter != null) {
            //MainActivity.this.invalidateOptionsMenu();
            mFriendCounter.setText(Integer.toString(mRequestCount));
        }


        ApplicationController.userApi().friends(mUser.getId(), new Callback<List<User>>() {
            @Override
            public void success(List<User> users, Response response) {
                mFriends = users;
                mUsersAdapter.setUsers(mFriends);
                mUsersAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_network, menu);

        MenuItem item = menu.findItem(R.id.action_friend_requests);
        RelativeLayout badgeLayout = (RelativeLayout) MenuItemCompat.getActionView(item);
        badgeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetailActivity(v);
            }
        });

        mFriendCounter = (TextView) badgeLayout.findViewById(R.id.counter);

        if (mRequestCount > 0) {
            mFriendCounter.setText(Integer.toString(mRequestCount));
        } else {
            mFriendCounter.setVisibility(View.GONE);
        }


        return true;
    }


   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_friend_requests) {
            showDetailActivity(MenuItemCompat.getActionView(item));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @OnClick(R.id.add_friend_button)
    void showDetailActivity(View view) {
        Intent i = new Intent(this, FriendSearchRequestActivity.class);
        i.putExtra("mode", view.getId() == R.id.action_friend_requests);
        startActivity(i);
    }
}
