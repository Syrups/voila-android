package com.tenveux.theglenn.tenveux;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonElement;
import com.tenveux.theglenn.tenveux.apimodel.CreateUserResponse;
import com.tenveux.theglenn.tenveux.apimodel.Proposition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import retrofit.mime.TypedFile;

/**
 * Created by theGlenn on 13/10/2014.
 */
public interface ApiController {

    public static final String BASE = "http://10.0.3.2:5000/";
    public static final String BASE_IMG = "http://10.0.3.2:5000";
    public static final String BASE_URL = "http://10.0.3.2:5000/api/";
    public static final String USERS_ME = "/me";
    public static final String USERS_CREATE = "/users/create/";
    public static final String USERS_SHOW = "/users/{id}";
    public static final String USERS_TAKEN = "/users/{id}/taken";
    public static final String USERS_PENDING = "/users/{id}/pending";
    public static final String USERS_RECEIVED = "/users/{id}/received";
    public static final String USERS_SENT = "/users/{id}/sent";

    //Proposition
    public static final String PROPOSITION_SEND = "/propositions";
    public static final String PROPOSITION_TAKE = "/propositions/{id}/take";
    public static final String PROPOSITION_DIS = "/propositions/{id}/dismiss";

    public static final String IMAGE = "/images";


    @GET(USERS_CREATE)
    public void createUser(@QueryMap Map<String, String> options, Callback<CreateUserResponse> callback);

    @GET(USERS_RECEIVED)
    public void getReceivedPropostion(@Path("id") String id, Callback<List<Proposition>> callback);

    @GET(USERS_PENDING)
    public void getPendingPropostion(@Path("id") String id, Callback<List<Proposition>> callback);

    //Proposition functions

    @POST(PROPOSITION_SEND)
    public void sendPropostion(@Body Proposition proposition, Callback<JsonElement> callback);

    @GET(PROPOSITION_TAKE)
    public void takePropostion(@Path("id") String id, Callback<JsonElement> callback);

    @GET(PROPOSITION_DIS)
    public void dismissPropostion(@Path("id") String id, Callback<JsonElement> callback);

}
