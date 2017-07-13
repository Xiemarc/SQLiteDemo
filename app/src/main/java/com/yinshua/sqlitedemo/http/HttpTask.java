package com.yinshua.sqlitedemo.http;

import com.alibaba.fastjson.JSON;
import com.yinshua.sqlitedemo.http.interfaces.IHttpListener;
import com.yinshua.sqlitedemo.http.interfaces.IHttpService;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.FutureTask;

/**
 * 请求任务
 * T 代表请求的类型
 * Created by marc on 2017/7/4.
 */

public class HttpTask<T> implements Runnable {
    private IHttpService httpService;
    private FutureTask futureTask;

    public HttpTask(RequestHodler<T> requestHodler) {
        httpService = requestHodler.getHttpService();
        httpService.setHttpListener(requestHodler.getHttpListener());
        httpService.setUrl(requestHodler.getUrl());
        //增加方法
        IHttpListener httpListener = requestHodler.getHttpListener();
        httpListener.addHttpHeader(httpService.getHttpHeadMap());
        try {
            T request = requestHodler.getRequestInfo();
            if (request != null) {
                String requestInfo = JSON.toJSONString(request);
                httpService.setRequestData(requestInfo.getBytes("UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        httpService.excute();
    }

    /**
     * 真正开始下载任务
     */
    public void start() {
        futureTask = new FutureTask(this, null);
        try {
            ThreadPoolManager.getInstance().execte(futureTask);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        httpService.pause();
        if (futureTask != null) {
            ThreadPoolManager.getInstance().removeTask(futureTask);
        }
    }
}
