package org.jekajops.vk.answer_listeners;

import org.eclipse.jetty.util.BlockingArrayQueue;

import java.util.Queue;

public class AnswerListenerManager {
    public static volatile Queue<AnswerListener> answerListeners = new BlockingArrayQueue<>();

    synchronized public static AnswerListener getAnswerListener(int userId) {
        for (AnswerListener answerListener : answerListeners) {
            if (answerListener == null) continue;
            if (answerListener.getUserId() == userId) {
                answerListeners.remove(answerListener);
                return answerListener;
            }
        }
        return null;
    }

    synchronized public static void removeAnswerListener(int userId) {
        getAnswerListener(userId);
    }

    synchronized public static boolean hasAnswerListeners() {
        return !answerListeners.isEmpty();
    }

    synchronized public static void addAnswerListener(AnswerListener answerListener) {
        answerListeners.add(answerListener);
    }
}
