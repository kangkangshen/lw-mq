package org.archer.mq.config;


import lombok.Data;
import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.RetryForever;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.retry.RetryOneTime;
import org.archer.mq.constants.RetryPolicyEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@Data
public class ZkConfig {

    @Value("${zk.server.path:localhost:2181}")
    private String zkServerPath;

    @Value("${zk.retry.policy:RetryNTimes}")
    private String retryPolicy;

    @Value("${zk.max.retry:10}")
    private int maxRetryTimes;

    @Value("${zk.retry.interval:3000}")
    private int retryInterval;

    @Value("${zk.session.timeout:15000}")
    private int sessionTimeout;

    /**
     * 根据传入的参数解析重试策略
     *
     * @return 重试策略
     * @throws UnsupportedOperationException
     */

}

