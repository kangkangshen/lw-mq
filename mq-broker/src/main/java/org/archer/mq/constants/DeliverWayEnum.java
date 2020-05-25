package org.archer.mq.constants;

import org.archer.mq.MessageBroker;
import org.archer.mq.broker.QueueMessageBroker;
import org.archer.mq.broker.TopicMessageBroker;

import java.util.Objects;

public enum DeliverWayEnum {
    TOPIC(0, TopicMessageBroker.class,"deliver msg to all topic subscriber,and it is the default deliver way"),
    QUEUE(1, QueueMessageBroker.class,"deliver msg to a queue.");


    private final int val;

    private final Class<? extends MessageBroker> msgBroker;

    private final String desc;

    DeliverWayEnum(int val, Class<? extends MessageBroker> msgBroker, String desc) {
        this.val = val;
        this.msgBroker = msgBroker;
        this.desc = desc;
    }

    public int getVal() {
        return val;
    }

    public Class<? extends MessageBroker> getMsgBroker() {
        return msgBroker;
    }

    public String getDesc() {
        return desc;
    }

    public static DeliverWayEnum of(Integer val){
        DeliverWayEnum[] deliverWayEnums = values();
        for(DeliverWayEnum deliverWayEnum:deliverWayEnums){
            if(Objects.equals(deliverWayEnum.val,val)){
                return deliverWayEnum;
            }
        }
        return null;
    }
}
