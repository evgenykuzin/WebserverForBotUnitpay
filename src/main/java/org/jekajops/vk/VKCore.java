package org.jekajops.vk;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;
import org.jekajops.core.utils.files.PropertiesManager;


import java.util.List;
import java.util.Properties;
import java.util.logging.LogManager;

public class VKCore {
    private final VkApiClient vk;
    private static int ts;
    private final GroupActor groupActor;
    private static int maxMsgId = -1;
    public static int groupId;
    public static String access_token;
    public VKCore() throws ClientException, ApiException {
        System.setProperty("-Dorg.slf4j.simpleLogger.defaultLogLevel", "OFF");
        LogManager.getLogManager().reset();
        TransportClient transportClient = HttpTransportClient.getInstance();
        vk = new VkApiClient(transportClient);
        System.setProperty("-Dorg.slf4j.simpleLogger.defaultLogLevel", "OFF");
        LogManager.getLogManager().reset();
        Properties properties = PropertiesManager.getProperties("bot");
        access_token = properties.getProperty("access_token");
        groupId = Integer.parseInt(properties.getProperty("group_id"));
        groupActor = new GroupActor(groupId, access_token);
        ts = vk.messages().getLongPollServer(groupActor).execute().getTs();
    }

    public GroupActor getGroupActor() {
        return groupActor;
    }
    public VkApiClient getVk() {
        return vk;
    }
    public Message getMessage() throws ClientException, ApiException {
        MessagesGetLongPollHistoryQuery eventsQuery = vk.messages()
                .getLongPollHistory(groupActor)
                .ts(ts);
        if (maxMsgId > 0){
            eventsQuery.maxMsgId(maxMsgId);
        }
        List<Message> messages = eventsQuery
                .execute()
                .getMessages()
                .getMessages();

        if (!messages.isEmpty()){
            try {
                ts =  vk.messages()
                        .getLongPollServer(groupActor)
                        .execute()
                        .getTs();
            } catch (ClientException e) {
                e.printStackTrace();
            }
        }
        if (!messages.isEmpty() && !messages.get(0).isOut()) {

                /*
                messageId - максимально полученный ID, нужен, чтобы не было ошибки 10 internal server error,
                который является ограничением в API VK. В случае, если ts слишком старый (больше суток),
                а max_msg_id не передан, метод может вернуть ошибку 10 (Internal server error).
                 */
            int messageId = messages.get(0).getId();
            if (messageId > maxMsgId){
                maxMsgId = messageId;
            }
            return messages.get(0);
        }
        return null;
    }

    public String getResponse() throws ClientException, ApiException{
        MessagesGetLongPollHistoryQuery eventsQuery = vk.messages()
                .getLongPollHistory(groupActor)
                .ts(ts);
        if (maxMsgId > 0){
            eventsQuery.maxMsgId(maxMsgId);
        }
        String response = eventsQuery.executeAsString();

        if (response.isEmpty()){
            try {
                ts =  vk.messages()
                        .getLongPollServer(groupActor)
                        .execute()
                        .getTs();
            } catch (ClientException e) {
                e.printStackTrace();
            }
        } else {
            return response;
        }
        return null;
    }

    public static int getGroupId() {
        return groupId;
    }

    public static String getAccessToken() {
        return access_token;
    }
}

