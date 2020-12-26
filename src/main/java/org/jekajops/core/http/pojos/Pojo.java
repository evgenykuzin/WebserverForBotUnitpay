package org.jekajops.core.http.pojos;

import com.google.gson.Gson;

import java.util.Map;

public interface Pojo {
    String getJsonString();
    default String gsonParse() {
        return new Gson().toJson(this);
    }
}
