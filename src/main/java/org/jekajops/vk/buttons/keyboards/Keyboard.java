package org.jekajops.vk.buttons.keyboards;

import org.jekajops.vk.buttons.Button;
import org.jekajops.vk.buttons.KeyboardManager;
import org.jekajops.vk.commands.CategoryCommand;
import org.jekajops.vk.commands.SendCommand;
import org.jekajops.vk.commands.Command;
import org.jekajops.vk.commands.admin.AdminCommand;
import org.jekajops.core.database.Database;

import java.sql.SQLException;
import java.util.*;

public interface Keyboard {
    String name();
    List<Button> getButtons();

    default Keyboard set(Keyboard keyboard) {
        getButtons().clear();
        getButtons().addAll(keyboard.getButtons());
        lastButton().set(keyboard.lastButton());
        return this;
    }

    default void addCategoryCommand(Keyboard childrenKeyboard) {
        CategoryCommand command = new CategoryCommand(childrenKeyboard.name(), childrenKeyboard, getKeyboardManager());
        addCommand(command);
    }

    default void addSendCommand(String name, String category, String subcategory) {
        SendCommand command;
        try {
            command = new SendCommand(
                    name,
                    new Database().getPranks(category, subcategory),
                    getKeyboardManager()
            );
            addCommand(command);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    default void addCommand(Command command) {
        getButtons().add(new Button(command));
    }

    KeyboardManager getKeyboardManager();

    Button lastButton();

}