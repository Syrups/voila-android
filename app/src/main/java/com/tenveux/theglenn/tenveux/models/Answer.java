package com.tenveux.theglenn.tenveux.models;

import com.google.gson.annotations.Expose;

/**
 * Created by theGlenn on 01/03/15.
 */
public class Answer {

    @Expose
    private Proposition proposition;

    @Expose
    private User from;

    @Expose
    private User to;

    @Expose
    private String answer;


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

}
