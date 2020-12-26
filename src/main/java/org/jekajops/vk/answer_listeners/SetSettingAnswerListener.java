package org.jekajops.vk.answer_listeners;

import com.vk.api.sdk.objects.messages.Message;
import org.jekajops.core.context.Context;
import org.jekajops.core.database.Database;
import org.jekajops.vk.VKManager;
import org.jekajops.vk.buttons.KeyboardManager;
import org.jekajops.vk.buttons.keyboards.SettingsKeyboard;
import org.jekajops.vk.commands.BackCommand;

import java.sql.SQLException;

public class SetSettingAnswerListener implements AnswerListener{
    int userId;
    String settingKey;
    String msgKey;
    KeyboardManager keyboardManager;
    public SetSettingAnswerListener(int userId, String settingKey, String msgKey, KeyboardManager keyboardManager) {
        this.userId = userId;
        this.settingKey = settingKey;
        this.msgKey = msgKey;
        this.keyboardManager = keyboardManager;
    }

    @Override
    public void onAnswer(Message answer) {
        VKManager vkmanager = new VKManager();
        String answerText = answer.getBody();
        var keyRusEnMap = Context.SETTINGS.keyRusEnMap;
        if (answerText.equals(new BackCommand(null).name)
                || keyRusEnMap.containsKey(answerText)
                || keyRusEnMap.containsValue(answerText)
                || settingKey.equals(answerText)) {
            vkmanager.sendMessage("Ошибка. Некорректное значение: " + answerText, userId, null);
            return;
        }
        try {
            Database database = new Database();
            if (settingKey.equals("text_privetstviya")
                    || settingKey.equals("dont_understand"))
                database.updateBlobSetting(settingKey, answerText);
            else database.updateSetting(settingKey, answerText);
            Context.SETTINGS.update();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        keyboardManager.setKeyboard(new SettingsKeyboard(keyboardManager));
        vkmanager.sendMessage("Значение (" + msgKey + ") изменилось на: " + answerText, userId, keyboardManager.buildKeyboard());
    }

    @Override
    public int getUserId() {
        return userId;
    }

}
