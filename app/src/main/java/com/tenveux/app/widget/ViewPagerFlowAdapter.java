package com.tenveux.app.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tenveux.app.R;
import com.tenveux.app.Utils;
import com.tenveux.app.models.Proposition;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by theGlenn on 02/03/15.
 */
public class ViewPagerFlowAdapter extends PagerAdapter {


    Context context;
    List<Proposition> propositions;
    LayoutInflater inflater;

    public ViewPagerFlowAdapter(Context ctx, List<Proposition> propositions) {
        this.context = ctx;
        this.propositions = propositions;
        this.inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return this.propositions.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        Proposition p = propositions.get(position);
        //R.color.cyan
        String s = String.format("<font color=#05c0a7>%s</font> <font color=#5e5b5a>%s</font>", p.getSent(), "service");
        Spanned text = Html.fromHtml(s);

        View v = inflater.inflate(R.layout.pager_item_profile_services, null, false);

        ImageView image = ButterKnife.findById(v, android.R.id.icon);
        TextView textView = ButterKnife.findById(v, android.R.id.text1);
        textView.setText(text);

        final ProgressBar progress = ButterKnife.findById(v, android.R.id.progress);

        String imageURl = Utils.getPropositionMediaUrl(p);
        Picasso.with(context)
                .load(imageURl)
                        //.centerInside()
                .placeholder(R.drawable.ic_user_placeholder)
                .into(image, new Callback() {
                    @Override
                    public void onSuccess() {
                        progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        progress.setVisibility(View.GONE);
                    }
                });

        container.addView(v, 0);

        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((TextView) object);
    }


    static class FlowHolder {
        @InjectView(android.R.id.icon)
        ImageView image;

        @InjectView(android.R.id.text1)
        TextView textView;

        public FlowHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}