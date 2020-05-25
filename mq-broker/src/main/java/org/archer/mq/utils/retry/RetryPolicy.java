package org.archer.mq.utils.retry;

import org.archer.mq.constants.RetryStateEnum;

public interface RetryPolicy {

    /**
     * 判断当前是否允许进行重试，不允许重试的可能性有两种：
     * 一种是处于重试间隔内未达到重试时间，当前调用不允许直接重试
     * 另一种是超过重试策略限制，比如达到最大重试次数导致不允许重试
     *
     * @return 本次调用是否允许运行
     */
    boolean allowRetry();

    /**
     * 重新计算并返回当前的重试状态
     *
     * @return 当前的重试状态
     * @see RetryStateEnum
     */
    RetryStateEnum recalculate();
}
