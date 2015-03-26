package com.tenveux.app.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.tenveux.app.R;
import com.tenveux.app.models.Proposition;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by theGlenn on 22/03/15.
 */
public class PropositionAdapter extends ArrayAdapter<Proposition> {

    File imageFileFolder;

    public PropositionAdapter(Context context, ArrayList<Proposition> users) {
        super(context, 0, users);

        imageFileFolder = new File(context.getFilesDir(), "Image");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Proposition proposition = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_proposition, parent, false);
        }


        File imageFileName = new File(imageFileFolder, proposition.getImage());

        if (imageFileName.exists()) {

            Bitmap myBitmap = BitmapFactory.decodeFile(imageFileName.getAbsolutePath());

            ImageView icon = (ImageView) convertView.findViewById(android.R.id.icon);
            icon.setImageBitmap(myBitmap);
        }

        TextView t1 = (TextView) convertView.findViewById(android.R.id.text1);
        //TextView t2 = (TextView) convertView.findViewById(android.R.id.text2);

        t1.setText(proposition.toString());

        return convertView;
    }
}

