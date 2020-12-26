package org.jekajops.vk.buttons;

import org.jekajops.core.database.Database;
import org.jekajops.core.entities.Categories;
import org.jekajops.vk.buttons.keyboards.Keyboard;
import org.jekajops.vk.buttons.keyboards.MainKeyboard;
import org.jekajops.vk.buttons.keyboards.ChildKeyboard;

import java.sql.SQLException;

public class KeyboardLoader {
    public static Keyboard loadKeyboards(KeyboardManager keyboardManager) {
        Keyboard mainKeyboard = new MainKeyboard(keyboardManager);
        Categories categories = getCategories();
        categories.forEach((category, subcategories) -> {
            if (!subcategories.isEmpty()) {
                Keyboard childKeyboard = new ChildKeyboard(keyboardManager, category);
                for (String child : subcategories) {
                    childKeyboard.addSendCommand(child, category, child);
                }
                mainKeyboard.addCategoryCommand(childKeyboard);
            } else if (category.contains("Все розыгрыши")
                    || category.contains("Топ")) {
                mainKeyboard.addSendCommand(category, null, null);
            } else {
                mainKeyboard.addSendCommand(category, category, null);
            }
        });
        return mainKeyboard;
    }

    private static Categories getCategories() {
        try {
            return new Database().getCategories();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return new Categories();
    }

}
