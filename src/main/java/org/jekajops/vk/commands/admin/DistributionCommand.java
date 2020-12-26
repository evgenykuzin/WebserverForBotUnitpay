package org.jekajops.vk.commands.admin;

import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.MessageAttachment;
import org.jekajops.core.database.Database;
import org.jekajops.core.entities.User;
import org.jekajops.vk.VKManager;
import org.jekajops.vk.answer_listeners.AnswerListener;
import org.jekajops.vk.answer_listeners.AnswerListenerManager;

import java.sql.SQLException;
import java.util.ArrayList;

public class DistributionCommand extends AdminCommand{
    public DistributionCommand() {
        super("рассылка");
    }

    @Override
    public void adminExecute(Message message) {
        new VKManager().sendMessage("Напишите текст рассылки и загрузите медиафайлы, если требуется." +
                " Важно чтобы все было в одном сообщении.",message.getUserId(), null);
        AnswerListenerManager.addAnswerListener(new AnswerListener() {
            @Override
            public void onAnswer(Message answer) {
                System.out.println("answer = " + answer);
                try {
                    var users = new Database().getUsers();
                    for (User user : users) {
                        var messageAttachments = answer.getAttachments();
                        var attachments = new ArrayList<String>();
                        if (messageAttachments != null) {
                            for (MessageAttachment attachment : messageAttachments) {
                                var type = attachment.getType().getValue();
                                var audio = attachment.getAudio();
                                var photo = attachment.getPhoto();
                                var doc = attachment.getDoc();
                                if (audio != null) attachments.add("audio"+audio.getOwnerId()+"_"+audio.getId());
                                if (photo != null) attachments.add("photo"+photo.getOwnerId()+"_"+photo.getId());
                                if (doc != null) attachments.add("doc"+doc.getOwnerId()+"_"+doc.getId());
                                System.out.println(type);
                            }
                        }
                        new VKManager().sendPost(user.getUserId(), answer.getBody(), attachments);
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

            @Override
            public int getUserId() {
                return message.getUserId();
            }
        });
    }
}
