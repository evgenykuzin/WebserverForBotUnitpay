package org.jekajops.vk.buttons.keyboards;

import org.jekajops.vk.buttons.Button;
import org.jekajops.vk.buttons.KeyboardManager;
import org.jekajops.vk.commands.BackCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChildKeyboard implements Keyboard {
    private final KeyboardManager keyboardManager;
    private final List<Button> buttons = new ArrayList<>();
    private final String name;
    public ChildKeyboard(KeyboardManager keyboardManager, String name) {
        this.keyboardManager = keyboardManager;
        this.name = name;
    }

    @Override
    public KeyboardManager getKeyboardManager() {
        return keyboardManager;
    }

    @Override
    public Button lastButton() {
        return new Button(new BackCommand(keyboardManager));
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public List<Button> getButtons() {
        return buttons;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChildKeyboard that = (ChildKeyboard) o;
        return Objects.equals(keyboardManager, that.keyboardManager) &&
                (buttons.containsAll(that.buttons) || that.buttons.containsAll(buttons));
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyboardManager, buttons);
    }

    @Override
    public String toString() {
        String keyboardManagerKeyboard;
        if (keyboardManager.getKeyboard().equals(this)) {
            keyboardManagerKeyboard = "this";
        } else {
            keyboardManagerKeyboard = keyboardManager.getKeyboard().toString();
        }
        return "ChildKeyboard{" +
                "keyboardManager.keyboard=" + keyboardManagerKeyboard +
                ", buttons=" + buttons +
                '}';
    }
}
