package org.archer.mq.utils.retry;


@FunctionalInterface
public interface ExceptionHandler {

    void onException(Throwable e);
}
