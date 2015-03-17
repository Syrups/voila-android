package com.tenveux.theglenn.tenveux.network;

import com.google.gson.JsonElement;

import retrofit.Callback;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

/**
 * Created by theGlenn on 17/10/2014.
 */
public interface OffApiController {

    public static final String UPLOAD_URL = "/images";

    @Multipart
    @POST(UPLOAD_URL)
    public void sendImage(@Part("file") TypedFile image, Callback<JsonElement> callback);
}
