package org.jekajops.payment_service.core.utils.parsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonParserUtil {
    public static String getValue(String jsonString, String field) {
        try {
            return new JSONObject(jsonString).get(field).toString();
        } catch (JSONException e) {
            return "";
        }
    }

    public static String getStringValue(String jsonString, String field){
        try {
            return new JSONObject(jsonString).getString(field);
        } catch (JSONException e) {
            return "";
        }
    }
    public static String getStringValue(JSONObject jsonObject, String field) {
        try {
            return jsonObject.getString(field);
        } catch (JSONException e) {
            return "";
        }
    }
    public static int getIntValue(String jsonString, String field) {
        try {
            return new JSONObject(jsonString).getInt(field);
        } catch (JSONException e) {
            return 0;
        }
    }
    public static int getIntValue(JSONObject jsonObject, String field) {
        try {
            return jsonObject.getInt(field);
        } catch (JSONException e) {
            return 0;
        }
    }
    public static List<JSONObject> getArray(String jsonString) {
        List<JSONObject> list = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonString);
        for (int i = 0; i < jsonArray.length(); i++) {
            Object object = jsonArray.get(i);
            String str = object.toString();
            str = str.replaceAll("=", ":");
            JSONObject jsonObject = new JSONObject(str);
            list.add(jsonObject);
        }
        return list;
    }
}
