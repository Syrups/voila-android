package com.tenveux.theglenn.tenveux.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tenveux.theglenn.tenveux.ApiController;
import com.tenveux.theglenn.tenveux.ApplicationController;
import com.tenveux.theglenn.tenveux.R;
import com.tenveux.theglenn.tenveux.Utils;
import com.tenveux.theglenn.tenveux.activities.PropositionsActivity;
import com.tenveux.theglenn.tenveux.apimodel.Proposition;

import java.net.URI;
import java.net.URISyntaxException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
    CircularImageView mAvatar;

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
                Log.d("done", "done");
            }

            @Override
            public void onError() {
                Log.d("error", "error");
            }
        });

        //TODO : Load Avatar

        /*Picasso.with(getActivity())
                .load()
                .resize(50, 50)
                .centerCrop()
                .into(avatar);*/

        // Sender informations
        String name = proposition.getSenderName();
        String tenveux = getResources().getString(R.string.ten_veux);
        Spanned phrase = Html.fromHtml("<b>" + name + "</b> : " + "<i>" +  tenveux + "</i>") ;

        text.setText(phrase);


        return v;
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

    @OnClick(R.id.button_not_take)
    void setmDismissButton() {
        ApplicationController.api().dismissPropostion(proposition.getId(), new retrofit.Callback<JsonElement>() {
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
        ApplicationController.api().takePropostion(proposition.getId(), new retrofit.Callback<JsonElement>() {
            @Override
            public void success(JsonElement jsonElement, Response response) {
                Log.d("take", response.getBody().toString());
                ((PropositionsActivity) getActivity()).removeProposition(proposition);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("TakeError", error.getMessage());
                error.printStackTrace();
            }
        });
    }

}
