package org.jekajops.vk.buttons.keyboards;

import org.jekajops.vk.buttons.Button;
import org.jekajops.vk.buttons.KeyboardManager;
import org.jekajops.vk.commands.PayCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainKeyboard implements Keyboard {
    private final KeyboardManager keyboardManager;
    private final List<Button> buttons = new ArrayList<>();

    public MainKeyboard(KeyboardManager keyboardManager) {
        this.keyboardManager = keyboardManager;
    }

    @Override
    public KeyboardManager getKeyboardManager() {
        return keyboardManager;
    }

    @Override
    public Button lastButton() {
        return new Button(new PayCommand(keyboardManager));
    }

    @Override
    public String name() {
        return "main";
    }

    @Override
    public List<Button> getButtons() {
        return buttons;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MainKeyboard that = (MainKeyboard) o;
        return Objects.equals(keyboardManager, that.keyboardManager) &&
                Objects.equals(buttons, that.buttons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyboardManager, buttons);
    }

    @Override
    public String toString() {
        String keyboardManagerKeyboard;
        Keyboard keyboard = keyboardManager.getKeyboard();
        if (keyboardManager.isMainKeyboard(keyboard)) {
            keyboardManagerKeyboard = "main";
        } else {
            keyboardManagerKeyboard = keyboardManager.getKeyboard().toString();
        }
        return "MainKeyboard{" +
                "keyboardManager.keyboard=" + keyboardManagerKeyboard +
                ", buttons=" + buttons +
                '}';
    }
}
