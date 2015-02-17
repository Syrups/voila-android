package com.tenveux.theglenn.tenveux.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.JsonElement;

import de.hdodenhof.circleimageview.CircleImageView;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tenveux.theglenn.tenveux.Utils;
import com.tenveux.theglenn.tenveux.network.ApiController;
import com.tenveux.theglenn.tenveux.ApplicationController;
import com.tenveux.theglenn.tenveux.R;
import com.tenveux.theglenn.tenveux.activities.PropositionsActivity;
import com.tenveux.theglenn.tenveux.models.Proposition;
import com.tenveux.theglenn.tenveux.widget.VoilaSeekBar;

import java.net.URI;
import java.net.URISyntaxException;

import retrofit.RetrofitError;
import retrofit.client.Response;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class PropositionFragment extends Fragment {

    final static float END_SCALE = 1.2f;

    @InjectView(android.R.id.text1)
    TextView text;

    @InjectView(R.id.text_status_fading)
    TextView statusText;

    @InjectView(android.R.id.background)
    ImageView mImage;

    @InjectView(R.id.layout_status_fading)
    View fadingView;


    @InjectView(R.id.avatar)
    CircleImageView mAvatar;

    Proposition proposition;


    /**
     * Fragment representing a proposition
     *
     * @return A new instance of fragment PropositionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PropositionFragment newInstance(Proposition proposition) {
        PropositionFragment fragment = new PropositionFragment();
        fragment.proposition = proposition;
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

        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_proposition, container, false);

        ButterKnife.inject(this, v);

        String imageURl = null;
        try {
            imageURl = Utils.getImage2(proposition.getImage());
            //title.setText(imageURl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        //Load Background
        Picasso.with(getActivity()).load(imageURl).into(mImage, new Callback() {
            @Override
            public void onSuccess() {
                Log.d("Picasso", "done");
            }

            @Override
            public void onError() {
                Log.d("Picasso", "error");
            }
        });

        //TODO : Load Avatar

        /*Picasso.with(getActivity())
                .load()
                .resize(50, 50)
                .centerCrop()
                .into(avatar);*/

        // Sender informations
        String name = proposition.getSender().getName();
        String tenveux = getResources().getString(R.string.ten_veux);
        Spanned phrase = Html.fromHtml("<b>" + name + "</b> : " + "<i>" + tenveux + "</i>");

        text.setText(phrase);

        this.setupSwitch(v);


        return v;
    }


    protected void setupSwitch(View view) {


        final VoilaSeekBar switchBar = (VoilaSeekBar) view.findViewById(R.id.seekBar);

        final float initX = ViewHelper.getScaleX(statusText) * 2;
        final float initY = ViewHelper.getScaleY(statusText) * 2;


        ViewHelper.setScaleX(statusText, initX);
        ViewHelper.setScaleY(statusText, initY);

        switchBar.setOnVoilaSeekBarChangeListener(new VoilaSeekBar.OnVoilaSeekBarChangeListener() {

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onOptionSelected(final boolean isPositive) {

                boolean newApi = Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB;
                final float currentX = newApi ? statusText.getScaleX() : ViewHelper.getScaleX(statusText);
                final float currentY = newApi ? statusText.getScaleY() : ViewHelper.getScaleY(statusText);

                ScaleAnimation sAnim = new ScaleAnimation(
                        currentX, END_SCALE,
                        currentY, END_SCALE,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);

                sAnim.setDuration(100);
                sAnim.setFillAfter(true);
                statusText.startAnimation(sAnim);

                sAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (isPositive)
                            PropositionFragment.this.takeProposition();

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            @Override
            public void onNeutralSelected() {
                fadingView.setVisibility(View.GONE);
            }

            @Override
            public void onStartTrackingTouch() {
                fadingView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onValueChanged(SeekBar seekbar, int value) {

                int progress = seekbar.getProgress();

                float val = ((float) progress / 100.f) - 1.f;
                float absValue = Math.abs(val);
                float multiplier = 0.5f + absValue;

                //Log.d("Progress", "->" + absValue);
                //Log.d("value", progress + " = p");

                int colorRes = val < 0 ? R.color.red : R.color.cyan;
                int color = getResources().getColor(colorRes);

                statusText.setTextColor(color);

                if (absValue < .75f) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                        statusText.setAlpha(absValue);
                        statusText.setScaleX(initX / multiplier);
                        statusText.setScaleY(initY / multiplier);

                    } else {

                        ViewHelper.setAlpha(statusText, absValue);
                        ViewHelper.setScaleX(statusText, initX / multiplier);
                        ViewHelper.setScaleY(statusText, initY / multiplier);

                    }
                    //Log.d("ViewHelper", ViewHelper.getScaleX(statusText) + " A " + ViewHelper.getScaleY(statusText));
                } else {
                    Log.d("ViewHelper", ViewHelper.getScaleX(statusText) + " A " + ViewHelper.getScaleY(statusText));

                }
            }
        });
    }

    void showDialog() {
        // Create the fragment and show it as a dialog.
        DialogFragment newFragment = DialogProposition.newInstance();
        newFragment.show(getFragmentManager(), "dialog");
    }


    void takeProposition() {

        ApplicationController.propositionApi().takePropostion(proposition.getId(), new retrofit.Callback<JsonElement>() {
            @Override
            public void success(JsonElement jsonElement, Response response) {
                Log.d("take", response.getBody().toString());
                showDialog();
                ((PropositionsActivity) getActivity()).removeProposition(proposition);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("TakeError", error.getMessage());
                error.printStackTrace();
            }
        });
    }

    void dismissProposition() {
        ApplicationController.propositionApi().dismissPropostion(proposition.getId(), new retrofit.Callback<JsonElement>() {
            @Override
            public void success(JsonElement jsonElement, Response response) {
                Log.d("dismiss", response.getBody().toString());
                ((PropositionsActivity) getActivity()).removeProposition(proposition);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("DisError", error.getMessage());
                error.printStackTrace();
            }
        });
    }

    void bounceProposition() {
        Proposition p = new Proposition();
        p.setOriginalProposition(this.proposition.getId());

        ApplicationController.propositionApi().sendPropostion(p, new retrofit.Callback<JsonElement>() {
            @Override
            public void success(JsonElement jsonElement, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
       /* final Session session = Session.getActiveSession();

        if (session != null) {
            Log.d("sessiion", session.getState().isOpened() + "");
            if (session.getState().isOpened()) {
                //TODO : SHOW FRIEND LIST !!!!!
                Log.d("users", "launch");

                Request.newMyFriendsRequest(session, new Request.GraphUserListCallback() {
                    @Override
                    public void onCompleted(List<GraphUser> users, com.facebook.Response response) {

                        Log.d("users", "done");
                        for (GraphUser u : users) {
                            Log.d("users", u.getName());
                        }
                        // DialogFragment.show() will take care of adding the fragment
                        // in a transaction.  We also want to remove any currently showing
                        // dialog, so make our own transaction and take care of that here.
                        FragmentTransaction ft = getChildFragmentManager().beginTransaction();

                        Dial prev = getFragmentManager().findFragmentByTag("dialog");
                        if (prev != null) {
                            ft.remove(prev);
                        }

                        ft.addToBackStack(null);

                        // Create and show the dialog.
                        FriendsFragment newFragment = FriendsFragment.newInstance(users, imagetoSend);
                        newFragment.show(ft, "dialog");
                    }
                }).executeAsync();
            }
        }*/
    }

    public static class DialogProposition extends DialogFragment {

        @InjectView(R.id.button_ok)
        Button buttonOk;

        @InjectView(R.id.button_resend)
        Button buttonRe;

        static DialogProposition newInstance() {
            return new DialogProposition();
        }

       /* @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_dialog_jaipris, container, false);
            //View tv = v.findViewById(R.id.text);
            //((TextView)tv).setText("This is an instance of MyDialogFragment");
            return v;
        }*/

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_jaipris, null);

            ButterKnife.inject(v);

            // creating the fullscreen dialog
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(v);
            Drawable d = new ColorDrawable(Color.BLACK);
            d.setAlpha(130);
            dialog.getWindow().setBackgroundDrawable(d);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


            return dialog;
        }

        @OnClick(R.id.button_ok)
        void setButtonOk() {
            this.dismiss();
        }

        @OnClick(R.id.button_resend)
        void setButtonRe() {
            this.dismiss();
            //reSend();
        }

        @Override
        public void onActivityCreated(Bundle arg0) {
            super.onActivityCreated(arg0);
            getDialog().getWindow()
                    .getAttributes().windowAnimations = R.style.DialogAnimation;
        }
    }


}
