package com.tenveux.theglenn.tenveux.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tenveux.theglenn.tenveux.R;
import com.tenveux.theglenn.tenveux.Utils;
import com.tenveux.theglenn.tenveux.models.User;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by theGlenn on 18/10/2014.
 */
public class FriendsArrayApdater extends ArrayAdapter {

    public static final int SEND_MODE = 0;
    public static final int DEFAULT_MODE = 1;
    public static final int REQUEST_MODE = 2;
    public static final int SEARCH_MODE = 3;

    private List<User> mUsers;
    private Context mContext;
    private int layoutResourceId;
    private LayoutInflater inflater;

    private int showMode = DEFAULT_MODE;

    private FriendActionListener mFriendActionListener;

    public FriendsArrayApdater(Context context, int resource, List<User> users) {
        super(context, resource);

        mUsers = users;
        mContext = context;
        layoutResourceId = resource;
        inflater = LayoutInflater.from(getContext());

    }

    public FriendsArrayApdater(Context context, int resource, List<User> users, FriendActionListener listener) {
        super(context, resource);

        mUsers = users;
        mContext = context;
        layoutResourceId = resource;
        inflater = LayoutInflater.from(getContext());
        mFriendActionListener = listener;

    }

    public void setSendMode(int showMode) {
        this.showMode = showMode;
    }

    public void setFriendActionListener(FriendActionListener listener) {
        this.mFriendActionListener = listener;
    }

    public void setUsers(List<User> mUsers) {
        this.mUsers = mUsers;
    }

    @Override
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public User getItem(int position) {
        return mUsers.get(position);
    }


    public void removeItem(int position) {
        mUsers.remove(position);
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        final User user = getItem(position);

        if (user != null) {

            String name = user.getName();

            //TODO : uncoment
            Picasso.with(getContext())
                    .load(user.getAvatar())
                    .resize(50, 50)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_placeholder)
                            //.transform(new CircleTransformation())
                    .into(holder.avatar);

            switch (showMode) {
                case DEFAULT_MODE:
                    holder.dots.setVisibility(View.VISIBLE);
                    break;
                case REQUEST_MODE:
                    holder.buttonLayout.setVisibility(View.VISIBLE);
                    break;
                case SEARCH_MODE:
                    holder.buttonLayout.setVisibility(View.VISIBLE);
                    holder.buttonNo.setVisibility(View.GONE);
                    break;
                case SEND_MODE:
                    holder.checkbox.setVisibility(View.VISIBLE);
                    break;
            }


            if (mFriendActionListener != null) {
                View.OnClickListener yesNoListner = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        switch (v.getId()) {
                            case R.id.button_friend_yes:
                                mFriendActionListener.onYesButtonClicked(user, position);
                                break;
                            case R.id.button_friend_no:
                                mFriendActionListener.onNoButtonClicked(user, position);
                                break;
                        }
                    }
                };

                holder.buttonYes.setOnClickListener(yesNoListner);
                holder.buttonNo.setOnClickListener(yesNoListner);
            }

            holder.t1.setText(name);
        }

        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.avatar)
        CircleImageView avatar;

        @InjectView(android.R.id.text1)
        TextView t1;

        @InjectView(android.R.id.icon)
        ImageView dots;

        @InjectView(android.R.id.checkbox)
        InertCheckBox checkbox;

        @InjectView(R.id.request_buttons_layout)
        LinearLayout buttonLayout;

        @InjectView(R.id.button_friend_yes)
        Button buttonYes;

        @InjectView(R.id.button_friend_no)
        Button buttonNo;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    public interface FriendActionListener {
        public void onYesButtonClicked(User user, int index);

        public void onNoButtonClicked(User user, int index);
    }
}
