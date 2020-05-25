package org.archer.mq.utils.retry.policy;

import org.archer.mq.constants.RetryStateEnum;
import org.archer.mq.utils.retry.RetryPolicy;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RetryNTime implements RetryPolicy {

    private final AtomicInteger retryTimes;

    /**
     * 最大重试次数
     */
    private final int maxRetryTimes;

    /**
     * 重试间隔(固定版),单位ms
     */
    private final long retryInterval;

    private volatile long lastCalculateTime;


    public RetryNTime(int maxRetryTimes, long retryInterval) {
        Assert.isTrue(maxRetryTimes >= 0, "maxRetryTimes must be >=0");
        Assert.isTrue(retryInterval > 0, "retryInterval must be >0");
        this.maxRetryTimes = maxRetryTimes;
        this.retryInterval = retryInterval;
        this.retryTimes = new AtomicInteger();
    }

    public RetryNTime(int maxRetryTimes, int retryInterval, TimeUnit timeUnit) {
        this(maxRetryTimes, timeUnit.toMillis(retryInterval));
    }

    @Override
    public boolean allowRetry() {
        if (retryTimes.get() > maxRetryTimes) {
            return false;
        }
        return System.currentTimeMillis() - lastCalculateTime >= retryInterval;
    }

    @Override
    public RetryStateEnum recalculate() {
        lastCalculateTime = System.currentTimeMillis();
        return retryTimes.incrementAndGet() <= maxRetryTimes
                ? RetryStateEnum.WAIT_RETRY
                : RetryStateEnum.RETRY_FAILED;
    }
}
