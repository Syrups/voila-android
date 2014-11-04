package com.tenveux.theglenn.tenveux.widget;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.tenveux.theglenn.tenveux.apimodel.Proposition;
import com.tenveux.theglenn.tenveux.fragment.PropositionFragment;

import java.util.List;

/**
 * Created by theGlenn on 01/11/2014.
 */
public class PropositionPagerAdapter extends FragmentStatePagerAdapter {

    List<Proposition> propositions;

    public PropositionPagerAdapter(FragmentManager supportFragmentManager, List<Proposition> propositions) {
        super(supportFragmentManager);
        this.propositions = propositions;
    }

    @Override
    public Fragment getItem(int position) {
        return PropositionFragment.newInstance(propositions.get(position));
    }

    @Override
    public int getCount() {
        return this.propositions.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }


    public void remove(Proposition proposition) {
        int position = propositions.indexOf(proposition);
        propositions.remove(position);
        this.notifyDataSetChanged();
    }
}
