package com.tenveux.theglenn.tenveux.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nineoldandroids.view.ViewHelper;
import com.tenveux.theglenn.tenveux.ApplicationController;
import com.tenveux.theglenn.tenveux.R;
import com.tenveux.theglenn.tenveux.UserPreferences;
import com.tenveux.theglenn.tenveux.Utils;
import com.tenveux.theglenn.tenveux.models.Proposition;
import com.tenveux.theglenn.tenveux.models.User;
import com.tenveux.theglenn.tenveux.widget.FriendsArrayApdater;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link com.tenveux.theglenn.tenveux.activities.MainActivity}
 * interface.
 */
public class FriendsFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ADAPT = "adapter";

    // TODO: Rename and change types of parameters

    private FriendsArrayApdater adapter;
    private FrienSelectedListner mListener;

    TypedFile mImageFile;
    List<User> users;
    Proposition proposition = new Proposition();

    boolean bounce;


    @InjectView(android.R.id.list)
    ListView mUsersList;

    @InjectViews({R.id.button_private, R.id.button_public})
    List<Button> buttons;


    // TODO: Rename and change types of parameters
    public static FriendsFragment newInstance(List<User> users, TypedFile image) {
        FriendsFragment fragment = new FriendsFragment();
        fragment.users = users;
        fragment.mImageFile = image;
        return fragment;
    }

    public static FriendsFragment newInstance(List<User> users, Proposition p, boolean bounce) {
        FriendsFragment fragment = new FriendsFragment();
        fragment.users = users;
        fragment.proposition = p;
        fragment.bounce = bounce;
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FriendsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getParentFragment() == null) {
            try {
                mListener = (FrienSelectedListner) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        } else {
            try {
                mListener = (FrienSelectedListner) getParentFragment();
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment_friends, null);
        adapter = new FriendsArrayApdater(getActivity(), R.layout.list_item_friends, this.users);

        ButterKnife.inject(this, v);


        mUsersList.setAdapter(adapter);
        mUsersList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mUsersList.setItemsCanFocus(false);
        mUsersList.setDivider(null);


        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(v);

        //Drawable d = new ColorDrawable(Color.BLACK);
        //d.setAlpha(30);

        Bitmap map = Utils.takeScreenShot(getActivity());
        Bitmap fast = Utils.fastblur(map, 10);
        final Drawable draw = new BitmapDrawable(getResources(), fast);
        dialog.getWindow().setBackgroundDrawable(draw);

        //dialog.getWindow().setBackgroundDrawable(d);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return dialog;
    }

    @OnClick({R.id.button_private, R.id.button_public})
    void setPropositioonScope(Button button) {

        Boolean isPrivate = button.getId() == R.id.button_private;

        buttons.get(0).setSelected(isPrivate);
        buttons.get(1).setSelected(!isPrivate);

        buttons.get(0).setBackgroundResource(isPrivate ? R.drawable.button_selection_bg : R.drawable.button_selection_bg_transparent);
        buttons.get(1).setBackgroundResource(!isPrivate ? R.drawable.button_selection_bg : R.drawable.button_selection_bg_transparent);

        ViewHelper.setAlpha(buttons.get(0), isPrivate ? 1 : .4f);
        ViewHelper.setAlpha(buttons.get(1), isPrivate ? .4f : 1);

        proposition.setIsPrivate(isPrivate);
    }

    @OnClick(R.id.send_proposition_button)
    void serve() {
        final SparseBooleanArray checkedItems = mUsersList.getCheckedItemPositions();

        if (checkedItems == null) {
            return;
        }

        // For each element in the status array
        int checkedItemsCount = checkedItems.size();
        final List<String> userIds = new ArrayList<String>();

        for (int i = 0; i < checkedItemsCount; ++i) {
            // This tells us the item position we are looking at
            final int position = checkedItems.keyAt(i);

            User user = users.get(position);
            userIds.add(user.getId());
        }

        User sessionUser = UserPreferences.getSessionUser();
        proposition.setReceivers(userIds);
        proposition.setSender(sessionUser);

        if (mImageFile != null) {
            ApplicationController.offApi().sendImage(mImageFile, new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {


                    String source = jsonElement.toString();
                    Log.d("taken", source);

                    JsonObject json = jsonElement.getAsJsonObject();
                    String image = json.get("filename").getAsString();

                    proposition.setImage(image);

                    servePropostion();
                }

                @Override
                public void failure(RetrofitError error) {
                    error.printStackTrace();
                }
            });
        } else {
            servePropostion();
        }
    }


    void servePropostion() {
        ApplicationController.propositionApi().sendPropostion(proposition, new Callback<JsonElement>() {
            @Override
            public void success(JsonElement jsonElement, Response response) {

                Log.d("SendProposition", jsonElement.toString());
                switch (response.getStatus()) {
                    case 201:
                        FriendsFragment.this.dismiss();
                        mListener.onPropositionSent(jsonElement);
                        break;
                    case 500:

                        break;
                }
            }

            @Override
            public void failure(RetrofitError error) {

                Toast.makeText(getActivity(), "Erreur de connextion", Toast.LENGTH_LONG);

                error.printStackTrace();
            }
        });
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
    public interface FrienSelectedListner {
        // TODO: Update argument type and name
        public void onPropositionSent(JsonElement JsonElement);

    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }
}
