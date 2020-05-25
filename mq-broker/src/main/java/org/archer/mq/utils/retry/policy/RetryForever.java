package org.archer.mq.utils.retry.policy;


import org.archer.mq.constants.RetryStateEnum;
import org.archer.mq.utils.retry.RetryPolicy;


/**
 * 一直重试，直到成功
 */
public class RetryForever implements RetryPolicy {

    @Override
    public boolean allowRetry() {
        return true;
    }

    @Override
    public RetryStateEnum recalculate() {
        return RetryStateEnum.WAIT_RETRY;
    }
}
