package org.jekajops.vk.commands.admin;

import com.vk.api.sdk.objects.messages.Message;
import org.jekajops.core.entities.User;
import org.jekajops.vk.VKManager;
import org.jekajops.vk.buttons.KeyboardBuilder;
import org.jekajops.vk.buttons.KeyboardManager;
import org.jekajops.vk.buttons.keyboards.ChildKeyboard;
import org.jekajops.vk.buttons.keyboards.Keyboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class GetAdminCommandsCommand extends AdminCommand{

    KeyboardManager keyboardManager;
    Collection<AdminCommand> adminCommands;

    public GetAdminCommandsCommand(Collection<AdminCommand> adminCommands, KeyboardManager keyboardManager) {
        super("admin");
        this.keyboardManager = keyboardManager;
        this.adminCommands = adminCommands;
    }

    @Override
    public void adminExecute(Message message) {
        Keyboard keyboard = new ChildKeyboard(keyboardManager, name);
        for (AdminCommand com : adminCommands) {
            if (!com.name.equals(name)) {
                keyboard.addCommand(com);
            }
        }
        keyboardManager.setKeyboard(keyboard);
        new VKManager().sendMessage(name, message.getUserId(), keyboardManager.buildKeyboard());
    }

}
