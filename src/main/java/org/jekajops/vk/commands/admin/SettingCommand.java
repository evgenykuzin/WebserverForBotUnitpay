package org.jekajops.vk.commands.admin;

import com.vk.api.sdk.objects.messages.Message;
import org.jekajops.core.database.Database;
import org.jekajops.core.entities.User;
import org.jekajops.vk.VKManager;
import org.jekajops.vk.answer_listeners.*;
import org.jekajops.vk.buttons.KeyboardManager;
import org.jekajops.vk.commands.BackCommand;
import org.jekajops.vk.buttons.keyboards.ChildKeyboard;
import org.jekajops.core.context.Settings;
import org.jekajops.core.context.Context;

import java.sql.SQLException;

public class SettingCommand extends AdminCommand {
    private final KeyboardManager keyboardManager;

    public SettingCommand(String name, KeyboardManager keyboardManager) {
        super(name);
        this.keyboardManager = keyboardManager;
    }

    @Override
    public void adminExecute(Message message) {
        int userId = message.getUserId();
        var vkmanager = new VKManager();
        var settingKey = Context.SETTINGS.keyRusEnMap.get(name);
        var settingValue = Context.SETTINGS.settingsMap.get(settingKey);
        vkmanager.sendMessage("Текущее значение: " + settingValue.toString(), userId, null);
        ChildKeyboard keyboard = new ChildKeyboard(keyboardManager, name);
        BackCommand backCommand = new BackCommand("отмена", keyboardManager);
        keyboard.addCommand(backCommand);
        keyboardManager.setKeyboard(keyboard);
        vkmanager.sendMessage("Напишите в ответ новое значение или нажмите на кнопку" +
                        " 'в главное меню' или 'отмена', чтобы НЕ менять значение"
                , userId, keyboardManager.buildKeyboard());
        AnswerListenerManager.addAnswerListener(
                new SetSettingAnswerListener(userId, settingKey, name, keyboardManager));
    }


}
