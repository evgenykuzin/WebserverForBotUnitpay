package org.jekajops.vk.commands.admin;

import com.vk.api.sdk.objects.messages.Message;
import org.jekajops.core.entities.User;
import org.jekajops.vk.commands.Command;

public abstract class AdminCommand extends Command {
    public AdminCommand(String name) {
        super(name, null);
    }

    @Override
    public void execute(Message message) {
            adminExecute(message);
    }

    public abstract void adminExecute(Message message);

    @Override
    public String description() {
        return null;
    }
}
