package org.jekajops.vk.commands.admin;

import com.vk.api.sdk.objects.messages.Message;
import org.jekajops.core.entities.User;
import org.jekajops.vk.VKManager;

public class HelpAdminCommand extends AdminCommand {
    private static final String HELP_TEXT = "я не могу помочь";

    public HelpAdminCommand() {
        super("помощь");
    }

    @Override
    public void adminExecute(Message message) {
        new VKManager().sendMessage(HELP_TEXT, message.getUserId(), null);
    }
}
