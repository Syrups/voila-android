package com.tenveux.theglenn.tenveux.apimodel;

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
    private String sender;
    @Expose
    private List<String> receivers = new ArrayList<String>();
    @Expose
    private String senderName;
    @Expose
    private String sentAt;
    @Expose
    private String receivedAt;
    @Expose
    private String originalProposition;
    @Expose
    private String image;
    @Expose
    private Boolean taken;
    @Expose
    private Boolean dismissed;
    @Expose
    private Integer reproposedCount;
    @Expose
    private List<String> takers = new ArrayList<String>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
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

    public Boolean getTaken() {
        return taken;
    }

    public void setTaken(Boolean taken) {
        this.taken = taken;
    }

    public Boolean getDismissed() {
        return dismissed;
    }

    public void setDismissed(Boolean dismissed) {
        this.dismissed = dismissed;
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


}
