package com.yinshua.sqlitedemo.http;

import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池
 * Created by marc on 2017/7/4.
 */

public class ThreadPoolManager {
    private static ThreadPoolManager instance = new ThreadPoolManager();

    public static ThreadPoolManager getInstance() {
        return instance;
    }

    private ThreadPoolExecutor threadPoolExecutor;
    private LinkedBlockingQueue<Future<?>> taskQuene = new LinkedBlockingQueue<>();

    private ThreadPoolManager() {
        threadPoolExecutor = new ThreadPoolExecutor(4, 10, 10, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(4), handler);
        threadPoolExecutor.execute(runnable);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                FutureTask futureTask = null;
                try {
                    Log.i("marc", "等待队列……" + taskQuene.size());
                    //阻塞式函数，
                    futureTask = (FutureTask) taskQuene.take();//从队列取出任务
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (futureTask != null) {
                    threadPoolExecutor.execute(futureTask);
                }
                Log.i("marc", "线程池大小……" + threadPoolExecutor.getPoolSize());
            }
        }
    };

    /**
     * 暴露方法，添加执行任务
     *
     * @param futureTask
     * @param <T>
     * @throws InterruptedException
     */
    public <T> void execte(FutureTask<T> futureTask) throws InterruptedException {
        taskQuene.put(futureTask);
    }

    /**
     * 拒绝策略
     */
    private RejectedExecutionHandler handler = new RejectedExecutionHandler() {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                taskQuene.put(new FutureTask<Object>(r, null) {
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
}
