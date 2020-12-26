package org.jekajops.vk.buttons;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jekajops.vk.VKCore;

public class KeyboardBuilder {
    private final JsonObject json;

    public KeyboardBuilder(boolean oneTime, boolean inline) {
        json = new JsonObject();
        json.addProperty("one_time", oneTime);
        json.addProperty("inline", inline);
        JsonArray buttons = new JsonArray();
        json.add("buttons", buttons);
        addLine();
    }

    public KeyboardBuilder addLine(JsonArray line) {
        getButtons().add(line);
        return this;
    }

    public KeyboardBuilder addLine() {
        return addLine(new JsonArray());
    }

    public JsonArray getLine(int i) {
        return (JsonArray) getButtons().get(i);
    }

    private JsonArray getButtons() {
        return json.getAsJsonArray("buttons");
    }

    public KeyboardBuilder addTextButton(Button button, int line) {
        if (line >= getButtons().size()) addLine();
        JsonObject payloadJO = new JsonObject();
        payloadJO.addProperty("button", "2");
        String type = "text";
        JsonObject jsonObject = new JsonObject();
        JsonObject action = new JsonObject();
        action.addProperty("type", type);
        action.add("payload", payloadJO);
        if (button.getName() != null) action.addProperty("label", button.getName());
        if (button.getName() != null) jsonObject.addProperty("color", button.getColor());
        jsonObject.add("action", action);
        getLine(line).add(jsonObject);
        return this;
    }

    public KeyboardBuilder addVkPayButton(int line) {
        if (line >= getButtons().size()) addLine();
        String type = "vkpay";
        String id = "186088523";
        String hash = "action=transfer-to-group&group_id=" + id + "&aid=" + id;
        JsonObject jsonObject = new JsonObject();
        JsonObject action = new JsonObject();
        action.addProperty("type", type);
        action.addProperty("hash", hash);
        jsonObject.add("action", action);
        getLine(line).add(jsonObject);
        return this;
    }

    public KeyboardBuilder addButton(String label, String color, String type, JsonObject payload, int line) {
        if (line >= getButtons().size()) addLine();
        JsonObject jsonObject = new JsonObject();
        JsonObject action = new JsonObject();
        if (type != null) action.addProperty("type", type);
        if (payload != null) action.add("payload", payload);
        if (label != null) action.addProperty("label", label);
        if (color != null) jsonObject.addProperty("color", color);
        jsonObject.add("action", action);
        getLine(line).add(jsonObject);
        return this;

    }

    public KeyboardBuilder addButton(String label, String color, String type, int line) {
        JsonObject payloadJO = new JsonObject();
        payloadJO.addProperty("button", "2");
        return addButton(label, type, color, payloadJO, line);
    }

    public KeyboardBuilder addButton(String label, String color, int line) {
        JsonObject payloadJO = new JsonObject();
        payloadJO.addProperty("button", "2");
        return addButton(label, color, "text", payloadJO, line);
    }

    public KeyboardBuilder addButton(String label, String color) {
        return addButton(label, color, 0);
    }

    public KeyboardBuilder addLinkButton(String label, String link, int line) {
        if (line >= getButtons().size()) addLine();
        JsonObject jsonObject = new JsonObject();
        JsonObject action = new JsonObject();
        JsonObject payloadJO = new JsonObject();
        payloadJO.addProperty("button", "2");
        action.addProperty("type", "open_link");
        action.add("payload", payloadJO);
        if (link != null) action.addProperty("link", link);
        if (label != null) action.addProperty("label", label);
        jsonObject.add("action", action);
        getLine(line).add(jsonObject);
        return this;
    }

    public boolean contains(Button button) {
        for (var b : getButtons()) {
            var action = b.getAsJsonArray().get(0).getAsJsonObject().get("action");
            var label = action.getAsJsonObject().get("label");
            if (label.getAsString().equals(button.getName())) {
                return true;
            }

        }
        return false;
    }

    public String getJson() {
        return json.toString();
    }

}
