package com.tenveux.theglenn.tenveux;

import android.app.Application;

import com.tenveux.theglenn.tenveux.network.ApiController;
import com.tenveux.theglenn.tenveux.network.OffApiController;
import com.tenveux.theglenn.tenveux.network.apis.ApiUsers;
import com.tenveux.theglenn.tenveux.network.client.MockClient;

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
    private ApiUsers userService;

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
                .setClient(new MockClient())
                .build();
        //service = restAdapter.create(ApiController.class);
        userService = restAdapter.create(ApiUsers.class);


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

    public static ApiUsers userApi() {
        return sInstance.userService;
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