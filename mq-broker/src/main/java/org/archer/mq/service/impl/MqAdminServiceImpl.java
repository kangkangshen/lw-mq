package org.archer.mq.service.impl;


import com.google.common.collect.Multimap;
import org.archer.mq.MqAdminService;
import org.archer.mq.model.arch.SubscriberMeta;
import org.archer.mq.model.mng.TopicManager;
import org.archer.rpc.annotations.ServiceProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.util.List;

@ServiceProvider(value = MqAdminService.class, version = "1.0.0")
public class MqAdminServiceImpl implements MqAdminService {

    @Autowired
    private TopicManager topicManager;

    @Override
    public void declareTopic(String topic, String desc) {
        topicManager.declareTopic(topic, desc);
    }

    @Override
    public void deleteTopic(String topic) {

    }

    @Override
    public void declareQueue(String queue, String desc) {

    }

    @Override
    public void deleteQueue(String queue) {

    }

    @Override
    public void listenTopic(String topic, SubscriberMeta meta) {
        topicManager.subscribe(topic, meta);
    }

    @Override
    public void batchListenTopic(Multimap<String, SubscriberMeta> batchMeta) {
        topicManager.batchSubscribe(batchMeta);
    }


    @Override
    public void listenQueue(String queue, SubscriberMeta meta) {

    }

    @Override
    public void batchListenQueue(Multimap<String, SubscriberMeta> batchMeta) {

    }


    @Override
    public int totalMsgNum(String topic) {
        return 0;
    }

    @Override
    public int msgNum(String topic, InetAddress node) {
        return 0;
    }

    @Override
    public List<String> listAllTopic() {
        return null;
    }

    @Override
    public List<String> listAllQueue() {
        return null;
    }
}
