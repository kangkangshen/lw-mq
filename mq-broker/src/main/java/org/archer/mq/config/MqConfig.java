package org.archer.mq.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class MqConfig {


    @Value("${mq.max.queue.length:1024}")
    private int maxQueueLength;

    @Value("${mq.thread.keep.alive:3}")
    private int threadKeepAlive;

}
