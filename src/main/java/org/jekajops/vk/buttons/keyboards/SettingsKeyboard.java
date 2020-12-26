package org.jekajops.vk.buttons.keyboards;

import org.jekajops.core.context.Context;
import org.jekajops.core.database.Database;
import org.jekajops.core.entities.User;
import org.jekajops.vk.answer_listeners.AnswerListener;
import org.jekajops.vk.answer_listeners.MakeUserListener;
import org.jekajops.vk.answer_listeners.RemoveUserRoleListener;
import org.jekajops.vk.buttons.KeyboardManager;
import org.jekajops.vk.commands.InfoCommand;
import org.jekajops.vk.commands.admin.SettingCommand;
import org.jekajops.vk.commands.admin.SettingMakeCommand;

import java.sql.SQLException;
import java.util.stream.Collectors;

public class SettingsKeyboard extends ChildKeyboard{
    public SettingsKeyboard(KeyboardManager keyboardManager) {
        this(keyboardManager, "настройки");
    }

    public SettingsKeyboard(KeyboardManager keyboardManager, String name) {
        super(keyboardManager, name);
        for (String key : Context.SETTINGS.keyRusEnMap.keySet()) {
            addCommand(new SettingCommand(key, keyboardManager));
        }
        addCommand(new InfoCommand("список админов", getUserListMessage(User.Role.ADMIN), null));
        addCommand(new InfoCommand("список избранных", getUserListMessage(User.Role.PRIVILEGE), null));
        addCommand(new SettingMakeCommand("добавить админа") {
            @Override
            public AnswerListener answerListener(int userId) {
                return new MakeUserListener(userId, User.Role.ADMIN);
            }
        });
        addCommand(new SettingMakeCommand("удалить админа") {
            @Override
            public AnswerListener answerListener(int userId) {
                return new RemoveUserRoleListener(userId, User.Role.ADMIN);
            }
        });
        addCommand(new SettingMakeCommand("добавить избранного") {
            @Override
            public AnswerListener answerListener(int userId) {
                return new MakeUserListener(userId, User.Role.PRIVILEGE);
            }
        });
        addCommand(new SettingMakeCommand("удалить избранного") {
            @Override
            public AnswerListener answerListener(int userId) {
                return new RemoveUserRoleListener(userId, User.Role.PRIVILEGE);
            }
        });
    }

    String getUserListMessage(User.Role role) {
        try {
            var users = new Database().getUsers();
            users = users.stream().filter(user -> user.hasRole(role)).collect(Collectors.toList());
            StringBuilder sb = new StringBuilder();
            sb.append(role.name()).append(":").append("\n");
            users.forEach(user -> sb
                    .append("Имя: ")
                    .append(user.getUserName())
                    .append(" |   Vk id: ")
                    .append(user.getUserId())
                    .append("\n"));
            return sb.toString();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "пусто";
    }
}
