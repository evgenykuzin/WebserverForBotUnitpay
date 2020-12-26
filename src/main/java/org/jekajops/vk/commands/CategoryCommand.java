package org.jekajops.vk.commands;

import com.vk.api.sdk.objects.messages.Message;
import org.jekajops.vk.VKManager;
import org.jekajops.vk.buttons.Button;
import org.jekajops.vk.buttons.keyboards.Keyboard;
import org.jekajops.vk.buttons.KeyboardManager;

public class CategoryCommand extends Command {
    private final Keyboard childrenKeyboard;
    private KeyboardManager keyboardManager;
    public CategoryCommand(String name, Keyboard childrenKeyboard, KeyboardManager keyboardManager) {
        super(name, keyboardManager);
        this.keyboardManager = keyboardManager;
        this.childrenKeyboard = childrenKeyboard;
    }

    @Override
    public void execute(Message message) {
        keyboardManager.setKeyboard(childrenKeyboard);
        new VKManager().sendMessage( "открыта категория '"+ name +"'", message.getUserId(), keyboardManager.buildKeyboard());
    }

    @Override
    public String description() {
        return null;
    }

    public boolean addChild(Command command) {
        return childrenKeyboard.getButtons().add(new Button(command));
    }

    public Keyboard getChildrenKeyboard() {
        return childrenKeyboard;
    }

    public KeyboardManager getKeyboardManager(){
        return keyboardManager;
    }
}
