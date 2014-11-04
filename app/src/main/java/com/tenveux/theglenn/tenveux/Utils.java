package com.tenveux.theglenn.tenveux;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.facebook.model.GraphUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by theGlenn on 13/10/2014.
 */
public class Utils {

    public static JSONObject JSONIfyUser(GraphUser user){
        JSONObject userJSON = new JSONObject();
        try {
            userJSON.put("name", user.getName());
            userJSON.put("id", user.getId());


            // Example: access via key for array (languages)
            // - requires user_likes permission
            /*JSONArray languages = (JSONArray) user.getProperty("languages");
            if (languages.length() > 0) {
                ArrayList<String> languageNames = new ArrayList<String>();
                for (int i = 0; i < languages.length(); i++) {
                    JSONObject language = languages.optJSONObject(i);
                    // Add the language name to a list. Use JSON
                    // methods to get access to the name field.
                    languageNames.add(language.optString("name"));
                }
                //userJSON.
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userJSON;

    }

    public static String getFacebookPixURL(GraphUser user) {
       return "https://graph.facebook.com/" + user.getId() + "/picture?type=large" ;
    }
}
