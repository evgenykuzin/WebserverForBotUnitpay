package org.jekajops.call_api;

import org.jekajops.call_api.call_managers.CallManagerInterface;
import org.jekajops.call_api.call_managers.callbine.CallManagerCallbine;
import org.jekajops.core.database.Database;
import org.jekajops.core.entities.Order;
import org.jekajops.core.entities.Prank;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.jekajops.call_api.exceptions.CallException;
import org.jekajops.call_api.exceptions.TooManyRequestsException;
import org.jekajops.vk.VKManager;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

public class CallServer implements Runnable {

    @Override
    public void run() {
        launch();
    }

    public static void main(String[] args) {
         launch();
    }

    synchronized private static void launch() {
        try {
            var database = new Database();
            var ordersNeedCall = database.getOrdersQueueNeedCall();
            var ordersInProcess = database.getOrdersQueueInProcess();
            var callManager = new CallManagerCallbine();
            var ordersSetsQueue = getQueueOfSetsOfOrders(ordersNeedCall);
            while (!ordersSetsQueue.isEmpty() || !ordersInProcess.isEmpty()) {
                var ordersSet = ordersSetsQueue.poll();
                var orderInProcess = ordersInProcess.poll();
                if (prank(callManager, ordersSet)) System.out.println("prank success");
                if (load(callManager, orderInProcess)) System.out.println("load success");
                //callManager.checkErrors(ordersSet);
                callManager.checkErrors(orderInProcess);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static Queue<Set<Order>> getQueueOfSetsOfOrders(Queue<Order> ordersNeedCall) {
        Queue<Set<Order>> ordersSetsQueue = new BlockingArrayQueue<>();
        Map<Integer, Set<Order>> map = new HashMap<>();
        while (!ordersNeedCall.isEmpty()) {
            Order next = ordersNeedCall.poll();
            if (next == null) continue;
            int prankId = next.prankId();
            if (!map.containsKey(prankId)) map.put(prankId, new HashSet<>());
            var set = map.get(prankId);
            if (set == null) continue;
            set.add(next);
        }
        ordersSetsQueue.addAll(map.values());
        return ordersSetsQueue;
    }

    synchronized protected static boolean prank(CallManagerInterface callManager, Set<Order> ordersSet) throws SQLException, InterruptedException {
        if (ordersSet == null || ordersSet.isEmpty()) {
            return false;
        }
        System.out.println("do orders: " + ordersSet);
        Prank prank = new Database().getPrankByDbId(ordersSet.iterator().next().prankId());
        if (prank != null) {
            try {
                callManager.call(ordersSet, prank.getAudioUrl());
            } catch (TooManyRequestsException tmre) {
                tmre.printStackTrace();
                sleep(1000);
                return false;
            } catch (CallException ce) {
                ce.printStackTrace();
                return false;
            }
            return true;
        } else System.out.println("prank is null");
        return false;
    }

    synchronized protected static boolean load(CallManagerInterface callManager, Order order) {
        File file;
        if (order == null) return false;
        System.out.println("load file for order: " + order);
        try {
            file = callManager.record(order);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (CallException ce) {
            System.out.println(ce.getMessage());
            return false;
        }
        if (file != null) {
            sendFile(file, order);
        }
        return true;
    }

    synchronized private static void sendFile(File file, Order order) {
        new VKManager().sendCompletedOrder(order, file);
    }

}
