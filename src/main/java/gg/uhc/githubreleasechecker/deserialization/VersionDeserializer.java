package gg.uhc.githubreleasechecker.deserialization;

import com.github.zafarkhaja.semver.Version;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

class VersionDeserializer implements JsonDeserializer<Version> {
    @Override
    public Version deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        try {
            return Version.valueOf(json.getAsJsonPrimitive().getAsString());
        } catch (Exception ex) {
            return null;
        }
    }
}
