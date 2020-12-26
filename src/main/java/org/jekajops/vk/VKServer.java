package org.jekajops.vk;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.Message;
import org.jekajops.vk.buttons.KeyboardManager;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VKServer implements Runnable{
    public static VKCore vkCore;
    public static HashMap<Integer, KeyboardManager> keyboardManagersMap;

    static {
        try {
            resetVk(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        keyboardManagersMap = new HashMap<>();
    }

    public void start() throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();
        try {
            if (vkCore == null) {
                Logger.getGlobal().log(Level.WARNING, "can not get vkCore. maybe some group or vk error");
                waitVk(0);
                if (vkCore == null) return;
            }
            Message message = vkCore.getMessage();
            if (message != null) {
                KeyboardManager keyboardManager = keyboardManagersMap.get(message.getUserId());
                if (keyboardManager == null) {
                    keyboardManager = new KeyboardManager();
                    keyboardManagersMap.put(message.getUserId(), keyboardManager);
                }
                Messenger messenger = new Messenger(message, keyboardManager);
                exec.execute(messenger);
                if (messenger.isNeedToUpdateKeyboardManager()) {
                    keyboardManager.updateMainKeyboard();
                    messenger.setNeedToUpdateKeyboardManager(false);
                }
            }
        } catch (ClientException | ApiException | OutOfMemoryError e) {
            e.printStackTrace();
            waitVk(0);
        }
    }

    private static void waitVk(int errorCounter) throws InterruptedException {
        if (errorCounter > 100) return;
        System.out.println("Возникли проблемы");
        final int RECONNECT_TIME = 1000;
        System.out.println("Повторное соединение через " + RECONNECT_TIME / 1000 + " секунд");
        Thread.sleep(RECONNECT_TIME);
        resetVk(errorCounter);
    }

    public static void resetVk(int errorCounter) throws InterruptedException {
        try {
            vkCore = new VKCore();
        } catch (ClientException | ApiException e) {
            e.printStackTrace();
            waitVk(++errorCounter);
        }
    }

    @Override
    public void run() {
        try {
            start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}