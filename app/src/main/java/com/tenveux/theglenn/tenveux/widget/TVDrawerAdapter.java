package com.tenveux.theglenn.tenveux.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tenveux.theglenn.tenveux.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by theGlenn on 23/10/2014.
 */
public class TVDrawerAdapter extends ArrayAdapter {

    Context mContext;
    int layoutResourceId;
    LayoutInflater inflater;

    private static final int ICONS[] = new int[]{R.drawable.menu_item_profile, R.drawable.menu_item_reception, R.drawable.menu_item_friends, R.drawable.menu_item_settings};

   //TODO replace with string ressources
    private static final String NAMES[] = new String[]{"profile", "reception", "network", "settings"};

    public TVDrawerAdapter(Context context, int resource) {
        super(context, resource);

        mContext = context;
        layoutResourceId = resource;
        inflater = LayoutInflater.from(getContext());
    }

    @Override
    public int getCount() {
        return ICONS.length;
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

        int icon = ICONS[position];
        String name = NAMES[position];

        holder.icon.setImageResource(icon);
        holder.text.setText(name);


        return view;
    }


    static class ViewHolder {
        @InjectView(android.R.id.icon)
        ImageView icon;
        @InjectView(android.R.id.text1)
        TextView text;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}