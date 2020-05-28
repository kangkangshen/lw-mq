package org.archer.mq.broker;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.archer.mq.Message;
import org.archer.mq.MessageBroker;
import org.archer.mq.config.MqConfig;
import org.archer.mq.constants.ExceptionMsgPatterns;
import org.archer.mq.constants.HttpRpcPatterns;
import org.archer.mq.constants.PropertyKeys;
import org.archer.mq.model.arch.SubscriberMeta;
import org.archer.mq.model.mng.TopicManager;
import org.archer.mq.utils.ConcurrentUtils;
import org.archer.rpc.RpcException;
import org.archer.rpc.utils.HttpUtils;
import org.archer.rpc.utils.Result;
import org.archer.rpc.utils.SerializeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Component
public class TopicMessageBroker implements MessageBroker, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(MessageBroker.class);

    @Autowired
    private TopicManager topicManager;

    @Resource
    private MqConfig mqConfig;

    private ExecutorService executorService;


    @Override
    public void deliverMsg(@Nonnull Message msg) {
        Assert.notNull(msg, "msg must not be null");
        String topic = (String) msg.properties().get(PropertyKeys.MSG_TARGET_TOPIC);
        if (StringUtils.isBlank(topic)) {
            throw new IllegalArgumentException("Message is routed to a topic broker but topic property is blank");
        }
        if (topicManager.exists(topic)) {
            Multimap<String/*consumerTag*/, SubscriberMeta> subscriberMeta = topicManager.subscriberMeta(topic);
            for (String consumerTag : subscriberMeta.keySet()) {
                List<SubscriberMeta> subscribers = Lists.newArrayList(subscriberMeta.get(consumerTag));
//                final CountDownLatch countDownLatch = new CountDownLatch(subscribers.size());
                subscribers.forEach(subscriber -> {
                    executorService.submit(ConcurrentUtils.supportRetry(() -> {
                        msg.properties().put(PropertyKeys.MSG_TARGET_CONSUMER_TAG, subscriber.getConsumerTag());
                        Result<Void> result = HttpUtils.postSync(MessageFormat.format(HttpRpcPatterns.DEFAULT_MSG_RECEIVE_URL_PATTERN, subscriber.getHost(), subscriber.getPort()), SerializeUtils.serialize(msg));
                        if (!result.isSuccess()) {
                            logger.error(result.getExceptionStack());
                            throw new RpcException(result.getExceptionMessage());
                        }
                    }, executorService));
                });
            }
        } else {
            //不存在这个topic,假定在以后较长时间段内这个topic也不会声明，因而直接丢弃这个msg
            logger.debug(MessageFormat.format(ExceptionMsgPatterns.TARGET_TOPIC_NOT_EXIST, msg.id(), topic));
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.executorService = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors()/*corePoolSize*/,
                Runtime.getRuntime().availableProcessors() * 2/*maxPoolSize*/,
                mqConfig.getThreadKeepAlive(),
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(mqConfig.getMaxQueueLength()));
    }
}
