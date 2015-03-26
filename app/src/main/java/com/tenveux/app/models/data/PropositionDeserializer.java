package com.tenveux.app.models.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.tenveux.app.models.Proposition;

import java.lang.reflect.Type;

/**
 * Created by theGlenn on 03/03/15.
 */
public class PropositionDeserializer implements JsonDeserializer<Proposition> {
    @Override
    public Proposition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject elPropositon = json.getAsJsonObject();
        JsonElement elSender = elPropositon.get("sender");
        JsonElement elOriginalProposition = elPropositon.get("originalProposition");
        JsonArray elReceivers = elPropositon.get("receivers").getAsJsonArray();
        JsonArray elTakers = elPropositon.get("takers").getAsJsonArray();

        if (!elSender.isJsonObject()) {
            elPropositon.remove("sender");
        }

        if (elOriginalProposition != null && elOriginalProposition.isJsonObject()) {

            elPropositon.remove("originalProposition");
        }


        if (!elTakers.isJsonNull()) {
            elPropositon.remove("takers");
            /*if (elTakers.getAsJsonArray().size() > 0) {
                if (!elTakers.get(0).isJsonObject())
                    elPropositon.remove("takers");
            }*/
        }

        if (!elReceivers.isJsonNull()) {
            if (!elReceivers.get(0).isJsonObject()) {
                elPropositon.remove("receivers");
            }
        }

        return new Gson().fromJson(elPropositon, Proposition.class);

    }
}