package com.tenveux.theglenn.tenveux.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.facebook.model.GraphUser;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;
import com.tenveux.theglenn.tenveux.R;
import com.tenveux.theglenn.tenveux.Utils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by theGlenn on 18/10/2014.
 */
public class FriendsArrayApdater extends ArrayAdapter {

    List<GraphUser> mUsers;
    Context mContext;
    int layoutResourceId;
    LayoutInflater inflater;

    public FriendsArrayApdater(Context context, int resource, List<GraphUser> users) {
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
    public GraphUser getItem(int position) {
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

        GraphUser user = getItem(position);
        String name = user.getName();
        String fName = user.getFirstName();
        String lName = user.getLastName();

        if (user != null) {

            if (name.length() <= 0 || null == fName) {
                String[] arrs = name.split(" ");
                fName = arrs[0];
                lName = arrs[1];
            }


            Picasso.with(getContext())
                    .load(Utils.getFacebookPixURL(user))
                    .resize(50, 50)
                    .centerCrop()
                            //.transform(new CircleTransformation())
                    .into(holder.avatar);

            holder.t1.setText(fName);
            holder.t2.setText(lName);
        }

        return view;
    }


    static class ViewHolder {
        @InjectView(R.id.avatar)
        CircularImageView avatar;
        @InjectView(android.R.id.text1)
        TextView t1;
        @InjectView(android.R.id.text2)
        TextView t2;


        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
