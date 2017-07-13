package com.yinshua.sqlitedemo.http.download.enums;

/**
 * 下载状态
 * Created by marc on 2017/7/5.
 */

public enum DownloadStatus {
    //暂停
    waitting(0),
    //开始
    starting(1),
    //下载中
    downloading(2),
    //暂停
    pause(3),
    //结束
    finish(4),
    //失败
    failed(5);

    private int value;

    private DownloadStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
