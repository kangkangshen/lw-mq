package org.archer.mq.model.mng;

import com.google.common.collect.Multimap;
import org.archer.mq.model.arch.SubscriberMeta;

public interface TopicManager {


    void declareTopic(String topic, String desc);

    void destroyTopic(String topic);

    boolean exists(String topic);

    Multimap<String/*consumerTag*/, SubscriberMeta> subscriberMeta(String topic);

    void subscribe(String topic, SubscriberMeta subscriberMeta);

    void batchSubscribe(Multimap<String/*topic*/, SubscriberMeta> metas);


}
