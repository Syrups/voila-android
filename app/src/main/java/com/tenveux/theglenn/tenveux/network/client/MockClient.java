package com.tenveux.theglenn.tenveux.network.client;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;

import retrofit.client.Client;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by theGlenn on 05/02/15.
 */
public class MockClient implements Client {

    @Override
    public Response execute(Request request) throws IOException {
        Uri uri = Uri.parse(request.getUrl());

        Log.d("MOCK SERVER", request.getMethod() + "/ fetching uri: " + uri.getPath());

        String responseString = "";

        if (uri.getPath().contains("/api/users/")) {
            responseString = UserClient.handleUserRequest(uri, request);
        } else if (uri.getPath().contains("/api/proposition/")) {
            responseString = "{\n" +
                    "  \"id\": \"1234\",\n" +
                    "  \"username\": \"dozen\",\n" +
                    "  \"avatar\": \"a.png\"\n" +
                    "}";
        }else{
            responseString = "{\n" +
                    "  \"id\": \"1234\",\n" +
                    "  \"username\": \"dozen\",\n" +
                    "  \"avatar\": \"a.png\"\n" +
                    "}";
        }

        return new Response(request.getUrl(), 200, "nothing", Collections.EMPTY_LIST, new TypedByteArray("application/json", responseString.getBytes()));
    }

    public static String getLastBitFromUrl(final String url) {
        // return url.replaceFirst("[^?]*/(.*?)(?:\\?.*)","$1);" <-- incorrect
        return url.replaceFirst(".*/([^/?]+).*", "$1");
    }
}