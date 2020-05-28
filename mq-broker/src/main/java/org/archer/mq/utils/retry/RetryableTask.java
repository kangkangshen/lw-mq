package org.archer.mq.utils.retry;

import org.archer.mq.constants.RetryStateEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class RetryableTask implements Retryable {

    private static final Logger logger = LoggerFactory.getLogger(Retryable.class);

    private final RetryPolicy retryPolicy;

    private final Runnable task;

    private final ExecutorService executorService;

    private final ExceptionHandler exceptionHandler;

    private Runnable retryHandle;

    private volatile RetryStateEnum retryStateEnum;


    public RetryableTask(@Nonnull Runnable task, @Nonnull RetryPolicy retryPolicy, @Nonnull ExceptionHandler exceptionHandler, @Nonnull ExecutorService executorService) {
        Assert.notNull(task, "task must be not null");
        Assert.notNull(retryPolicy, "retryPolicy must be not null");
        Assert.notNull(exceptionHandler, "exceptionHandler must be not null");
        Assert.notNull(executorService, "executorService must be not null");
        this.task = task;
        this.retryPolicy = retryPolicy;
        this.exceptionHandler = exceptionHandler;
        this.executorService = executorService;
        this.retryStateEnum = RetryStateEnum.WAIT_RETRY;
    }

    public void setRetryHandle(Runnable retryHandle) {
        this.retryHandle = retryHandle;
    }

    @Override
    public void run() {
        if (retryPolicy.allowRetry()) {
            try {
                this.task.run();
            } catch (Throwable e) {
                this.retryStateEnum = retryPolicy.recalculate();
                if (Objects.equals(RetryStateEnum.WAIT_RETRY, this.retryStateEnum)) {
                    if (Objects.nonNull(retryHandle)) {
                        retryHandle.run();
                    }
                    this.executorService.submit(this);
                } else if (Objects.equals(RetryStateEnum.RETRY_FAILED, this.retryStateEnum)) {
                    exceptionHandler.onException(e);
                }
            }
        } else {
            this.executorService.submit(this);
        }
    }

}
