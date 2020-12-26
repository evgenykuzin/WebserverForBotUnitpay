package org.jekajops.vk.buttons;

import org.jekajops.vk.buttons.keyboards.Keyboard;

import java.util.*;

public class KeyboardManager {
    private Keyboard keyboard;
    private Keyboard MAIN_KEYBOARD;
    public KeyboardManager() {
        updateMainKeyboard();
        useMainKeyboard();
    }

    public KeyboardManager(KeyboardManager keyboardManager) {
        MAIN_KEYBOARD = keyboardManager.MAIN_KEYBOARD;
        useMainKeyboard();
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }

    public void setKeyboard(Keyboard keyboard) {
        this.keyboard = keyboard;
    }

    public boolean isKeyboardClassUsed(Class<? extends Keyboard> keyboardClass) {
        return this.keyboard.getClass().equals(keyboardClass);
    }

    public void useMainKeyboard() {
        setKeyboard(MAIN_KEYBOARD);
    }

    public void updateMainKeyboard() {
        MAIN_KEYBOARD = KeyboardLoader.loadKeyboards(this);
    }

    public KeyboardBuilder buildKeyboard() {
        return buildKeyboard(2, false, false);
    }

    public KeyboardBuilder buildKeyboard(int rawSize, boolean oneTime, boolean inline) {
        KeyboardBuilder kb = new KeyboardBuilder(oneTime, inline);
        int line = addMultiButtonLines(kb, keyboard.getButtons(), rawSize);
        if (!oneTime && !inline) {
            kb.addLine();
            kb.addTextButton(keyboard.lastButton(), line + 1);
        }
        return kb;
    }

    public int addSingleButtonLines(KeyboardBuilder kb, Collection<Button> buttons) {
        int line = 0;
        for (var button : buttons) {
            kb.addLine();
            kb.addTextButton(button, line);
            line++;
        }
        return line;
    }

    public int addMultiButtonLines(KeyboardBuilder kb, Collection<Button> buttons, int rawSize) {
        int line = 0;
        int i = 0;
        for (var button : buttons) {
            if (i == rawSize) {
                kb.addLine();
                line++;
                i = 0;
            }
            kb.addTextButton(button, line);
            i++;
        }
        return line;
    }

    public List<Button> getAllMainButtons() {
        return MAIN_KEYBOARD.getButtons();
    }

    public boolean isMainKeyboard(Keyboard keyboard) {
        return keyboard.equals(MAIN_KEYBOARD);
    }

}
