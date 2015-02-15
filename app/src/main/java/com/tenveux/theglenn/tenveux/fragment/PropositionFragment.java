package com.tenveux.theglenn.tenveux.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.JsonElement;

import de.hdodenhof.circleimageview.CircleImageView;

import com.nineoldandroids.animation.ValueAnimator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PropositionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PropositionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PropositionFragment extends Fragment {


    private OnFragmentInteractionListener mListener;

    @InjectView(android.R.id.text1)
    TextView text;

    @InjectView(android.R.id.background)
    ImageView mImage;

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
            imageURl = getImage2(proposition.getImage());
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


    /*
         * Mise en place du switch entre les mode de r�servaion
         */
    protected void setupSwitch(View view) {


        final VoilaSeekBar switchBar = (VoilaSeekBar) view.findViewById(R.id.seekBar);
        switchBar.setProgress(100);
        switchBar.incrementProgressBy(0);
        switchBar.setMax(200);

        final ValueAnimator anim = ValueAnimator.ofInt(0, switchBar.getMax());
        anim.setDuration(1000);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animProgress = (Integer) animation.getAnimatedValue();
                int step = 4;
                int midMax = switchBar.getMax() / 2;

                float moreLess = midMax * 0.5f;
                float morePlus = midMax * 1.5f;

                if (switchBar.getProgress() == midMax) {

                    animProgress = midMax;

                } else if (switchBar.getProgress() <= midMax) {
                    if (switchBar.getProgress() < moreLess) {

                        animProgress = switchBar.getProgress() - step;

                    } else {
                        animProgress = switchBar.getProgress() + step;

                    }
                } else {
                    if (switchBar.getProgress() < morePlus) {

                        animProgress = switchBar.getProgress() - step;

                    } else {
                        animProgress = switchBar.getProgress() + step;

                    }
                }

                switchBar.setProgress(animProgress);

            }
        });

        switchBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar,
                                          int progress, boolean fromUser) {
                progress = progress / 10;
                progress = progress * 10;


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                anim.start();
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            //mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


    public static String getImage2(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = ApiController.BASE_IMG + uri.getRawPath();
        return domain;
    }


    void setmDismissButton() {
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


    @OnClick(R.id.button_take)
    void setmTakeButton() {

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

    void showDialog() {
        // Create the fragment and show it as a dialog.
        DialogFragment newFragment = DialogProposition.newInstance();
        newFragment.show(getFragmentManager(), "dialog");
    }

    /*void reSend(){
        final Session session = Session.getActiveSession();

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
        }
    }*/

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
