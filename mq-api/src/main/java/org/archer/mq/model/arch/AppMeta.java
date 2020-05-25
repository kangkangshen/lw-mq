package org.archer.mq.model.arch;


import com.google.common.collect.Multimap;
import lombok.Data;

@Data
public class AppMeta {

    private String appName;
    private Multimap<String/*consumerTag*/, SubscriberMeta> consumers;
}
