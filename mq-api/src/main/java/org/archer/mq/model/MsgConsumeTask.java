package org.archer.mq.model;

import org.archer.mq.Message;
import org.archer.mq.MessageReceiver;

public class MsgConsumeTask implements Runnable {

    private final Message message;

    private final MessageReceiver receiver;

    public MsgConsumeTask(Message message, MessageReceiver receiver) {
        this.message = message;
        this.receiver = receiver;
    }

    @Override
    public void run() {
        receiver.onMsgReceive(message);
    }
}
