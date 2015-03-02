package com.tenveux.theglenn.tenveux.widget;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.google.gson.reflect.TypeToken;
import com.tenveux.theglenn.tenveux.models.Answer;
import com.tenveux.theglenn.tenveux.models.Proposition;
import com.tenveux.theglenn.tenveux.fragment.PropositionFragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by theGlenn on 01/11/2014.
 */
public class PropositionPagerAdapter extends FragmentStatePagerAdapter {

    List<Object> propositionAndAnswers;


    public PropositionPagerAdapter(FragmentManager supportFragmentManager, List<Object> propositionAndAnswers) {
        super(supportFragmentManager);

        //Type listType = new TypeToken<List<Proposition>>() {}.getType();
        //mPropositons = new Gson().fromJson(propositions, listType);

        this.propositionAndAnswers = propositionAndAnswers;

        /*for (Proposition p : propositions) {
            PropositionAnswerWrapper wrapper = new PropositionAnswerWrapper();
            wrapper.proposition = p;
            propositionAndAnswers.add(wrapper);

        }

        for (Answer a : answers) {
            PropositionAnswerWrapper wrapper = new PropositionAnswerWrapper();
            wrapper.answer = a;
            propositionAndAnswers.add(wrapper);

        }*/
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


    public void remove(Object wrapper) {
        int position = propositionAndAnswers.indexOf(wrapper);
        propositionAndAnswers.remove(position);
        this.notifyDataSetChanged();
    }

    public class PropositionAnswerWrapper {
        public Proposition proposition;
        public Answer answer;

    }
}
