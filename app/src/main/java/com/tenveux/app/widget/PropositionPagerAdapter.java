package com.tenveux.app.widget;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.tenveux.app.models.Answer;
import com.tenveux.app.models.Proposition;
import com.tenveux.app.fragment.PropositionFragment;

import java.util.List;

/**
 * Created by theGlenn on 01/11/2014.
 */
public class PropositionPagerAdapter extends FragmentStatePagerAdapter {

    List<Object> propositionAndAnswers;


    public PropositionPagerAdapter(FragmentManager supportFragmentManager, List<Object> propositionAndAnswers) {
        super(supportFragmentManager);
        
        this.propositionAndAnswers = propositionAndAnswers;
    }

    @Override
    public Fragment getItem(int position) {
        Object wrapper = propositionAndAnswers.get(position);
        return PropositionFragment.newInstance(wrapper);

    }

    @Override
    public int getCount() {
        return this.propositionAndAnswers.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }


    public void remove(Proposition p) {
        int position = propositionAndAnswers.indexOf(p);
        propositionAndAnswers.remove(position);
        this.notifyDataSetChanged();
    }

    public void remove(Answer a) {
        int position = propositionAndAnswers.indexOf(a);
        propositionAndAnswers.remove(position);
        this.notifyDataSetChanged();
    }


    private int indexOf(Object of) {
        int i = 0;
        if (of != null)
            for (Object o : propositionAndAnswers) {
                if (of instanceof Proposition) {
                    if (((Proposition) o).getId().equals(((Proposition) o).getId())) {
                        return i;
                    }
                } else if (of instanceof Answer) {
                    if (((Answer) o).getId().equals(((Answer) o).getId())) {
                        return i;
                    }
                }
                i++;
            }
        return -1;
    }
}
