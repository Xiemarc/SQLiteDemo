package com.yinshua.sqlitedemo.http;

import com.yinshua.sqlitedemo.http.interfaces.IDataListener;
import com.yinshua.sqlitedemo.http.interfaces.IHttpListener;
import com.yinshua.sqlitedemo.http.interfaces.IHttpService;
import com.yinshua.sqlitedemo.http.jsonstring.JsonDealListener;
import com.yinshua.sqlitedemo.http.jsonstring.JsonHttpService;

import java.util.concurrent.FutureTask;

/**
 * Created by marc on 2017/7/4.
 */

public class Volley {

    /**
     * 具体的请求方法
     *
     * @param requestInfo  请求信息参数
     * @param url          请求地址
     * @param response     响应返回数据
     * @param dataListener
     * @param <T>          请求信息参数
     * @param <M>          响应返回数据
     */
    public static <T, M> void sendRequest(T requestInfo, String url, Class<M> response, IDataListener dataListener) {
        RequestHodler<T> requestHodler = new RequestHodler<>();
        requestHodler.setUrl(url);
        IHttpService httpService = new JsonHttpService();
        IHttpListener httpListener = new JsonDealListener<>(response, dataListener);
        requestHodler.setHttpListener(httpListener);
        requestHodler.setHttpService(httpService);
        requestHodler.setRequestInfo(requestInfo);
        HttpTask<T> httpTask = new HttpTask<>(requestHodler);
        try {
            ThreadPoolManager.getInstance().execte(new FutureTask<Object>(httpTask, null));
        } catch (InterruptedException e) {
            e.printStackTrace();
            dataListener.onFail();
        }
    }
}
