package org.jekajops.vk.answer_listeners;

import com.vk.api.sdk.objects.messages.Message;
import org.jekajops.core.database.Database;
import org.jekajops.core.entities.User;
import org.jekajops.vk.VKManager;
import org.jekajops.core.context.Context;

import java.sql.SQLException;

public class PhoneAnswerListener implements AnswerListener {
    User user;
    int prankId;

    public PhoneAnswerListener(User user, int prankId) {
        this.user = user;
        this.prankId = prankId;
    }

    @Override
    public void onAnswer(Message answer) {
        String phone = answer.getBody();
        if (!phone.matches("^\\+?([0-9])?\\s?\\(?[0-9]{3}\\)?\\s?[0-9]{3}-?[0-9]{2}-?[0-9]{2}$")) {
            sendMessage("Неправильный формат номера телефона! Попробуйте заказать еще раз и ввести другой номер.", answer);
            return;
        }
        phone = phone.replaceAll("[()\\-\\s]", "");
        System.out.println("phone: " + phone);
        boolean buySuccess;
        if (user.hasRole(User.Role.PRIVILEGE)) {
            buySuccess = user.buyPrivilegePrank();
        } else {
            buySuccess = user.buyPrank();
        }
        Database database;
        try {
            database = new Database();
            if (buySuccess) {
                database.insertOrder(
                        prankId,
                        user.getUserId(),
                        phone.replaceAll("\\D", "")
                );
                database.updatePrankRating(prankId);
                sendMessage("Заказ принят! Вскоре вам будет отправлена запись розыгрыша.", answer);
            } else sendLowBalanceError(answer);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public int getUserId() {
        return user.getUserId();
    }


    private void sendMessage(String s, Message m) {
        new VKManager().sendMessage(s, m.getUserId(), null);
    }

    private void sendLowBalanceError(Message m) {
        sendMessage("Пожалуйста, пополните баланс, чтобы заказать розыгрыш", m);
    }

}
