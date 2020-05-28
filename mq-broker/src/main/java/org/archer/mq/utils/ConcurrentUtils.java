package org.archer.mq.utils;

import org.archer.mq.utils.retry.ExceptionHandler;
import org.archer.mq.utils.retry.RetryPolicy;
import org.archer.mq.utils.retry.Retryable;
import org.archer.mq.utils.retry.RetryableTask;
import org.archer.mq.utils.retry.policy.RetryNTime;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public final class ConcurrentUtils {

    private ConcurrentUtils() {
    }

    /**
     * 默认重试3次，每次间隔3秒
     *
     * @param task 原task
     * @return 包装之后的支持重试的task
     */
    public static Retryable supportRetry(Runnable task, ExecutorService executorService) {
        return new RetryableTask(
                task,
                new RetryNTime(3, 3, TimeUnit.SECONDS),
                Throwable::printStackTrace,
                executorService);
    }


    public static Retryable supportRetry(Runnable task, ExecutorService executorService, RetryPolicy retryPolicy) {
        return new RetryableTask(
                task,
                retryPolicy,
                Throwable::printStackTrace,
                executorService);
    }

    public static Retryable supportRetry(Runnable task, ExecutorService executorService, RetryPolicy retryPolicy, Runnable retryHandle, ExceptionHandler exceptionHandler) {
        return new RetryableTask(
                task,
                retryPolicy,
                exceptionHandler,
                executorService);
    }

}
