package org.jekajops.payment_service.vk;

import com.vk.api.sdk.actions.Docs;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.docs.Doc;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.PhotoUpload;
import com.vk.api.sdk.objects.photos.responses.WallUploadResponse;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.queries.docs.DocsGetMessagesUploadServerType;
import com.vk.api.sdk.queries.messages.MessagesSendQuery;
import org.jekajops.payment_service.core.database.Database;
import org.jekajops.payment_service.core.entities.Order;
import org.jekajops.payment_service.core.utils.parsers.VkJsonParser;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class VKManager {
    public static VKCore vkCore;
    private static Database database;
    private static final Logger logger = Logger.getGlobal();
    static {
        try {
            vkCore = new VKCore();
            database = new Database();
        } catch (ApiException | ClientException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg, int peerId) {
        if (msg == null) {
            System.out.println("message = null");
            return;
        }
        try {
            MessagesSendQuery msQuery = getSendQuery().peerId(peerId).message(msg);
            msQuery.executeAsRaw();
            System.out.println("бот ответил " + VkJsonParser.getUserRealNameById(peerId, vkCore) + ": " + msg);
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    public void sendDocs(File[] files, int peerId) {
        try {
            List<String> attachments = new ArrayList<>(files.length);
            for (File file : files) {
                attachments.add(getUploadedDocument(file, peerId));
            }
            getSendQuery()
                    .peerId(peerId)
                    .message("settings")
                    .attachment(attachments)
                    .execute();
        } catch (ClientException | ApiException e) {
            e.printStackTrace();
        }
    }

    private String getUploadedDocument(File file, int peerId) throws ClientException, ApiException {
        VkApiClient vk = vkCore.getVk();
        GroupActor groupActor = vkCore.getGroupActor();
        Docs docs = vk.docs();
        String url = docs
                .getMessagesUploadServer(groupActor)
                .type(DocsGetMessagesUploadServerType.DOC)
                .peerId(peerId)
                .execute()
                .getUploadUrl();
        String uploadedFile = vk.upload().doc(url, file).execute().getFile();
        Doc doc = docs.save(groupActor, uploadedFile).execute().get(0);
        return "doc" + doc.getOwnerId() + "_" + doc.getId();
    }


    public void sendCompletedOrder(Order order, File audioFile) {
        try {
            String audioID = getUploadedAudio(audioFile, order.userId());
            getSendQuery()
                    .peerId(order.userId())
                    .message("Запись розыгрыша. Абонент: " + order.phone())
                    .attachment(audioID)
                    .execute();
            new Database().deleteOrder(order.id());
            audioFile.delete();
        } catch (ClientException | ApiException | SQLException e) {
            e.printStackTrace();
        }
    }

    public String getUploadedAudio(File file, int peerId) throws ClientException, ApiException {
        VkApiClient vk = vkCore.getVk();
        GroupActor groupActor = vkCore.getGroupActor();
        Docs docs = vk.docs();
        String url = docs
                .getMessagesUploadServer(groupActor)
                .type(DocsGetMessagesUploadServerType.AUDIO_MESSAGE)
                .peerId(peerId)
                .execute()
                .getUploadUrl();
        String uploadedFile = vk.upload().doc(url, file).execute().getFile();
        Doc doc = docs.save(groupActor, uploadedFile).execute().get(0);
        return "doc" + doc.getOwnerId() + "_" + doc.getId();
    }

    public void sendPhoto(File file, int peerId) {
        try {
            PhotoUpload serverResponse = vkCore.getVk().photos().getMessagesUploadServer(vkCore.getGroupActor()).execute();
            WallUploadResponse uploadResponse;
            List<Photo> photoList;
            uploadResponse = vkCore.getVk().upload().photoWall(serverResponse.getUploadUrl(), file).execute();
            photoList = vkCore.getVk().photos().saveMessagesPhoto(vkCore.getGroupActor(), uploadResponse.getPhoto()).hash(uploadResponse.getHash()).server(uploadResponse.getServer()).execute();
            Photo photo = photoList.get(0);
            String attachId = "photo" + photo.getOwnerId() + "_" + photo.getId();
            vkCore.getVk().messages().send(vkCore.getGroupActor()).peerId(peerId).message(".").attachment(attachId).execute();
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
    }

    public void sendPost(int peerId, String text, List<String> attachments) {
        try {
            vkCore.getVk()
                    .messages()
                    .send(vkCore.getGroupActor())
                    .peerId(peerId)
                    .message(text)
                    .attachment(attachments)
                    .execute();
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
    }

    public MessagesSendQuery getSendQuery() {
        return vkCore.getVk().messages().send(vkCore.getGroupActor());
    }

    /**
     * Обращается к VK API и получает объект, описывающий пользователя.
     *
     * @param id идентификатор пользователя в VK
     * @return {@link UserXtrCounters} информацию о пользователе
     * @see UserXtrCounters
     */
    public static UserXtrCounters getUserInfo(int id) {
        try {
            return vkCore.getVk().users()
                    .get(vkCore.getGroupActor())
                    .userIds(String.valueOf(id))
                    .execute()
                    .get(0);
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isSubscriber(int userId) {
        try {
            return vkCore.getVk()
                    .groups()
                    .getMembers(vkCore.getGroupActor())
                    .groupId(String.valueOf(VKCore.getGroupId()))
                    .execute()
                    .getItems()
                    .contains(userId);
        } catch (ClientException | ApiException e) {
            e.printStackTrace();
        }
        return false;
    }

}
