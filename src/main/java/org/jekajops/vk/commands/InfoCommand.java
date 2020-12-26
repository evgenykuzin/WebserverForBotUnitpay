package org.jekajops.vk.commands;

import com.vk.api.sdk.objects.messages.Message;
import org.jekajops.vk.VKManager;
import org.jekajops.vk.buttons.KeyboardManager;

public class InfoCommand extends Command{
    private final String text;
    public InfoCommand(String name, String text, KeyboardManager keyboardManager) {
        super(name, keyboardManager);
        this.text = text;
    }

    @Override
    public void execute(Message message) {
        var kb = keyboardManager != null ?
                keyboardManager.buildKeyboard() : null;
        new VKManager().sendMessage(
                text,
                message.getUserId(),
                kb
        );
    }

    @Override
    public String description() {
        return null;
    }
}
