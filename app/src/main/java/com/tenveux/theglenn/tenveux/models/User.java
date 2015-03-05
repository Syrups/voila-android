package com.tenveux.theglenn.tenveux.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;

import java.lang.reflect.Type;

/**
 * Created by theGlenn on 16/10/2014.
 */
public class User {

    @Expose
    private String message;

    @Expose
    private String id;

    @Expose
    private String email;

    @Expose
    private String username;

    @Expose
    private String avatar;

    @Expose
    private String token;

    @Expose
    private int sent;

    @Expose
    private int taken;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return username;
    }

    public void setName(String name) {
        this.username = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getSent() {
        return sent;
    }

    public void setSent(int sent) {
        this.sent = sent;
    }

    public int getTaken() {
        return taken;
    }

    public void setTaken(int avatar) {
        this.taken = taken;
    }
}
