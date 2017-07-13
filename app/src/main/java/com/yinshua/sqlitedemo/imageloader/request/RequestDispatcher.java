package com.yinshua.sqlitedemo.imageloader.request;

import java.util.concurrent.BlockingQueue;

/**
 * 请求转发线程，不断从请求队列中获取请求处理
 * Created by marc on 2017/7/11.
 */

public class RequestDispatcher extends Thread {
    private BlockingQueue<BitmapRequest> mRequestQueue;

    public RequestDispatcher(BlockingQueue<BitmapRequest> mRequestQueue) {
        this.mRequestQueue = mRequestQueue;
    }

    @Override
    public void run() {
        //非阻塞状态，获取请求处理
        while (!isInterrupted()){
            try {
                //从队列中获取优先级最高的请求进行处理
                BitmapRequest request = mRequestQueue.take();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
