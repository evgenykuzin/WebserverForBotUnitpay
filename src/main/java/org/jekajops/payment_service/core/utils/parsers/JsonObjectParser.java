package org.jekajops.payment_service.core.utils.parsers;

public class JsonObjectParser {
    private String jsonString;
    public JsonObjectParser(String jsonString) {
        this.jsonString = jsonString;
    }
    public String get(String key){
        return JsonParserUtil.getValue(jsonString, key);
    }
}
