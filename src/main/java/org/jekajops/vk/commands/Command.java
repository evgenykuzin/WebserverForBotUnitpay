package org.jekajops.vk.commands;


import com.vk.api.sdk.objects.messages.Message;
import org.jekajops.vk.buttons.KeyboardManager;

public abstract class Command {

    public final String name;
    final KeyboardManager keyboardManager;
    public Command(String name, KeyboardManager keyboardManager){
        this.name = name;
        this.keyboardManager = keyboardManager;
    }

    public abstract void execute(Message message);

    public abstract String description();

    @Override
    public String toString() {
        return String.format("name: %s",this.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Command){
            if (name.equals(((Command) obj).name)){
                return true;
            }
        }
        return false;
    }
}
