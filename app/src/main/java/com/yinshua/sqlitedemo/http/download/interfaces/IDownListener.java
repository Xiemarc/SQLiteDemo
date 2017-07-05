package com.yinshua.sqlitedemo.http.download.interfaces;

import com.yinshua.sqlitedemo.http.interfaces.IHttpListener;
import com.yinshua.sqlitedemo.http.interfaces.IHttpService;

/**
 * Created by marc on 2017/7/5.
 */

public interface IDownListener extends IHttpListener {
    //新添加几个扩展方法
    void setHttpService(IHttpService httpService);

    void setCancleCanable();

    void setPauseCanalbe();
}
