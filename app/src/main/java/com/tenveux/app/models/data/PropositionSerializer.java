package com.tenveux.app.models.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.tenveux.app.models.Proposition;
import com.tenveux.app.models.User;

import java.lang.reflect.Type;

/**
 * Created by theGlenn on 12/02/15.
 */
public class PropositionSerializer implements JsonSerializer<Proposition> {
    public JsonElement serialize(final Proposition proposition, final Type type, final JsonSerializationContext context) {

        Gson g = new Gson();
        JsonObject result = (JsonObject) g.toJsonTree(proposition);

        result.addProperty("id", proposition.getId());

        if (proposition.getSender() != null) {
            result.addProperty("sender", proposition.getSender().getId());
        }

        JsonArray jArray = new JsonArray();

        for (User receiver : proposition.getReceivers()) {
            jArray.add(new JsonPrimitive(receiver.getId()));
        }

        result.add("receivers", jArray);

        if (proposition.getOriginalProposition() != null) {
            result.addProperty("originalProposition", proposition.getOriginalProposition());
        }

        return result;
    }
}