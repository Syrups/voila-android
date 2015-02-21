package com.tenveux.theglenn.tenveux.models.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.tenveux.theglenn.tenveux.models.Proposition;

import java.lang.reflect.Type;

/**
 * Created by theGlenn on 12/02/15.
 */
public class PropositionSerializer implements JsonSerializer<Proposition> {
    public JsonElement serialize(final Proposition proposition, final Type type, final JsonSerializationContext context) {

        Gson g = new Gson();
        JsonObject result = (JsonObject) g.toJsonTree(proposition);

        result.addProperty("id", proposition.getId());
        result.addProperty("sender", proposition.getSender().getId());
        //result.addProperty("originalProposition", proposition.getOriginalProposition());

        return result;
    }
}