package com.yinshua.sqlitedemo.http;

import com.alibaba.fastjson.JSON;
import com.yinshua.sqlitedemo.http.interfaces.IHttpListener;
import com.yinshua.sqlitedemo.http.interfaces.IHttpService;

import java.io.UnsupportedEncodingException;

/**
 * 请求任务
 * T 代表请求的类型
 * Created by marc on 2017/7/4.
 */

public class HttpTask<T> implements Runnable {
    private IHttpService httpService;

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
}
