package com.tenveux.theglenn.tenveux;

import android.app.Application;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by theGlenn on 13/10/2014.
 */
public class ApplicationController extends Application {


    /**
     * Log or request TAG
     */
    public static final String TAG = "Tenveux";


    /**
     * A singleton instance of the application class for easy access in other places
     */
    private static ApplicationController sInstance;
    private ApiController service;
    private OffApiController offService;
    private boolean isUserLoggedIn;
    private String userToken;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the singletons so their instances
        // are bound to the application process.
        initSingletons();

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                if (isUserLoggedIn()) {
                    request.addHeader("X-Authorization-Token", userToken);
                }
            }
        };

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setRequestInterceptor(requestInterceptor)
                .setEndpoint(ApiController.BASE_URL)
                .build();
        service = restAdapter.create(ApiController.class);

        RestAdapter offAdapter = new RestAdapter.Builder()
                .setRequestInterceptor(requestInterceptor)
                .setEndpoint(ApiController.BASE)
                .build();
        offService = offAdapter.create(OffApiController.class);

        //CalligraphyConfig
        CalligraphyConfig.initDefault("fonts/Roboto-Regular.ttf", R.attr.fontPath);
    }

    protected void initSingletons() {
        // initialize the singleton
        sInstance = this;
        // Initialize the instance of MySingleton
        //ApplicationController.initInstance();
    }


    /**
     * @return ApplicationController singleton instance
     */
    public static synchronized ApplicationController getInstance() {
        return sInstance;
    }

    public static ApiController api() {
        return sInstance.service;
    }
    public static OffApiController offApi() {
        return sInstance.offService;
    }


    public boolean isUserLoggedIn() {
        return isUserLoggedIn;
    }

    public void setIsUserLoggedIn(boolean is) {
        isUserLoggedIn = is;
    }

    public void setUserToken(String token) {
        userToken = token;
    }

    /*public void sendImage(final Bitmap photo) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
        byte[] byte_arr = stream.toByteArray();
        String image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);
        final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        MultipartEntity reqEntity = new MultipartEntity(
                HttpMultipartMode.BROWSER_COMPATIBLE);
        reqEntity.addPart("uploaded", bab);
        reqEntity.addPart("photoCaption", new StringBody("sfsdfsdf"));
        postRequest.setEntity(reqEntity);
        HttpResponse response = httpClient.execute(postRequest);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                response.getEntity().getContent(), "UTF-8"));
        String sResponse;
        StringBuilder s = new StringBuilder();

        while ((sResponse = reader.readLine()) != null) {
            s = s.append(sResponse);
        }
        System.out.println("Response: " + s);

        nameValuePairs.add(new BasicNameValuePair("image", image_str));
        new Thread(new Runnable() {
            public void run() {


                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(ApiController.IMAGE);
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                } catch (Exception e) {

                    System.out.println("Error in http connection " + e.toString());
                }
            }
        }).start();
    }*/
}