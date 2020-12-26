package org.jekajops.vk.commands;

import com.vk.api.sdk.objects.messages.Message;
import org.jekajops.vk.VKManager;
import org.jekajops.vk.buttons.KeyboardManager;

public class BackCommand extends Command{
    public BackCommand(String name, KeyboardManager keyboardManager) {
        super(name, keyboardManager);
    }

    public BackCommand(KeyboardManager keyboardManager) {
        super("В главное меню", keyboardManager);
    }

    @Override
    public void execute(Message message) {
        keyboardManager.useMainKeyboard();
        new VKManager().sendMessage("Вы в главном меню", message.getUserId(), keyboardManager.buildKeyboard());
    }

    @Override
    public String description() {
        return null;
    }
}
