package org.archer.mq.model.arch;


import lombok.Data;
import org.archer.mq.MessageReceiver;

import java.io.Serializable;
import java.util.Date;

@Data
public class SubscriberMeta implements Serializable {
    private String entity;
    private String consumerTag;
    private String host;
    private String port;
    private String receiverInterface = MessageReceiver.class.getName();
    private String receiverMethod = "onMsgReceive";
    private String protocol;
    private Date gmtCreate;
    private String desc;

}
