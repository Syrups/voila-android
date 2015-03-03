package com.tenveux.theglenn.tenveux.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tenveux.theglenn.tenveux.ApplicationController;
import com.tenveux.theglenn.tenveux.R;
import com.tenveux.theglenn.tenveux.UserPreferences;
import com.tenveux.theglenn.tenveux.Utils;
import com.tenveux.theglenn.tenveux.activities.PropositionsActivity;
import com.tenveux.theglenn.tenveux.models.Answer;
import com.tenveux.theglenn.tenveux.models.Proposition;
import com.tenveux.theglenn.tenveux.models.User;
import com.tenveux.theglenn.tenveux.widget.PropositionPagerAdapter;
import com.tenveux.theglenn.tenveux.widget.VoilaSeekBar;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PropositionFragment extends Fragment implements FriendsFragment.FrienSelectedListner {

    final static float END_SCALE = 1.2f;

    @Optional
    @InjectView(R.id.taken_layout)
    ViewGroup mTakenLayout;

    @InjectView(R.id.text_status_fading)
    TextView statusText;

    @InjectView(R.id.layout_status_fading)
    View fadingView;

    @InjectView(android.R.id.background)
    ImageView mImage;


    @Optional
    @InjectView(R.id.took_logo)
    ImageView mTookLogo;


    @Optional
    @InjectView(android.R.id.text1)
    TextView text;

    @Optional
    @InjectView(R.id.sender_name)
    TextView fromName;

    @Optional
    @InjectView(R.id.user_name)
    TextView toName;

    @Optional
    @InjectView(R.id.button_resend)
    Button buttonRe;

    @InjectView(R.id.avatar)
    CircleImageView mAvatar;

    @Optional
    @InjectView(R.id.avatar_receiver)
    CircleImageView mAvatarReceiver;

    @Optional
    @InjectView(R.id.seekBar)
    VoilaSeekBar mSwitchBar;

    @Optional
    @InjectView(R.id.button_ok)
    Button buttonOk;


    Proposition mProposition;
    Answer mAnswer;
    boolean mPropMode;


    /**
     * Fragment representing a proposition
     *
     * @return A new instance of fragment PropositionFragment.
     */

    public static PropositionFragment newInstance(Object wrapper) {
        PropositionFragment fragment = new PropositionFragment();

        if (wrapper instanceof Answer) {
            fragment.mAnswer = (Answer) wrapper;
        } else {
            fragment.mProposition = (Proposition) wrapper;
        }

        return fragment;
    }


    public PropositionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mPropMode = this.mProposition != null;
        int layout = mPropMode ? R.layout.fragment_proposition : R.layout.fragment_answer;
        View v = getActivity().getLayoutInflater().inflate(layout, container, false);

        ButterKnife.inject(this, v);

        final String imageURl = Utils.getPropositionMediaUrl(mPropMode ? mProposition : mAnswer.getProposition());
        Picasso.with(getActivity()).load(imageURl).into(mImage, new Callback() {
            @Override
            public void onSuccess() {
                Log.d("Picasso", "done -> " + imageURl);
            }

            @Override
            public void onError() {
                Log.d("Picasso", "error ->" + imageURl);
            }
        });


        if (mPropMode) {
            this.initProposition();
        } else {
            this.initAnswer(v);
        }

        return v;
    }

    void initProposition() {
        //Load : Background


        //TODO : Load Avatar
        /*Picasso.with(getActivity())
                .load()
                .resize(50, 50)
                .centerCrop()
                .into(avatar);*/

        // Sender informations
        String name = mProposition.getSender().getName();
        String tenveux = getResources().getString(R.string.ten_veux);
        Spanned phrase = Html.fromHtml("<b>" + name + "</b> : " + "<i>" + tenveux + "</i>");

        text.setText(phrase);
        this.setupSwitch();
    }

    void initAnswer(View v) {

        User from = this.mAnswer.getFrom();
        User to = this.mAnswer.getTo();
         /*Picasso.with(getActivity())
                .load(to.getAvatar())
                .resize(50, 50)
                .centerCrop()
                .into(mAvatar);
         Picasso.with(getActivity())
                .load(from.getAvatar())
                .resize(50, 50)
                .centerCrop()
                .into(mAvatar);*/

        fromName.setText(from.getName());
        toName.setText(to.getName());

        int colorRes = this.mAnswer.isYes() ? R.color.cyan : R.color.red;
        int color = getResources().getColor(colorRes);

        statusText.setTextColor(color);

        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                fadingView.setVisibility(View.VISIBLE);
                statusText.setVisibility(View.VISIBLE);
                buttonOk.setVisibility(View.VISIBLE);
            }
        }, 500);
    }


    protected void setupSwitch() {

        final float initX = ViewHelper.getScaleX(statusText) * 2;
        final float initY = ViewHelper.getScaleY(statusText) * 2;

        ViewHelper.setScaleX(statusText, initX);
        ViewHelper.setScaleY(statusText, initY);

        mSwitchBar.setProgress(100);
        mSwitchBar.setOnVoilaSeekBarChangeListener(new VoilaSeekBar.OnVoilaSeekBarChangeListener() {

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onOptionSelected(final boolean isPositive) {

                boolean newApi = Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB;
                //final float currentX = newApi ? statusText.getScaleX() : ViewHelper.getScaleX(statusText);
                //final float currentY = newApi ? statusText.getScaleY() : ViewHelper.getScaleY(statusText);

                if (isPositive)
                    PropositionFragment.this.takeProposition();
                else
                    PropositionFragment.this.dismissProposition();

                /*ScaleAnimation sAnim = new ScaleAnimation(
                        currentX, END_SCALE,
                        currentY, END_SCALE,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);*/

                //sAnim.setDuration(100);
                //sAnim.setFillAfter(true);
                //statusText.startAnimation(sAnim);

                /*sAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });*/
            }

            @Override
            public void onNeutralSelected() {
                fadingView.setVisibility(View.GONE);
            }

            @Override
            public void onStartTrackingTouch() {
                fadingView.setVisibility(View.VISIBLE);
                statusText.setVisibility(View.VISIBLE);

            }

            float augFactor = 0.5f;

            @Override
            public void onValueChanged(SeekBar seekbar, int value) {

                int progress = seekbar.getProgress();

                float val = ((float) progress / 100.f) - 1.f;
                float absValue = Math.abs(val);

                float aug = (absValue < .75f ? 0.1f : augFactor + 0.1f);
                float multiplier = absValue + (absValue < .75f ? 0.5f : aug);

                //Log.d("Progress", "->" + absValue);
                //Log.d("value", progress + " = p");

                int colorRes = val < 0 ? R.color.red : R.color.cyan;
                int color = getResources().getColor(colorRes);

                statusText.setTextColor(color);

                //if (absValue < .75f) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                    statusText.setAlpha(absValue);
                    statusText.setScaleX(initX / multiplier);
                    statusText.setScaleY(initY / multiplier);

                } else {

                    ViewHelper.setAlpha(statusText, absValue);
                    ViewHelper.setScaleX(statusText, initX / multiplier);
                    ViewHelper.setScaleY(statusText, initY / multiplier);

                }
                Log.d("ViewHelper", ViewHelper.getScaleX(statusText) + " A " + ViewHelper.getScaleY(statusText));
                /*} /*else {
                    Log.d("ViewHelper", ViewHelper.getScaleX(statusText) + " A " + ViewHelper.getScaleY(statusText));
                }*/
            }
        });
    }

    //TODO : include view to show in layout !
    void showDialog() {
        mTakenLayout.setVisibility(View.VISIBLE);
    }

    void showDismissDialog() {

    }


    void takeProposition() {

        mTookLogo.setVisibility(View.VISIBLE);
        mSwitchBar.setVisibility(View.INVISIBLE);

        /*ViewHelper.setScaleX(mTookLogo, 5f);
        ViewHelper.setScaleY(mTookLogo, 5f);

        ScaleAnimation sAnim = new ScaleAnimation(
                ViewHelper.getScaleX(mTookLogo), .3f,
                ViewHelper.getScaleY(mTookLogo), .3f,
                Animation.RELATIVE_TO_SELF, .5f,
                Animation.RELATIVE_TO_SELF, .5f);

        sAnim.setDuration(500);
        sAnim.setFillAfter(true)
        mTookLogo.startAnimation(sAnim);;*/

        ApplicationController.propositionApi().takePropostion(mProposition.getId(), new retrofit.Callback<JsonElement>() {
            @Override
            public void success(JsonElement jsonElement, Response response) {
                showDialog();
                //((PropositionsActivity) getActivity()).removeProposition(proposition);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    void dismissProposition() {
        ApplicationController.propositionApi().dismissPropostion(mProposition.getId(), new retrofit.Callback<JsonElement>() {
            @Override
            public void success(JsonElement jsonElement, Response response) {
                showDismissDialog();
                //((PropositionsActivity) getActivity()).removeProposition(proposition);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    @Optional
    @OnClick(R.id.button_resend)
    void bounceProposition() {

        final Proposition bProposition = new Proposition();
        bProposition.setOriginalProposition(this.mProposition.getId());

        User session = UserPreferences.getSessionUser();
        ApplicationController.userApi().friends(session.getId(), new retrofit.Callback<List<User>>() {
            @Override
            public void success(List<User> users, Response response) {
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                Fragment prev = getChildFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }

                ft.addToBackStack(null);

                // Create and show the dialog.
                FriendsFragment newFragment = FriendsFragment.newInstance(users, bProposition, true);
                newFragment.show(ft, "dialog");
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @OnClick(R.id.button_ok)
    void nextProposition() {
        if (!mPropMode) {
            ((PropositionsActivity) getActivity()).removeProposition(mProposition);
        } else {
            ApplicationController.propositionApi().acknowledge(mAnswer.getId(), new retrofit.Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {

                }

                @Override
                public void failure(RetrofitError error) {
                    error.printStackTrace();
                }
            });

            ((PropositionsActivity) getActivity()).removeProposition(mAnswer);
        }
    }

    @Override
    public void onPropositionSent(JsonElement JsonElement) {

    }
}
