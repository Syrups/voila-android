package com.tenveux.theglenn.tenveux.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.facebook.model.GraphUser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tenveux.theglenn.tenveux.ApplicationController;
import com.tenveux.theglenn.tenveux.R;
import com.tenveux.theglenn.tenveux.UserPreferences;
import com.tenveux.theglenn.tenveux.apimodel.CreateUserResponse;
import com.tenveux.theglenn.tenveux.apimodel.Proposition;
import com.tenveux.theglenn.tenveux.widget.FriendsArrayApdater;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link com.tenveux.theglenn.tenveux.MainActivity}
 * interface.
 */
public class FriendsFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ADAPT = "adapter";

    // TODO: Rename and change types of parameters
    private FriendsArrayApdater adapter;
    public List<GraphUser> users;
    TypedFile image;

    private FrienSelectedListner mListener;
    private ListView mUsersList;

    // TODO: Rename and change types of parameters
    public static FriendsFragment newInstance(List<GraphUser> users, TypedFile image) {
        FriendsFragment fragment = new FriendsFragment();
        fragment.users = users;
        fragment.image = image;
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
        try {
            mListener = (FrienSelectedListner) activity;
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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment_friends, null);
        adapter = new FriendsArrayApdater(getActivity(), R.layout.list_item_friends, this.users);

        mUsersList = ButterKnife.findById(v, android.R.id.list);
        mUsersList.setAdapter(adapter);
        mUsersList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mUsersList.setItemsCanFocus(false);
        mUsersList.setDivider(null);


        Button sendProposition = ButterKnife.findById(v, R.id.send_proposition_button);
        sendProposition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                    GraphUser user = users.get(position);
                    userIds.add(user.getId());
                }

                ApplicationController.offApi().sendImage(image, new Callback<JsonElement>() {
                    @Override
                    public void success(JsonElement jsonElement, Response response) {

                        CreateUserResponse sessionUser = UserPreferences.getSessionUser();

                        String source = jsonElement.toString();
                        //Log.d("taken", source);

                        JsonObject json = jsonElement.getAsJsonObject();
                        String image = json.get("filename").getAsString();

                        Proposition p = new Proposition();
                        p.setReceivers(userIds);
                        p.setSender(sessionUser.getId());
                        p.setSenderName(sessionUser.getName());
                        p.setImage(image);

                        ApplicationController.api().sendPropostion(p, new Callback<JsonElement>() {
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

                                error.printStackTrace();
                            }
                        });
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();
                    }
                });
            }
        });

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
