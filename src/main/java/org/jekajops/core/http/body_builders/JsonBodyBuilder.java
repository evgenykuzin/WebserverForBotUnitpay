package org.jekajops.core.http.body_builders;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface JsonBodyBuilder extends BodyBuilder {
    Body<JsonObject> construct(JsonObject object);
    static JsonObject construct(String jsonString) {
        return new JsonParser().parse(jsonString).getAsJsonObject();
    }
    default Body<JsonObject> construct() {
        JsonObject object = new JsonObject();
        return construct(object);
    }

    default Map<String, String> getBodyMap() {
        Set<Map.Entry<String, JsonElement>> entrySet = construct().getObject().entrySet();
        Map<String, String> stringMap = new HashMap<>();
        entrySet.forEach(entry -> {
            String value;
            JsonElement element = entry.getValue();
            if (element.isJsonArray()) {
                value = element.getAsJsonArray().getAsString();
            } else if (element.isJsonObject()) {
                value = element.getAsJsonObject().toString();
            } else if (element.isJsonPrimitive()) {
                value = element.getAsJsonPrimitive().getAsString();
            } else {
                value = element.getAsJsonNull().getAsString();
            }
            stringMap.put(entry.getKey(), value);
        });
        return stringMap;
    }

    default String getJsonString() {
        return new Gson().toJson(construct().getObject());
    }

    class BodyJson implements Body<JsonObject> {
        private final JsonObject object;
        public BodyJson(JsonObject object) {
            this.object = object;
        }
        @Override
        public JsonObject getObject() {
            return object;
        }
    }

    class BodyWithFiles implements Body<JsonObject> {
        private final JsonObject object;
        private final Map<String, File> files;

        public BodyWithFiles(JsonObject object, Map<String, File> files) {
            this.object = object;
            this.files = files;
        }

        @Override
        public JsonObject getObject() {
            return object;
        }

        public Map<String, File> getFiles() {
            return files;
        }
    }
}
