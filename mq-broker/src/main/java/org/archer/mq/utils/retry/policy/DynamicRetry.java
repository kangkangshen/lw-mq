package org.archer.mq.utils.retry.policy;

import org.archer.mq.constants.RetryStateEnum;
import org.archer.mq.utils.retry.RetryPolicy;
import org.springframework.util.Assert;

import java.util.concurrent.atomic.AtomicInteger;

public class DynamicRetry implements RetryPolicy {

    private final AtomicInteger retryTimes;

    /**
     * 最大重试次数
     */
    private final int maxRetryTimes;


    private long lastCalculateTime;


    public DynamicRetry(int maxRetryTimes) {
        Assert.isTrue(maxRetryTimes >= 0, "maxRetryTimes must be >=0");
        this.maxRetryTimes = maxRetryTimes;
        this.retryTimes = new AtomicInteger();
    }

    @Override
    public boolean allowRetry() {
        if (retryTimes.incrementAndGet() > maxRetryTimes) {
            return false;
        }
        return System.currentTimeMillis() - lastCalculateTime >= nextRetryInterval();
    }

    @Override
    public RetryStateEnum recalculate() {
        lastCalculateTime = System.currentTimeMillis();
        return retryTimes.incrementAndGet() <= maxRetryTimes ? RetryStateEnum.WAIT_RETRY : RetryStateEnum.RETRY_FAILED;
    }

    /**
     * 需要重载
     *
     * @return 下一次重试间隔
     */
    public long nextRetryInterval() {
        return 1000;
    }
}
