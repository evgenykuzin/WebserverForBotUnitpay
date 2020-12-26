package org.jekajops.vk.commands.admin;

import com.vk.api.sdk.objects.messages.Message;
import org.jekajops.vk.VKManager;
import org.jekajops.vk.buttons.KeyboardManager;
import org.jekajops.vk.buttons.keyboards.Keyboard;
import org.jekajops.vk.buttons.keyboards.ChildKeyboard;
import org.jekajops.core.context.Context;
import org.jekajops.vk.buttons.keyboards.SettingsKeyboard;

public class GetSettingsCommand extends AdminCommand {
    private final KeyboardManager keyboardManager;
    public GetSettingsCommand(KeyboardManager keyboardManager) {
        super("настройки");
        this.keyboardManager = keyboardManager;

    }

    @Override
    public void adminExecute(Message message) {
        Keyboard keyboard = new SettingsKeyboard(keyboardManager);
        keyboardManager.setKeyboard(keyboard);
        new VKManager().sendMessage(name, message.getUserId(), keyboardManager.buildKeyboard());
    }
}
