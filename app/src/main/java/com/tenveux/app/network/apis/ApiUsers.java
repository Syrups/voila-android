package com.tenveux.app.network.apis;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tenveux.app.models.Proposition;
import com.tenveux.app.models.User;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * Created by theGlenn on 04/02/15.
 */
public interface ApiUsers {

    public static final String API = "/users/";

    /**
     * password
     * email
     * username
     */
    @POST(API)
    public void createUser(@QueryMap Map<String, String> options, Callback<User> callback);


    @PUT(API + "{id}")
    public void update(@Path("id") String id, @QueryMap Map<String, String> options, Callback<User> callback);

    /**
     * password
     * username
     */
    @POST(API + "authenticate")
    public void authenticate(@QueryMap Map<String, String> options, Callback<User> callback);

    /**
     * User
     */
    @PUT(API + "{id}/addfriends")
    public void addFriend(@Path("id") String id, @QueryMap Map<String, String> options, Callback<JsonElement> cb);


    /**
     * username
     */
    @GET(API + "find/{name}")
    public void find(@Path("name") String name, Callback<List<User>> callback);

    @GET(API + "{id}")
    public void findByID(@Path("id") String id, Callback<User> callback);

    @GET(API + "{id}/friends")
    public void friends(@Path("id") String id, Callback<List<User>> callback);

    @GET(API + "{id}/requests")
    public void requests(@Path("id") String id, Callback<List<User>> callback);

    @GET(API + "{id}/received")
    public void getReceivedProposition(@Path("id") String id, Callback<List<Proposition>> callback);

    @GET(API + "{id}/taken")
    public void getTakenProposition(@Path("id") String id, Callback<List<Proposition>> callback);

    @GET(API + "{id}/pending")
    public void getPendingProposition(@Path("id") String id, Callback<List<Proposition>> callback);


    @GET(API + "{id}/sent")
    public void sent(@Path("id") String id, Callback<User> callback);

    @GET(API + "{id}/answers")
    public void answers(@Path("id") String id, Callback<User> callback);

    @GET(API + "{id}/pendingall")
    public void pendingall(@Path("id") String id, Callback<JsonObject> callback);

/*
    @GET(USERS_PENDING)
    public void getPendingPropostion(@Path("id") String id, Callback<List<Proposition>> callback);

    //Proposition functions

    @POST(PROPOSITION_SEND)
    public void sendPropostion(@Body Proposition proposition, Callback<JsonElement> callback);

    @GET(PROPOSITION_TAKE)
    public void takePropostion(@Path("id") String id, Callback<JsonElement> callback);

    @GET(PROPOSITION_DIS)
    public void dismissPropostion(@Path("id") String id, Callback<JsonElement> callback);
    */
}
