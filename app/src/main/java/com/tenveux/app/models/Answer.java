package com.tenveux.app.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.tenveux.app.models.data.PropositionDeserializer;

/**
 * Created by theGlenn on 01/03/15.
 */
public class Answer {

    @Expose
    private String id;

    @Expose
    private Proposition proposition;

    @Expose
    private User from;

    @Expose
    private User to;

    @Expose
    private String answer;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Proposition getProposition() {
        return proposition;
    }

    public void setProposition(Proposition proposition) {
        this.proposition = proposition;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isYes() {
        return this.answer.equals("yes");
    }


    public static Answer fromJson(String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Proposition.class, new PropositionDeserializer());
        Gson gson = gsonBuilder.create();

        return gson.fromJson(json, Answer.class);
    }

    public static Answer fromJson(JsonElement json) {
        return fromJson(json.toString());
    }
}
