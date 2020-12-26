package org.jekajops.vk.answer_listeners;

import com.vk.api.sdk.objects.messages.Message;

public interface AnswerListener {
    void onAnswer(Message answer);
    int getUserId();
}
