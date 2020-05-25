package org.archer.mq.broker;

import com.google.common.collect.Queues;
import org.archer.mq.Message;
import org.archer.mq.MessageBroker;
import org.archer.mq.config.MqConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Queue;

@Component
public class QueueMessageBroker implements MessageBroker, InitializingBean {

    @Autowired
    private MqConfig mqConfig;

    private Queue<Message> memQueue;


    @Override
    public void deliverMsg(@Nonnull Message msg) {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        memQueue = Queues.newLinkedBlockingQueue(mqConfig.getMaxQueueLength());
    }
}
