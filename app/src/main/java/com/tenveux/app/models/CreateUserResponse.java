package com.tenveux.app.models;

/**
 * Created by theGlenn on 16/10/2014.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class CreateUserResponse {
    @Expose
    private Integer code;
    @Expose
    private String message;
    @Expose
    private String id;
    @SerializedName("facebook_id")
    @Expose
    private String facebookId;
    @Expose
    private String name;
    @Expose
    private String token;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

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

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}