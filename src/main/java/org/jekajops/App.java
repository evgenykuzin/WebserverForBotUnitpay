package org.jekajops;

import com.jcabi.log.VerboseRunnable;
import org.jekajops.core.context.Context;
import org.jekajops.call_api.CallServer;
import org.jekajops.vk.VKServer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App {
    public static void main(String[] args) {
        launchBot(0);
    }

    private static void launchBot(int errorCounter){
        try {
            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);
            //Runnable loader = new Loader();
            System.out.println(Context.SETTINGS.toString());
            //System.out.println("Running loader...");
            //scheduledExecutorService.scheduleAtFixedRate(loader, 1, 1, TimeUnit.SECONDS);
            System.out.println("Running CallServer...");
            scheduledExecutorService.scheduleAtFixedRate(new VerboseRunnable(new CallServer(), true), 1, 2, TimeUnit.SECONDS);
            System.out.println("Running VKServer...");
            startVkServer(0, scheduledExecutorService);
        } catch (Throwable e) {
            e.printStackTrace();
            if (errorCounter < 10) {
                launchBot(++errorCounter);
            }
        }
    }

    private static void startVkServer(int errorCounter, ScheduledExecutorService executorService) {
        try {
            executorService.scheduleAtFixedRate(new VerboseRunnable(new VKServer(), true), 1, 1, TimeUnit.MILLISECONDS);
        } catch (Throwable e) {
            if (errorCounter < 100) {
                startVkServer(++errorCounter, executorService);
            }
        }
    }
}
