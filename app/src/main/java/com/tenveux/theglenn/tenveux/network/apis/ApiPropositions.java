package com.tenveux.theglenn.tenveux.network.apis;

import com.google.gson.JsonElement;
import com.tenveux.theglenn.tenveux.models.Proposition;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * Created by theGlenn on 04/02/15.
 */
public interface ApiPropositions {

    public static final String API = "/propositions";

    //Proposition
    //public static final String PROPOSITION_SEND = "/propositions";
    public static final String PROPOSITION_TAKE = API + "/{id}/take";
    public static final String PROPOSITION_DIS = API + "/{id}/dismiss";
    public static final String PROPOSITION_POPULAR = API + "/popular";

    //Proposition functions

    @POST(API)
    public void sendPropostion(@Body Proposition proposition, Callback<JsonElement> callback);

    @PUT(PROPOSITION_TAKE)
    public void takePropostion(@Path("id") String id, Callback<JsonElement> callback);

    @PUT(PROPOSITION_DIS)
    public void dismissPropostion(@Path("id") String id, Callback<JsonElement> callback);

    @GET(PROPOSITION_POPULAR)
    public void popular(Callback<List<Proposition>> callback);


    /**
     * Answers API
     *
     * @param id
     * @param callback
     */
    @PUT("/answers/{id}/acknowledge")
    public void acknowledge(@Path("id") String id, Callback<JsonElement> callback);
}
