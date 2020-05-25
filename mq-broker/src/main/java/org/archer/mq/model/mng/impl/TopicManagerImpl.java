package org.archer.mq.model.mng.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.transaction.CuratorOp;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.RetryForever;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.CreateMode;
import org.archer.mq.config.ZkConfig;
import org.archer.mq.constants.RetryPolicyEnum;
import org.archer.mq.model.mng.TopicManager;
import org.archer.mq.model.arch.SubscriberMeta;
import org.archer.rpc.constants.Delimiters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class TopicManagerImpl implements TopicManager, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(TopicManager.class);

    private static final String ROOT_PATH = "msg/topic";

    private CuratorFramework curatorClient;

    private TreeCache topicCache;

    @Resource
    private Environment environment;


    @SneakyThrows
    @Override
    public void declareTopic(String topic, String desc) {
        String path = Delimiters.SLASH + topic;
        if (Objects.isNull(topicCache.getCurrentChildren(path))) {
            //null 代表不存在该topic节点
            String result = curatorClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, desc.getBytes());
            logger.debug(result);
        } else {
            //不为null,有可能存在topic节点但是topic节点不存在children，此时返回empty map
            logger.debug("topic " + topic + " already exists，ignore this declare");
        }
    }

    @SneakyThrows
    @Override
    public void destroyTopic(String topic) {
        String path = Delimiters.SLASH + topic;

        if (Objects.isNull(topicCache.getCurrentChildren(path))) {
            //null 代表不存在该topic节点
            logger.debug("topic " + topic + " does not exist,ignore this destroy");
        } else if (CollectionUtils.isEmpty(topicCache.getCurrentChildren(path))) {
            //仅仅只做必要性检查，zkServer仍会检查当前topic下是否存在子节点
            curatorClient.delete().forPath(path);
            logger.debug("topic " + topic + " has been destroy");
        }
    }

    @Override
    public boolean exists(String topic) {
        String path = Delimiters.SLASH + topic;
        return Objects.nonNull(topicCache.getCurrentChildren(path));
    }

    @Override
    public Multimap<String/*consumerTag*/, SubscriberMeta> subscriberMeta(String topic) {
        String path = Delimiters.SLASH + topic;
        Multimap<String, SubscriberMeta> result = ArrayListMultimap.create();
        topicCache.getCurrentChildren(path).forEach((consumerTag, childData) -> {
            SubscriberMeta meta = JSON.parseObject(childData.getData(), SubscriberMeta.class);
            result.put(consumerTag, meta);
        });
        return result;
    }

    @SneakyThrows
    @Override
    public void subscribe(String topic, SubscriberMeta meta) {
        String jsonData = JSON.toJSONString(meta);
        String nodePath = Delimiters.SLASH + topic + Delimiters.SLASH + DigestUtils.md5Hex(jsonData);
        curatorClient.create().withMode(CreateMode.EPHEMERAL).forPath(nodePath, jsonData.getBytes());
    }


    @SneakyThrows
    @Override
    public void batchSubscribe(Multimap<String, SubscriberMeta> metas) {
        List<CuratorOp> operations = Lists.newArrayList();
        metas.forEach((topic, subscriberMeta) -> {
            String jsonData = JSON.toJSONString(subscriberMeta);
            String nodePath = Delimiters.SLASH + topic + Delimiters.SLASH + DigestUtils.md5Hex(jsonData);
            try {
                operations.add(curatorClient.transactionOp().create().withMode(CreateMode.EPHEMERAL).forPath(nodePath, jsonData.getBytes()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        curatorClient.transaction().forOperations(operations).forEach(curatorTransactionResult -> logger.debug(curatorTransactionResult.getResultPath() + " create successfully"));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initCuratorClient();
        initTopicCache();
    }


    @Autowired
    private ZkConfig zkConfig;

    private void initCuratorClient() {
        this.curatorClient = CuratorFrameworkFactory.builder()
                .connectString(zkConfig.getZkServerPath())
                .sessionTimeoutMs(zkConfig.getSessionTimeout())
                .retryPolicy(retryPolicy())
                .namespace(ROOT_PATH)
                .build();
        curatorClient.start();
    }

    @SneakyThrows
    private void initTopicCache() {
        this.topicCache = TreeCache.newBuilder(curatorClient, Delimiters.SLASH)
                .setMaxDepth(4)
                .setDataIsCompressed(false)
                .setCacheData(true)
                .setCreateParentNodes(false)
                .build();
        this.topicCache.start();
    }

    private RetryPolicy retryPolicy() {
        RetryPolicyEnum retryPolicyEnum = Optional.ofNullable(RetryPolicyEnum.of(zkConfig.getRetryPolicy())).orElse(RetryPolicyEnum.RETRY_FOREVER);
        switch (retryPolicyEnum) {
            case RETRY_FOREVER:
                return new RetryForever(zkConfig.getRetryInterval());
            case RETRY_NTIMES:
                return new RetryNTimes(zkConfig.getMaxRetryTimes(), zkConfig.getRetryInterval());
            case RETRY_ONE_TIME:
                return new RetryOneTime(zkConfig.getRetryInterval());
        }
        //todo dongyue 未来将使用string模板
        throw new UnsupportedOperationException(zkConfig.getRetryPolicy() + " policy cannot supported");
    }


}
