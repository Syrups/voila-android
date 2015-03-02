package com.tenveux.theglenn.tenveux.activities.menu.friends;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tenveux.theglenn.tenveux.ApplicationController;
import com.tenveux.theglenn.tenveux.R;
import com.tenveux.theglenn.tenveux.UserPreferences;
import com.tenveux.theglenn.tenveux.models.User;
import com.tenveux.theglenn.tenveux.widget.FriendsArrayApdater;

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

        if (getIntent() != null) {
            mRequestCount = getIntent().getIntExtra("numberOfFriendRequest", 0);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        ButterKnife.inject(this);


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

        ApplicationController.userApi().friends(UserPreferences.getSessionUser().getId(), new Callback<List<User>>() {
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
