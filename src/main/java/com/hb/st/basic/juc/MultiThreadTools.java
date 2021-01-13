package com.hb.st.basic.juc;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 多线程同步工具
 * CountDownLatch、CyclicBarrier、Semaphore
 *
 * @version v0.1, 2021/1/13 17:32, create by huangbiao.
 */
public class MultiThreadTools {

    /**
     * CountDownLatch：用于在完成一组正在其它线程中执行的操作之前，它允许一个或多个线程一直等待，await()表示等待，
     * 等到其它线程全部执行结束后（即通过countDown()方法来减数，计数为0，即其它线程执行完毕）然后继续执行
     */
    @Test
    public void testCountDownLatch() {
        // 该计数器初始值1，用于主线程发送命令
        final CountDownLatch latch1 = new CountDownLatch(1);
        // 该计数器初始值为2，用于响应命令接受完成
        final CountDownLatch latch2 = new CountDownLatch(2);
        // 创建一个大小为2线程池
        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = 0; i < 2; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("线程" + Thread.currentThread().getName() + "正准备接受命令");
                        // 等待主线程发送命令
                        latch1.await();
                        System.out.println("线程" + Thread.currentThread().getName() + "已接受命令");
                        Thread.sleep((long)(Math.random() * 10000));
                        System.out.println("线程" + Thread.currentThread().getName() + "回应命令处理结果");
                        // 命令接受完毕，返回给主线程，latch2减1。
                        latch2.countDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        try {
            Thread.sleep((long)(Math.random() * 10000));
            System.out.println("线程" + Thread.currentThread().getName() + "即将发布命令");
            // 发送命令，latch1计数减1
            latch1.countDown();
            System.out.println("线程" + Thread.currentThread().getName() + "已发送命令，正在等待响应");
            // 命令发送后处于等待状态，其它线程全部响应完成，也就是latch2.countDown()，再继续执行
            latch2.await();
            System.out.println("线程" + Thread.currentThread().getName() + "已收到所有响应结果");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 关闭线程池
        executor.shutdown();
    }

    /**
     * CyclicBarriar：用于多个线程在一个指定的公共屏障点（或者说集合点）相互等待，await()方法代表屏障点，每次调用await()，
     * 计数（创建CyclicBarriar对象时传入int类型的参数，表示初始计数）减一，直到减到0后，表示所有线程都抵达，然后开始执行后面的任务
     *
     * 注意：观察CyclicBarrier的使用可以发现，它计数减至0后，计数器会被重置，可以再次使用，
     * 可能这也是它被定义为Cyclic(周期的、循环的)原因，这个是和CountDownLatch区别的地方。
     */
    @Test
    public void testCyclicBarrier() {
        // 创建CyclicBarrier对象并设置2个公共屏障点
        final CyclicBarrier barrier = new CyclicBarrier(2);
        // 创建大小为2的线程池
        ExecutorService executor = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 2; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("线程" + Thread.currentThread().getName() + "即将到达集合地点1，当前已有"
                            + barrier.getNumberWaiting() + "个已经到达，正在等候");
                        // 如果没有达到公共屏障点，则该线程处于阻塞状态，如果达到公共屏障点则所有处于等待的线程都继续往下运行
                        barrier.await();

                        System.out.println("线程" + Thread.currentThread().getName() + "通过集合地点1");

                        System.out.println("线程" + Thread.currentThread().getName() + "即将到达集合地点2，当前已有"
                            + barrier.getNumberWaiting() + "个已经到达，正在等候");
                        barrier.await();

                        System.out.println("线程" + Thread.currentThread().getName() + "通过集合地点2");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 关闭线程池
        executor.shutdown();
    }

    /**
     * Semaphore也是一个线程同步的辅助类，可以维护当前访问自身的线程个数，并提供了同步机制。
     * 使用Semaphore可以控制同时访问资源的线程个数，例如，实现一个文件允许的并发访问数。
     *
     * Semaphore的主要方法摘要：
     * 　　void acquire():从此信号量获取一个许可，在提供一个许可前一直将线程阻塞，否则线程被中断。
     * 　　void release():释放一个许可，将其返回给信号量。
     * 　　int availablePermits():返回此信号量中当前可用的许可数。
     * 　　boolean hasQueuedThreads():查询是否有线程正在等待获取。
     */
    @Test
    public void testSemaphore() {
        ExecutorService service = Executors.newCachedThreadPool();
        final Semaphore sp = new Semaphore(2);// 创建Semaphore信号量，初始化许可大小为3
        for (int i = 0; i < 4; i++) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        sp.acquire();// 请求获得许可，如果有可获得的许可则继续往下执行，许可数减1。否则进入阻塞状态
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    System.out.println(
                        "线程" + Thread.currentThread().getName() + "进入，当前已有" + (2 - sp.availablePermits()) + "个并发");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    System.out.println("线程" + Thread.currentThread().getName() + "即将离开");
                    sp.release();// 释放许可，许可数加1
                    // 下面代码有时候执行不准确，因为其没有和上面的代码合成原子单元
                    System.out.println(
                        "线程" + Thread.currentThread().getName() + "已离开，当前已有" + (2 - sp.availablePermits()) + "个并发");
                }
            };
            service.execute(runnable);
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }
    }

}
