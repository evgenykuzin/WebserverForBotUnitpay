package org.jekajops.core.utils.parsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vk.api.sdk.exceptions.ClientException;
import org.jekajops.vk.VKServer;

public class VkJsonParser {

    public static int getUserIdByScreenName(String screenName) {
        String userJson;
        String id = "";
        try {
            userJson = VKServer
                    .vkCore
                    .getVk()
                    .users()
                    .get(VKServer.vkCore.getGroupActor())
                    .userIds(screenName).executeAsString();
            JsonObject jobj = new JsonParser().parse(userJson).getAsJsonObject();
            JsonArray jarr = jobj.getAsJsonArray("response");
            id = jarr.get(0).getAsJsonObject().get("id").getAsString();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(id);
    }

    public static String getUserRealNameById(int id){
        String userJson;
        String name = "";
        try {
            userJson = VKServer
                    .vkCore
                    .getVk()
                    .users()
                    .get(VKServer.vkCore.getGroupActor())
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
