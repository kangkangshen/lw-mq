package org.archer.mq.utils;

import junit.framework.TestCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentUtilsTest extends TestCase {

    /**
     * 使用main函数正确观测到了结果
     *
     * @param args
     */
    public static void main(String[] args) {
        Runnable runnable = () -> {
            System.out.println("hello,world");
            throw new RuntimeException();
        };
        Runnable another = () -> {
            System.out.println("another task");
        };
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.submit(ConcurrentUtils.supportRetry(runnable, executorService));
        executorService.submit(another);
    }

    /**
     * 使用Junit测试框架测试不稳定
     */
    public void testSupportRetry() {
        Runnable runnable = () -> {
            System.out.println("hello,world");
            throw new RuntimeException();
        };
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(ConcurrentUtils.supportRetry(runnable, executorService));
    }
}