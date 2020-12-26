package org.jekajops.vk.commands;

import com.vk.api.sdk.objects.messages.Message;
import org.jekajops.core.utils.payments.PaymentManager;
import org.jekajops.vk.VKManager;
import org.jekajops.vk.buttons.Button;
import org.jekajops.vk.buttons.KeyboardBuilder;
import org.jekajops.vk.buttons.KeyboardManager;
import org.jekajops.vk.buttons.keyboards.ChildKeyboard;
import org.jekajops.vk.buttons.keyboards.Keyboard;
import org.jekajops.core.context.Context;
import org.jekajops.core.database.Database;
import org.jekajops.core.entities.User;

import java.sql.SQLException;

public class PayCommand extends Command {
    public static final String name = "Баланс";

    public PayCommand(KeyboardManager keyboardManager) {
        super(name, keyboardManager);
    }

    @Override
    public void execute(Message message) {
        User user;
        try {
            user = new Database().getUserByUserId(message.getUserId());
            double cost = Context.SETTINGS.PRANK_COST.getDATA();
            new VKManager().sendMessage("Вы можете пополнить свой баланс с помощью VK Pay.\n" +
                            " 1 розыгрыш = " + cost + " руб.\n" +
                            " Ваш баланс: " + user.getBalance() + "руб.\n" +
                            " Вам доступно " + user.getPranksAvailable() + " розыгрышей.",
                    user.getUserId(),
                    new KeyboardBuilder(false, true)
                            .addVkPayButton(0)
                            .addLine()
                            .addLinkButton("Оплатить через Unitpay", PaymentManager.initPay(cost, String.valueOf(user.getUserId())), 1));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public String description() {
        return null;
    }
}
