package org.archer.mq;

public interface MessageSender {

    void unicast(Message msg, String queue);

    void broadcast(Message msg,String topic);
}
