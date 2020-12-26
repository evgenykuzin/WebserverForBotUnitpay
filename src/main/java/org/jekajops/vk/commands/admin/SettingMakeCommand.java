package org.jekajops.vk.commands.admin;

import com.vk.api.sdk.objects.messages.Message;
import org.jekajops.core.entities.User;
import org.jekajops.vk.VKManager;
import org.jekajops.vk.answer_listeners.AnswerListener;
import org.jekajops.vk.answer_listeners.AnswerListenerManager;
import org.jekajops.vk.answer_listeners.MakeUserListener;
import org.jekajops.vk.answer_listeners.RemoveUserRoleListener;

public abstract class SettingMakeCommand extends AdminCommand {
    public SettingMakeCommand(String name) {
        super(name);
    }

    @Override
    public void adminExecute(Message message) {
        var answerListener = answerListener(message.getUserId());
        try {
            if (((MakeUserListener)  answerListener).role().equals(User.Role.ADMIN))  {
                if (message.getUserId() != 220208294) {//Кирилл Брусигин
                    new VKManager().sendMessage("У вас нет на это прав.", message.getUserId(), null);
                    return;
                }
            }
        } catch (ClassCastException cce) {
            cce.printStackTrace();
        }
        try {
            if (((RemoveUserRoleListener)  answerListener).role().equals(User.Role.ADMIN))  {
                if (message.getUserId() != 220208294) {//Кирилл Брусигин
                    new VKManager().sendMessage("У вас нет на это прав.", message.getUserId(), null);
                    return;
                }
            }
        } catch (ClassCastException cce) {
            cce.printStackTrace();
        }
        new VKManager().sendMessage("Отправь в ответ id человека которого хочешь добавить", message.getUserId(), null);
        AnswerListenerManager
                .addAnswerListener(answerListener);
    }

    public abstract AnswerListener answerListener(int userId);

}
