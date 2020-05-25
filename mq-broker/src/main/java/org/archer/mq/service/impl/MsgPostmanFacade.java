package org.archer.mq.service.impl;


import org.archer.mq.Message;
import org.archer.mq.MessageBroker;
import org.archer.mq.MessageSender;
import org.archer.mq.constants.DeliverModeEnum;
import org.archer.mq.constants.DeliverWayEnum;
import org.archer.mq.constants.PropertyKeys;
import org.archer.mq.model.mng.MsgManager;
import org.archer.rpc.annotations.ServiceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

@ServiceProvider(value = MessageSender.class, version = "1.0.0")
public class MsgPostmanFacade implements MessageSender {

    @Resource
    private MsgManager msgManager;

    @Autowired
    private ApplicationContext springContext;

    @Override
    @Transactional
    public void unicast(Message msg, String queue) {
        msg.properties().put(PropertyKeys.MSG_TARGET_QUEUE,queue);
        if(Objects.equals(DeliverModeEnum.PERSISTENT,msg.deliverMode())){
            msgManager.saveMsg(msg);
        }
        MessageBroker messageBroker = springContext.getBean(DeliverWayEnum.QUEUE.getMsgBroker());

        messageBroker.deliverMsg(msg);
    }

    @Override
    public void broadcast(Message msg, String topic) {
        msg.properties().put(PropertyKeys.MSG_TARGET_TOPIC,topic);
        if(Objects.equals(DeliverModeEnum.PERSISTENT,msg.deliverMode())){
            msgManager.saveMsg(msg);
        }
        MessageBroker messageBroker = springContext.getBean(DeliverWayEnum.TOPIC.getMsgBroker());
        messageBroker.deliverMsg(msg);
    }
}
