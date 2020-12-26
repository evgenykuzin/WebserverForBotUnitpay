package org.jekajops.call_api;

import org.jekajops.call_api.call_managers.callbine.CallManagerCallbine;
import org.jekajops.core.database.Database;
import org.jekajops.core.entities.Order;
import org.jekajops.call_api.exceptions.CallException;
import org.jekajops.vk.VKManager;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Queue;

public class Loader implements Runnable {

    @Override
    public void run() {
        load();
    }

    public static void load() {
        Queue<Order> orders;
        try {
            orders = new Database().getOrdersQueueInProcess();

            var callManager = new CallManagerCallbine();
            callManager.checkErrors(orders);
            while (!orders.isEmpty()) {
                Order order = orders.poll();
                File file = null;
                try {
                    file = callManager.record(order);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (CallException ce) {
                    continue;
                }
                if (file != null) {
                    sendFile(file, order);
                } else {
                    System.out.println("file is null");
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    synchronized private static void sendFile(File file, Order order) {
        new VKManager().sendCompletedOrder(order, file);
    }

}
