package org.jekajops.vk.answer_listeners;

import com.vk.api.sdk.objects.messages.Message;
import org.jekajops.core.database.Database;
import org.jekajops.core.entities.User;
import org.jekajops.vk.VKManager;

import java.sql.SQLException;

public record MakeUserListener(int userId, User.Role role) implements AnswerListener {

    @Override
    public void onAnswer(Message answer) {
        var vkmanager = new VKManager();
        try {
            new Database().addUserRole(userId, role);
            vkmanager.sendMessage("польователь (" + userId + ")" +
                            " добавлен к категории (" + role.name() + ")",
                    answer.getUserId(), null);
        } catch (SQLException throwable) {
            vkmanager.sendMessage("Ошибка! Не удалось добавить польователя (" + userId + ")" +
                    " к категории (" + role.name() + ")", answer.getUserId(), null);
            throwable.printStackTrace();
        }
    }

    @Override
    public int getUserId() {
        return userId;
    }

}
