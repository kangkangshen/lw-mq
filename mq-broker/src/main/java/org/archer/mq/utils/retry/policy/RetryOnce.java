package org.archer.mq.utils.retry.policy;

public class RetryOnce extends RetryNTime {

    public RetryOnce(long retryInterval) {
        super(1, retryInterval);
    }

}
