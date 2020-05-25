package org.archer.mq.processor;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.archer.mq.Message;
import org.archer.mq.MessageReceiver;
import org.archer.mq.constants.PropertyKeys;
import org.archer.mq.model.MsgConsumeTask;
import org.archer.rpc.utils.Result;
import org.archer.rpc.utils.SerializeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RestController("localMsgRouter")
@RequestMapping("/msg")
public class MessageRouter implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(MessageRouter.class);

    @Autowired
    private ApplicationContext springContext;

    private ExecutorService executorService;

    @PostMapping("/receive")
    public Result<Void> receive(@RequestBody byte[] rawMsg) {
        try {
            Message message = (Message) SerializeUtils.toObject(rawMsg);
            Map<String, MessageReceiver> receiverMap = springContext.getBeansOfType(MessageReceiver.class);
            if (CollectionUtils.isEmpty(receiverMap)) {
                logger.debug("There is no any consumer to consume msg,may be config changed");
            } else {
                receiverMap.forEach((beanName, receiver) -> {
                    if (StringUtils.equals(receiver.consumerTag(), (String) message.properties().get(PropertyKeys.MSG_TARGET_CONSUMER_TAG))) {
                        logger.info("receiver " + beanName + " receive message whose id is " + message.id());
                        executorService.submit(new MsgConsumeTask(message, receiver));
                    }
                });
            }
            Result<Void> result = new Result<>();
            result.setSuccess(true);
            return result;
        } catch (Throwable e) {
            Result<Void> result = new Result<>();
            result.setSuccess(false);
            result.setExceptionCode(0);
            result.setExceptionMessage(e.getMessage());
            result.setExceptionStack(ExceptionUtils.getStackTrace(e));
            return result;
        }

    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.executorService = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors()/*corePoolSize*/,
                Runtime.getRuntime().availableProcessors() * 2/*maxPoolSize*/,
                3,
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<Runnable>());
    }
}
