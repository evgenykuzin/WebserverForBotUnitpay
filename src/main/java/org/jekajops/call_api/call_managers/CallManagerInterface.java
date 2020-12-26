package org.jekajops.call_api.call_managers;

import org.jekajops.core.context.Context;
import org.jekajops.core.database.Database;
import org.jekajops.core.entities.Order;
import org.jekajops.call_api.exceptions.CallException;
import org.jekajops.vk.VKManager;

import javax.annotation.Nullable;
import java.io.*;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public interface CallManagerInterface {

    void call(Set<Order> orders, String audioUrl) throws CallException;

    File record(Order order) throws CallException, IOException;

     void checkErrors(@Nullable Order order) throws CallException;

    String getApiUrl();

    String getApiToken();

    default String constructUrl(String command) {
        return getApiUrl() + command;
    }

    default void turnMoneyBack(Order order) {
        Database database;
        try {
            database = new Database();
            int userId = order.userId();
            var user = database.getUserByUserId(order.userId());
            user.updatePayment(Context.SETTINGS.PRANK_COST.getDATA());
            new VKManager().sendMessage(
                    "К сожалению произошла ошибка во время инициализации розыгрыша №"
                            + order.prankId() +
                            ", по номеру телефона: " +
                            order.phone() +
                            ". Вам возвращается стоимость розыгрыша на ваш баланс!"
                    , userId, null);
            database.deleteOrder(order.id());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
