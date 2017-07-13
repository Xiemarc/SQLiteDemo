package com.yinshua.sqlitedemo;

import android.app.Application;

import com.yinshua.sqlitedemo.utils.logger.Logger;

/**
 * Created by marc on 2017/7/11.
 */

public class MApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init("marc");
    }
}
