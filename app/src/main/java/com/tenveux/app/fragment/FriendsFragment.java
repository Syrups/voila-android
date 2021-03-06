package com.tenveux.app.fragment;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nineoldandroids.view.ViewHelper;
import com.tenveux.app.ApplicationController;
import com.tenveux.app.R;
import com.tenveux.app.UserPreferences;
import com.tenveux.app.Utils;
import com.tenveux.app.models.Proposition;
import com.tenveux.app.models.User;
import com.tenveux.app.models.data.PropositionDeserializer;
import com.tenveux.app.network.MediaAPI;
import com.tenveux.app.widget.FriendsArrayApdater;

import java.io.File;
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
 * Activities containing this fragment MUST implement the {@link com.tenveux.app.activities.MainActivity}
 * interface.
 */
public class FriendsFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ADAPT = "adapter";

    private boolean mBounce;

    private FriendsArrayApdater mUsersAdapter;
    private FrienSelectedListner mListener;

    TypedFile mImageFile;
    List<User> mUsers;
    Proposition mProposition = new Proposition();


    @InjectView(android.R.id.list)
    ListView mUsersList;

    @InjectViews({R.id.button_private, R.id.button_public})
    List<Button> mButtons;


    // TODO: Rename and change types of parameters
    public static FriendsFragment newInstance(File image) {
        FriendsFragment fragment = new FriendsFragment();
        fragment.mImageFile = new TypedFile("image/jpeg", image);
        return fragment;
    }

    public static FriendsFragment newInstance(Proposition p, boolean bounce) {
        FriendsFragment fragment = new FriendsFragment();
        fragment.mProposition = p;
        fragment.mBounce = bounce;
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

        mUsers = UserPreferences.getCachedFriends();

        final User session = UserPreferences.getSessionUser();
        if (session != null) {
            ApplicationController.userApi().friends(session.getId(), new Callback<List<User>>() {
                @Override
                public void success(List<User> users, Response response) {
                    mUsers = users;
                    mUsersAdapter.setUsers(mUsers);
                    mUsersAdapter.notifyDataSetChanged();
                }

                @Override
                public void failure(RetrofitError error) {
                    error.printStackTrace();
                }
            });
        }

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

        mUsersAdapter = new FriendsArrayApdater(getActivity(), R.layout.list_item_friends, mUsers);
        mUsersAdapter.setSendMode(FriendsArrayApdater.SEND_MODE);

        ButterKnife.inject(this, v);


        mUsersList.setAdapter(mUsersAdapter);
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

        mButtons.get(0).setSelected(isPrivate);
        mButtons.get(1).setSelected(!isPrivate);

        mButtons.get(0).setBackgroundResource(isPrivate ? R.drawable.button_selection_bg : R.drawable.button_selection_bg_transparent);
        mButtons.get(1).setBackgroundResource(!isPrivate ? R.drawable.button_selection_bg : R.drawable.button_selection_bg_transparent);

        ViewHelper.setAlpha(mButtons.get(0), isPrivate ? 1 : .4f);
        ViewHelper.setAlpha(mButtons.get(1), isPrivate ? .4f : 1);

        mProposition.setIsPrivate(isPrivate);
    }

    @OnClick(R.id.send_proposition_button)
    void serve() {
        final SparseBooleanArray checkedItems = mUsersList.getCheckedItemPositions();

        if (checkedItems == null) {
            return;
        }

        // For each element in the status array
        int checkedItemsCount = checkedItems.size();
        final List<User> userIds = new ArrayList<User>();

        for (int i = 0; i < checkedItemsCount; ++i) {
            // This tells us the item position we are looking at
            final int position = checkedItems.keyAt(i);

            User user = mUsers.get(position);
            userIds.add(user);
        }

        User sessionUser = UserPreferences.getSessionUser();
        mProposition.setReceivers(userIds);
        mProposition.setSender(sessionUser);

        if (mImageFile != null) {
            this.uploadImage(mImageFile);
        } else {
            if (mBounce) {
                serveProposition(mProposition);
            } else {
                //TODO localized
                Toast.makeText(getActivity(), "an error occured", Toast.LENGTH_LONG).show();
            }
        }
    }


    void uploadImage(TypedFile imageFile) {
        //TypedFile imageToSend = new TypedFile("image/jpeg", mImageFile);

        /*MediaAPI.sendImage(UserPreferences.getSessionUser().getToken(), imageFile.file(), new MediaAPI.ImageCallback() {
            @Override
            public void succes(JsonElement jsonElement) {
                JsonObject json = jsonElement.getAsJsonObject();
                String remoteURL = json.get("filename").getAsString();

                mProposition.setImage(remoteURL);
                mImageFile.file().delete();

                servePropostion(mProposition);

            }

            @Override
            public void failure(com.squareup.okhttp.Response response) {
                //error.printStackTrace();

                cacheProposition(mProposition);
            }
        });*/


        ApplicationController.media().sendImage(imageFile, new Callback<JsonElement>() {
            @Override
            public void success(JsonElement jsonElement, Response response) {
                JsonObject json = jsonElement.getAsJsonObject();
                String remoteURL = json.get("filename").getAsString();

                mProposition.setImage(remoteURL);
                mImageFile.file().delete();

                serveProposition(mProposition);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                cacheProposition(mProposition);
            }
        });
    }

    void cacheProposition(Proposition proposition) {
        Proposition.cache(this.getActivity().getApplicationContext(), proposition, mImageFile);
    }

    void cacheProposition(JsonElement jsonElement) {
        Proposition.cache(this.getActivity().getApplicationContext(), Proposition.fromJson(jsonElement.getAsJsonObject()), mImageFile);
    }


    void serveProposition(final Proposition proposition) {
        ApplicationController.propositionApi().sendPropostion(proposition, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {

                switch (response.getStatus()) {
                    case 201:
                        FriendsFragment.this.dismiss();
                        mListener.onPropositionSent(jsonObject);

                        proposition.setSent(1);
                        cacheProposition(jsonObject);
                        break;
                    case 500:
                        break;
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                cacheProposition(proposition);

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
