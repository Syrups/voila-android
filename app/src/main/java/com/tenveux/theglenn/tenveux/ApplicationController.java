package com.tenveux.theglenn.tenveux;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tenveux.theglenn.tenveux.models.Proposition;
import com.tenveux.theglenn.tenveux.models.data.PropositionSerializer;
import com.tenveux.theglenn.tenveux.network.ApiController;
import com.tenveux.theglenn.tenveux.network.OffApiController;
import com.tenveux.theglenn.tenveux.network.apis.ApiPropositions;
import com.tenveux.theglenn.tenveux.network.apis.ApiUsers;
import com.tenveux.theglenn.tenveux.network.client.MockClient;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
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
    private ApiUsers userService;
    private ApiPropositions propoService;

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

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Proposition.class, new PropositionSerializer()) // This is the important line ;)
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(ApiController.BASE_URL)
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(requestInterceptor)
                .setClient(new MockClient())
                .build();
        //service = restAdapter.create(ApiController.class);
        userService = restAdapter.create(ApiUsers.class);
        propoService = restAdapter.create(ApiPropositions.class);


        RestAdapter offAdapter = new RestAdapter.Builder()
                .setRequestInterceptor(requestInterceptor)
                .setEndpoint(ApiController.BASE)
                .setClient(new MockClient())
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

    public static ApiUsers userApi() {
        return sInstance.userService;
    }

    public static ApiPropositions propositionApi() {
        return sInstance.propoService;
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

}