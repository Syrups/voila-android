package com.tenveux.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tenveux.app.models.Proposition;
import com.tenveux.app.models.User;
import com.tenveux.app.models.data.PropositionDeserializer;
import com.tenveux.app.models.data.PropositionSerializer;
import com.tenveux.app.network.Api;
import com.tenveux.app.network.MediaController;
import com.tenveux.app.network.apis.ApiPropositions;
import com.tenveux.app.network.apis.ApiUsers;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by theGlenn on 13/10/2014.
 */
public class ApplicationController extends Application {

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


    /**
     * GCM
     */
    public static final String SENDER_ID = "111643242567";
    static GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();

    /**
     * Log or request TAG
     */
    public static final String TAG = "Tenveux";


    /**
     * A singleton instance of the application class for easy access in other places
     */
    private static ApplicationController sInstance;
    private MediaController mMediaService;
    private ApiUsers userService;
    private ApiPropositions propoService;

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

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Proposition.class, new PropositionSerializer()) // This is the important line ;)
                .registerTypeAdapter(Proposition.class, new PropositionDeserializer())
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(Api.BASE_URL)
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(requestInterceptor)
                        //.setClient(new MockClient())
                .build();
        //service = restAdapter.create(ApiController.class);
        userService = restAdapter.create(ApiUsers.class);
        propoService = restAdapter.create(ApiPropositions.class);


        RestAdapter mediAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setEndpoint(Api.BASE)
                .setRequestInterceptor(requestInterceptor)
                .build();
        mMediaService = mediAdapter.create(MediaController.class);

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

    public static ApiUsers userApi() {
        return sInstance.userService;
    }

    public static ApiPropositions propositionApi() {
        return sInstance.propoService;
    }

    public static MediaController media() {
        return sInstance.mMediaService;
    }

    public boolean isUserLoggedIn() {
        return userToken != null;
    }

    public void setUserToken(String token) {
        userToken = token;
    }

    public ApplicationController get() {
        return this;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    public String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private static SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.

        return context.getSharedPreferences("Voila_GCM",
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }


    public static void registerInBackground(final String id, final Context context) {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

                String msg = "";

                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }

                    String regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;


                    Map<String, String> datas = new LinkedHashMap<>();
                    datas.put("android_token", regid);

                    ApplicationController.userApi().update(id, datas, new Callback<User>() {
                        @Override
                        public void success(User user, Response response) {

                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });

                    // Persist the registration ID - no need to register again.
                    storeRegistrationId(context, regid);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.e(TAG, msg);

                    ex.printStackTrace();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }


            @Override
            protected void onPostExecute(String msg) {
                Log.d(TAG, msg);
                //mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public static boolean checkPlayServices(Activity a) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(a);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, a,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }
}