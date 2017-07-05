package com.yinshua.sqlitedemo.http;

import com.yinshua.sqlitedemo.http.interfaces.IHttpListener;
import com.yinshua.sqlitedemo.http.interfaces.IHttpService;

/**
 * 请求参数封装
 * Created by marc on 2017/7/4.
 */

public class RequestHodler<T> {
    /**
     * 获取数据 回调结果类
     */
    private IHttpListener httpListener;
    /**
     * 执行下载类
     */
    private IHttpService httpService;
    /**
     * 请求参数对应的实体
     */
    private T requestInfo;
    /**
     * 请求地址
     */
    private String url;

    public IHttpListener getHttpListener() {
        return httpListener;
    }

    public void setHttpListener(IHttpListener httpListener) {
        this.httpListener = httpListener;
    }

    public IHttpService getHttpService() {
        return httpService;
    }

    public void setHttpService(IHttpService httpService) {
        this.httpService = httpService;
    }

    public T getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(T requestInfo) {
        this.requestInfo = requestInfo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
