package com.tenveux.theglenn.tenveux;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.tenveux.theglenn.tenveux.models.User;
import com.tenveux.theglenn.tenveux.network.ApiController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by theGlenn on 13/10/2014.
 */
public class Utils {

    public static String getFacebookPixURL(User user) {
       return "https://graph.facebook.com/" + user.getId() + "/picture?type=large" ;
    }

    public static String getMediaAvatarUrl(User user) {
        return "https://graph.facebook.com/" + user.getId() + "/picture?type=large" ;
    }

    public static String getPropositionMediaUrl(User user) {
        return "https://graph.facebook.com/" + user.getId() + "/picture?type=large" ;
    }

    public static String getImage2(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = ApiController.BASE_IMG + uri.getRawPath();
        return domain;
    }

}
