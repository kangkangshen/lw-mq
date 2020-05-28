package org.archer.mq.service.impl;

import org.archer.mq.Message;
import org.archer.mq.MessageSender;


/**
 * 通用消息属性处理
 */
public class BaseMessageSender implements MessageSender {


    @Override
    public void unicast(Message msg, String queue) {

    }

    @Override
    public void broadcast(Message msg, String topic) {

    }
}
