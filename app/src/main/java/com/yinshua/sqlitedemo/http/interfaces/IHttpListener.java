package com.yinshua.sqlitedemo.http.interfaces;

import org.apache.http.HttpEntity;

import java.util.Map;

/**
 * 网络访问 回调
 * Created by marc on 2017/7/4.
 */

public interface IHttpListener {
    /**
     * 处理结果成功
     *
     * @param httpEntity
     */
    void onSuccess(HttpEntity httpEntity);

    /**
     * 失败
     */
    void onFail();

    /**
     * 添加请求头
     * @param headerMap
     */
    void addHttpHeader(Map<String,String> headerMap);
}
