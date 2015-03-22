
package com.tenveux.app.models;

import com.google.gson.annotations.Expose;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.tenveux.app.models.data.PropositionDeserializer;
import com.tenveux.app.models.data.PropositionSerializer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.List;

import retrofit.mime.TypedFile;

/**
 * Created by theGlenn on 16/10/2014.
 */
public class Proposition {

    @Expose
    private String id;

    @Expose
    private User sender;

    @Expose
    private List<String> allReceivers = new ArrayList<String>();

    @Expose
    private List<User> receivers = new ArrayList<User>();

    @Expose
    private String sentAt;
    @Expose
    private String receivedAt;

    @Expose
    private String originalProposition;
    @Expose
    private String image;


    @Expose
    private Boolean isPrivate;

    @Expose
    private Integer reproposedCount;
    @Expose
    private List<String> takers = new ArrayList<String>();

    @Expose
    private int sent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }


    public List<User> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<User> receivers) {
        this.receivers = receivers;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    public String getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(String receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getOriginalProposition() {
        return originalProposition;
    }

    public void setOriginalProposition(String originalProposition) {
        this.originalProposition = originalProposition;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }


    public Integer getReproposedCount() {
        return reproposedCount;
    }

    public void setReproposedCount(Integer reproposedCount) {
        this.reproposedCount = reproposedCount;
    }

    public List<String> getTakers() {
        return takers;
    }

    public void setTakers(List<String> takers) {
        this.takers = takers;
    }


    public int getSent() {
        return sent;
    }

    public void setSent(int sent) {
        this.sent = sent;
    }


    public static void cacheProposition(Context context, Proposition proposition, TypedFile image) {


        ArrayList<Proposition> propositions = getCachedPropositions(context);
        proposition.setImage(image.fileName());
        propositions.add(proposition);

        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Proposition.class, new PropositionSerializer());
            Gson gson = gsonBuilder.create();

            Type collectionType = new TypeToken<ArrayList<Proposition>>() {
            }.getType();

            String data = gson.toJson(propositions, collectionType);

            FileOutputStream out = context.openFileOutput("Propositions.json", Context.MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out);

            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    public static ArrayList<Proposition> getCachedPropositions(Context context) {

        ArrayList<Proposition> propositions = new ArrayList<>();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Proposition.class, new PropositionDeserializer());
        Gson gson = gsonBuilder.create();

        Type collectionType = new TypeToken<ArrayList<Proposition>>() {
        }.getType();

        String json;

        try {
            InputStream inputStream = context.openFileInput("Propositions.json");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                json = stringBuilder.toString();
                propositions = gson.fromJson(json, collectionType);
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return propositions;
    }
}

