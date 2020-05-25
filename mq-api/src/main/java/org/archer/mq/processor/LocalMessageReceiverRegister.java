package org.archer.mq.processor;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.archer.mq.MessageReceiver;
import org.archer.mq.MessageReceiverRegistrar;
import org.archer.mq.MqAdminService;
import org.archer.mq.constants.PropertyKeys;
import org.archer.mq.model.arch.SubscriberMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.net.InetAddress;
import java.util.Date;
import java.util.Map;

@Component
public class LocalMessageReceiverRegister implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(MessageReceiverRegistrar.class);

    private final Environment environment;
    private final MqAdminService mqAdminService;

    public LocalMessageReceiverRegister(Environment environment, MqAdminService mqAdminService) {
        this.environment = environment;
        this.mqAdminService = mqAdminService;
    }

    @SneakyThrows
    private SubscriberMeta buildSubscriberMeta(String consumerTag, String desc, String interestedEntity) {
        SubscriberMeta meta = new SubscriberMeta();
        meta.setConsumerTag(consumerTag);
        meta.setGmtCreate(new Date());
        meta.setDesc(desc);
        meta.setEntity(interestedEntity);
        meta.setHost(InetAddress.getLocalHost().getHostAddress());
        meta.setPort(environment.getProperty(PropertyKeys.SERVICE_PROVIDER_PORT));
        return meta;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ApplicationContext springContext = event.getApplicationContext();
        Map<String, MessageReceiver> receiverMap = springContext.getBeansOfType(MessageReceiver.class);
        if (!CollectionUtils.isEmpty(receiverMap)) {
            Multimap<String, SubscriberMeta> topicMeta = ArrayListMultimap.create();
            Multimap<String, SubscriberMeta> queueMeta = ArrayListMultimap.create();
            receiverMap.forEach((beanName, messageReceiver) -> {
                String interestedTopic = messageReceiver.interestTopic();
                String interestedQueue = messageReceiver.interestQueue();
                Assert.isTrue(!(StringUtils.isBlank(interestedQueue) && StringUtils.isBlank(interestedTopic)), "You must choose one kind of message caster to listen,but now all caster is blank.The illegal bean is " + beanName);
                Assert.isTrue(!(StringUtils.isNotBlank(interestedQueue) && StringUtils.isNotBlank(interestedTopic)), "We dont recommend listen more than one kind of caster,you can create two " + MessageReceiver.class.getName() + " instance to resolved it." + "The illegal bean is " + beanName);
                Assert.isTrue(StringUtils.isNotBlank(messageReceiver.consumerTag()), "You must specify the consumer tag,now bean " + beanName + "'s consumer tag is blank");
                Assert.isTrue(StringUtils.isNotBlank(messageReceiver.desc()), "You must specify the consumer desc,now bean " + beanName + "'s consumer desc is blank");

                if (StringUtils.isNotBlank(interestedTopic)) {
                    topicMeta.put(interestedTopic, buildSubscriberMeta(messageReceiver.consumerTag(), messageReceiver.desc(), messageReceiver.interestTopic()));
                } else {
                    queueMeta.put(interestedQueue, buildSubscriberMeta(messageReceiver.consumerTag(), messageReceiver.desc(), messageReceiver.interestQueue()));
                }
            });
            if (!topicMeta.isEmpty()) {
                mqAdminService.batchListenTopic(topicMeta);
            }
            if (!queueMeta.isEmpty()) {
                mqAdminService.batchListenQueue(queueMeta);
            }
        }
    }
}
