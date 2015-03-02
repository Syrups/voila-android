package com.tenveux.theglenn.tenveux.network;

import com.google.gson.JsonElement;
import com.tenveux.theglenn.tenveux.models.CreateUserResponse;
import com.tenveux.theglenn.tenveux.models.Proposition;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * Created by theGlenn on 13/10/2014.
 */
public interface ApiController {


    public static final String BASE = "http://tenveux.herokuapp.com";
            //"http://192.168.1.29:5000";
    //"http://10.0.3.2:5000";
    //public static final String BASE = "http://mockserver.com";

    //public static final String BASE =  "http://tenveux.herokuapp.com";
    //"http://192.168.2.2:5000";

    public static final String BASE_IMG = BASE; //"http://10.0.3.2:5000";
    public static final String BASE_URL = BASE + "/api/";
    public static final String USERS_ME = "/me";
    public static final String USERS_CREATE = "/users/create/";
    public static final String USERS_SHOW = "/users/{id}";
    public static final String USERS_TAKEN = "/users/{id}/taken";
    public static final String USERS_PENDING = "/users/{id}/pending";
    public static final String USERS_RECEIVED = "/users/{id}/received";
    public static final String USERS_SENT = "/users/{id}/sent";


}
