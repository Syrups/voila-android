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

        JsonObject elProposition = json.getAsJsonObject();
        JsonElement elSender = elProposition.get("sender");
        JsonElement elOriginalProposition = elProposition.get("originalProposition");
        JsonArray elReceivers = elProposition.get("receivers").getAsJsonArray();
        JsonArray elTakers = elProposition.get("takers").getAsJsonArray();

        if (elSender != null && !elSender.isJsonObject()) {
            elProposition.remove("sender");
        }

        if (elOriginalProposition != null && elOriginalProposition.isJsonObject()) {

            elProposition.remove("originalProposition");
        }


        if (!elTakers.isJsonNull()) {
            elProposition.remove("takers");
            /*if (elTakers.getAsJsonArray().size() > 0) {
                if (!elTakers.get(0).isJsonObject())
                    elPropositon.remove("takers");
            }*/
        }

        if (!elReceivers.isJsonNull() && elReceivers.size() > 0) {
            if (!elReceivers.get(0).isJsonObject()) {
                elProposition.remove("receivers");
            }
        }

        return new Gson().fromJson(elProposition, Proposition.class);

    }
}