package com.yinshua.sqlitedemo.http.interfaces;

import java.util.Map;

/**
 * 网络设置
 * Created by marc on 2017/7/4.
 */

public interface IHttpService {
    void setUrl(String url);

    /**
     * 执行请求
     */
    void excute();

    /**
     * 设置处理接口
     *
     * @param httpListener
     */
    void setHttpListener(IHttpListener httpListener);

    /**
     * 设置请求参数
     *
     * @param requestData
     */
    void setRequestData(byte[] requestData);



    /**
     * 以下的方法是 额外添加的
     * 获取请求头的map
     *
     * @return
     */
    Map<String, String> getHttpHeadMap();

    /**
     * 取消
     *
     * @return
     */
    boolean cancle();

    /**
     * 是否取消
     *
     * @return
     */
    boolean isCancle();

    /**
     * 是否暂停
     *
     * @return
     */
    boolean isPause();

    /**
     * 暂停
     */
    void pause();
}
