package com.tenveux.app.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.client.methods.HttpPost;

import java.io.File;
import java.io.IOException;

import retrofit.Callback;
import retrofit.http.Multipart;

/**
 * Created by theGlenn on 22/03/15.
 */
public class MediaAPI {
    private static final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");

    private static final OkHttpClient client = new OkHttpClient();


    public static void sendImage(String userToken, File image, ImageCallback callback) {

        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"file\""),
                        RequestBody.create(MEDIA_TYPE_JPEG, image))
                .build();

        Request request = new Request.Builder()

                .header("X-Authorization-Token", userToken)
                .url(Api.BASE_URL + MediaController.UPLOAD_URL)
                .post(requestBody)
                .build();


        try {
            Log.d("start", request.url().toString());

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                Log.d("err", response.body().string());
                callback.failure(response);

            } else {

                Log.d("done", response.body().string());

                JsonElement json = new JsonParser().parse(response.body().toString());
                callback.succes(json);
            }

        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    public interface ImageCallback {
        public void succes(JsonElement element);

        public void failure(Response response);
    }
}
