package com.tenveux.theglenn.tenveux.network.client;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Collections;

import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by theGlenn on 12/02/15.
 */
public class PropositionClient {

    public static final String created = "{ data: \n" +
            "   { __v: 0,\n" +
            "     sender: '54dd042f20fdc42a1aa8d1a4',\n" +
            "     image: '92689ba262a36d69b63db3518e84c8bd.jpg',\n" +
            "     _id: '54dd042f20fdc42a1aa8d1a8',\n" +
            "     answerAcknowledged: false,\n" +
            "     meta: { takeCount: 0 },\n" +
            "     dismissers: [],\n" +
            "     takers: [],\n" +
            "     resenders: [],\n" +
            "     receivedAt: null,\n" +
            "     sentAt: '2015-02-12T19:51:11.989Z',\n" +
            "     receivers: [ '54dd042f20fdc42a1aa8d1a5', '54dd042f20fdc42a1aa8d1a6' ],\n" +
            "     id: '54dd042f20fdc42a1aa8d1a8' },\n" +
            "  message: 'Proposition created' }";

    public static Response handleUserRequest(Uri uri, Request request) {

        int code = 200;
        String responseString = "";
        String route = MockClient.getLastBitFromUrl(uri.getPath());

        Log.d("handleUserRequest", request.getMethod() + "/ fetching uri: " + MockClient.getLastBitFromUrl(uri.getPath()));

        if (request.getMethod() == "POST") {

            Gson g = new Gson();

            Log.d(uri.getPath(), "posting \n" + g.toJson(request.getBody()));
            code = 201;
            responseString = created;

        } else if (route.equals("dismiss")) {


        } else if (route.equals("take")) {


        } else if (route.equals("takers")) {


        }

        return new Response(request.getUrl(), code, "nothing", Collections.EMPTY_LIST, new TypedByteArray("application/json", responseString.getBytes()));
    }
}
