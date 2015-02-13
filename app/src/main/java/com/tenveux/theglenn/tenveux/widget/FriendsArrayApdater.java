package com.tenveux.theglenn.tenveux.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tenveux.theglenn.tenveux.R;
import com.tenveux.theglenn.tenveux.models.User;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by theGlenn on 18/10/2014.
 */
public class FriendsArrayApdater extends ArrayAdapter {

    List<User> mUsers;
    Context mContext;
    int layoutResourceId;
    LayoutInflater inflater;

    public FriendsArrayApdater(Context context, int resource, List<User> users) {
        super(context, resource);

        mUsers = users;
        mContext = context;
        layoutResourceId = resource;
        inflater = LayoutInflater.from(getContext());

    }

    @Override
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public User getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        User user = getItem(position);


        if (user != null) {

            String name = user.getName();

            //TODO : uncoment
            /*Picasso.with(getContext())
                    .load(Utils.getFacebookPixURL(user))
                    .resize(50, 50)
                    .centerCrop()
                            //.transform(new CircleTransformation())
                    .into(holder.avatar);*/

            holder.t1.setText(name);
        }

        return view;
    }


    static class ViewHolder {
        @InjectView(R.id.avatar)
        CircleImageView avatar;

        @InjectView(android.R.id.text1)
        TextView t1;


        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
