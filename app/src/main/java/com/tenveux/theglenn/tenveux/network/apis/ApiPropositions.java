package com.tenveux.theglenn.tenveux.network.apis;

import com.google.gson.JsonElement;
import com.tenveux.theglenn.tenveux.models.Proposition;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by theGlenn on 04/02/15.
 */
public interface ApiPropositions {

    public static final String API = "/propositions";

    //Proposition
    public static final String PROPOSITION_SEND = "/propositions";
    public static final String PROPOSITION_TAKE = API + "/{id}/take";
    public static final String PROPOSITION_DIS = API + "/{id}/dismiss";

    //Proposition functions

    @POST(PROPOSITION_SEND)
    public void sendPropostion(@Body Proposition proposition, Callback<JsonElement> callback);

    @GET(PROPOSITION_TAKE)
    public void takePropostion(@Path("id") String id, Callback<JsonElement> callback);

    @GET(PROPOSITION_DIS)
    public void dismissPropostion(@Path("id") String id, Callback<JsonElement> callback);
}
