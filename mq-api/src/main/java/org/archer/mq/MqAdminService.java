package org.archer.mq;

import com.google.common.collect.Multimap;
import org.archer.mq.model.arch.SubscriberMeta;

import java.net.InetAddress;
import java.util.List;

/**
 * lw-rpc管理控制台服务
 */

public interface MqAdminService {


    void declareTopic(String topic, String desc);

    void deleteTopic(String topic);

    void declareQueue(String queue, String desc);

    void deleteQueue(String queue);

    void listenTopic(String topic, SubscriberMeta meta);

    void batchListenTopic(Multimap<String, SubscriberMeta> batchMeta);

    void listenQueue(String queue, SubscriberMeta meta);

    void batchListenQueue(Multimap<String, SubscriberMeta> batchMeta);

    int totalMsgNum(String topic);

    int msgNum(String topic, InetAddress node);

    List<String> listAllTopic();

    List<String> listAllQueue();


}
