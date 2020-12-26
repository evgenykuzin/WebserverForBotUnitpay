package org.jekajops.vk.commands;

import com.vk.api.sdk.objects.messages.Message;
import org.jekajops.core.database.Database;
import org.jekajops.core.entities.User;
import org.jekajops.vk.buttons.KeyboardManager;
import org.jekajops.vk.buttons.keyboards.ChildKeyboard;
import org.jekajops.core.entities.Prank;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.jekajops.vk.VKManager;

import java.sql.SQLException;
import java.util.*;

import static org.jekajops.core.context.Context.SETTINGS;

public class SendCommand extends Command {
    private final List<Prank> pranks;
    private final Queue<Prank> prankQueue = new BlockingArrayQueue<>();
    private int range;
    private final boolean isTop;
    public SendCommand(String name, List<Prank> pranks, KeyboardManager keyboardManager) {
        super(name, keyboardManager);
        this.pranks = pranks;
        range = pranks.size();
        isTop = name.contains("Топ");
        if (isTop) {
            range = Math.min(Integer.parseInt(name.replace("Топ ", "")), pranks.size());
            pranks.sort(Collections.reverseOrder());
        }
    }

    @Override
    public void execute(Message message) {
        VKManager vkManager = new VKManager();
        prankQueue.clear();
        for (int i = 0; i < range; i++) {
            prankQueue.add(pranks.get(i));
        }
        int counter = 0;
        boolean stop = false;
        User user = null;
        try {
            user = new Database().getUserByUserId(message.getUserId());
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        boolean isAdmin = user != null && user.isAdmin();
        do {
            counter++;
            Prank prank = prankQueue.poll();
            if (prank == null) continue;
            vkManager.sendPrank(prank, message.getUserId(), isAdmin);
            if (!isTop) stop = counter >= SETTINGS.MAX_PRANKS_TO_SEND.getDATA();
        } while (!prankQueue.isEmpty() && !stop);
        if (pranks.size() > SETTINGS.MAX_PRANKS_TO_SEND.getDATA()) {
            keyboardManager.setKeyboard(new NextPranksKeyboard());
            vkManager.sendMessage("Есть еще " + prankQueue.size() +
                    " розыгрышей", message.getUserId(),
                    keyboardManager.buildKeyboard(1, false, false));
        } else if (keyboardManager.isKeyboardClassUsed(NextPranksKeyboard.class)){
            keyboardManager.useMainKeyboard();
            vkManager.sendMessage("больше нет роыгрышей в этой категории", message.getUserId(), keyboardManager.buildKeyboard());
        }
    }

    @Override
    public String description() {
        return null;
    }

    private class NextPranksKeyboard extends ChildKeyboard {
        public NextPranksKeyboard() {
            super(keyboardManager, name);
            List<Prank> nextPranks = new ArrayList<>(prankQueue);
            addCommand(new SendCommand("Следующие", nextPranks, keyboardManager));
            addCommand(new PayCommand(keyboardManager));
        }
    }
}
