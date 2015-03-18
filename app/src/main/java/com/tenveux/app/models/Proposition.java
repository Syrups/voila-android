package com.tenveux.app.models;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

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
}
