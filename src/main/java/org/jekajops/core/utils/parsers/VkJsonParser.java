package org.jekajops.core.utils.parsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vk.api.sdk.exceptions.ClientException;
import org.jekajops.vk.VKCore;

public class VkJsonParser {

    public static String getUserRealNameById(int id, VKCore vkCore){
        String userJson;
        String name = "";
        try {
            userJson = vkCore
                    .getVk()
                    .users()
                    .get(vkCore.getGroupActor())
                    .userIds(String.valueOf(id)).executeAsString();
            JsonObject jobj = new JsonParser().parse(userJson).getAsJsonObject();
            JsonArray jarr = jobj.getAsJsonArray("response");
            if (jarr.size() < 1) {
                name = "unknown";
            } else {
                name = jarr.get(0).getAsJsonObject().get("first_name").getAsString() +
                        " " +
                        jarr.get(0).getAsJsonObject().get("last_name").getAsString();
            }
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return name;

    }
}
