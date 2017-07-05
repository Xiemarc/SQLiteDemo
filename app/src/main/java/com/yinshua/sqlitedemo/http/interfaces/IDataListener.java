package com.yinshua.sqlitedemo.http.interfaces;

/**
 * 针对 view层进行回调
 * Created by marc on 2017/7/4.
 */

public interface IDataListener<M> {

    /**
     * 回调结果给调用层
     *
     * @param m
     */
    void onSuccess(M m);


    void onFail();
}
