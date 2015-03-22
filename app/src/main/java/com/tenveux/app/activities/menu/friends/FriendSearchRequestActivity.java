package com.tenveux.app.activities.menu.friends;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;
import com.tenveux.app.ApplicationController;
import com.tenveux.app.R;
import com.tenveux.app.UserPreferences;
import com.tenveux.app.models.User;
import com.tenveux.app.widget.FriendsArrayApdater;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FriendSearchRequestActivity extends ActionBarActivity {

    boolean requestMode = true;

    private FriendsArrayApdater mUsersAdapter;
    private List<User> mUsers;


    @InjectView(android.R.id.list)
    ListView mUsersList;

    @InjectView(R.id.search_edit_text)
    EditText mSearchEditext;

    @InjectView(R.id.profile_background)
    ImageView mProfileBG;

    User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        ButterKnife.inject(this);

        mUser = UserPreferences.getSessionUser();

        Picasso.with(this)
                .load(mUser.getAvatar())
                .resize(100, 100)
                .centerCrop()
                .placeholder(R.drawable.ic_user_placeholder)
                .into(mProfileBG);


        Intent i = getIntent();
        if (i != null) {
            requestMode = i.getBooleanExtra("mode", true);
        }

        mUsers = new ArrayList<>();
        mUsersAdapter = new FriendsArrayApdater(this,
                R.layout.list_item_friends,
                mUsers,
                new FriendsArrayApdater.FriendActionListener() {
                    @Override
                    public void onYesButtonClicked(User user, int index) {

                        addFriend(user, index);

                    }

                    @Override
                    public void onNoButtonClicked(User user, int index) {

                    }
                });
        mUsersAdapter.setSendMode(requestMode ? FriendsArrayApdater.REQUEST_MODE : FriendsArrayApdater.SEARCH_MODE);


        mUsersList.setAdapter(mUsersAdapter);

        if (!requestMode) {
            mSearchEditext.setVisibility(View.VISIBLE);
            mSearchEditext.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    final StringBuilder sb = new StringBuilder(cs.length());
                    sb.append(cs);

                    ApplicationController.userApi().find(sb.toString(), new Callback<List<User>>() {
                        @Override
                        public void success(List<User> users, Response response) {
                            mUsers = users;
                            mUsersAdapter.setUsers(users);
                            mUsersAdapter.notifyDataSetChanged();
                            // mUsersList.noti
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            error.printStackTrace();
                        }
                    });
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                }

                @Override
                public void afterTextChanged(Editable arg0) {
                }
            });
        } else {
            ApplicationController.userApi().requests(mUser.getId(), new Callback<List<User>>() {
                @Override
                public void success(List<User> users, Response response) {
                    mUsers = users;
                    mUsersAdapter.setUsers(users);
                    mUsersAdapter.notifyDataSetChanged();
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friend_search_request, menu);
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

    @OnClick(R.id.button_close)
    void close() {
        finish();
    }

    void addFriend(User u, final int index) {

        Map<String, String> params = new LinkedHashMap<>();
        params.put("friend_id", u.getId());

        ApplicationController.userApi().addFriend(mUser.getId(), params, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement jsonElement, Response response) {

                String msg = jsonElement.getAsJsonObject().get("message").getAsString();
                Toast.makeText(FriendSearchRequestActivity.this, msg + "", Toast.LENGTH_LONG).show();

                if (requestMode) {
                    mUsersAdapter.removeItem(index);
                    mUsersAdapter.notifyDataSetChanged();
                } else {

                }
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                Toast.makeText(FriendSearchRequestActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
