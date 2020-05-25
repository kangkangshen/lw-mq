package org.archer.mq.utils.retry.policy;

public class DontRetry extends RetryNTime {

    public DontRetry(int maxRetryTimes, long retryInterval) {
        super(0, Integer.MIN_VALUE);
    }
}
