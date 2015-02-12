package com.tenveux.theglenn.tenveux.network.client;

import android.net.Uri;
import android.util.Log;

import retrofit.client.Request;

/**
 * Created by theGlenn on 05/02/15.
 */


public class UserClient {

    public static final String receivedResponse = "[ { _id: '54d370f5e4084d000084a592', sender: '54d370f5e4084d000084a58e', image: '3256660cff7a867141e3c79d5001d8bd.jpg', id: '54d370f5e4084d000084a592', __v: 0, answerAcknowledged: false, meta: { takeCount: 0 }, dismissers: [], takers: [ '54d370f5e4084d000084a58f' ], resenders: [ '54d370f5e4084d000084a58f' ], originalProposition: null, receivedAt: null, sentAt: '2015-02-05T13:32:37.493Z', isPrivate: false, receivers: [ '54d370f5e4084d000084a58f', '54d370f5e4084d000084a590' ] } ]";
    public static final String mockFriends = "[ { _id: '54d8a34f5b98e6ee6cce9c57',\n" +
            "    username: 'leo',\n" +
            "    email: 'leoht@gmail.com',\n" +
            "    id: '54d8a34f5b98e6ee6cce9c57',\n" +
            "    __v: 0,\n" +
            "    taken: 0,\n" +
            "    sent: 0 } ]";

    public static String handleUserRequest(Uri uri, Request request) {


        String responseString = "";
        String route = MockClient.getLastBitFromUrl(uri.getPath());

        Log.d("handleUserRequest", request.getMethod() + "/ fetching uri: " + MockClient.getLastBitFromUrl(uri.getPath()));

        if (MockClient.getLastBitFromUrl(uri.getPath()).equals("authenticate")) {

            responseString = "{ \"id\": \"1234\", \"username\": \"Doe\", \"avatar\": \"a.png\" , \"token\": \"xxxxxxxxxx\"}";

        } else if (route.equals("received")) {

            responseString = receivedResponse;

        } else if (route.equals("pending")) {

            responseString = receivedResponse;

        } else if (route.equals("taken")) {

            responseString = receivedResponse;

        } else if (route.equals("friends")) {

            responseString = mockFriends;

        } else {
            responseString = "{ \"id\": \"1992X\", \"username\": \"glenn\", \"avatar\": \"glenn.png\" , \"token\": \"xx_-_xxxx\"}";
        }
        return responseString;
    }
}
