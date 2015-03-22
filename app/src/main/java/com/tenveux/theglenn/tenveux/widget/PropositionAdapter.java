package com.tenveux.theglenn.tenveux.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tenveux.theglenn.tenveux.models.Proposition;

import java.util.ArrayList;

/**
 * Created by theGlenn on 22/03/15.
 */
public class PropositionAdapter extends ArrayAdapter<Proposition> {
    public PropositionAdapter(Context context, ArrayList<Proposition> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Proposition proposition = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        TextView t1 = (TextView) convertView.findViewById(android.R.id.text1);
        TextView t2 = (TextView) convertView.findViewById(android.R.id.text2);

        t1.setText(proposition.getSender().getName());

        return convertView;
    }
}

