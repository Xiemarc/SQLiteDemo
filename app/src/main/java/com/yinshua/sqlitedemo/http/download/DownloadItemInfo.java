package com.yinshua.sqlitedemo.http.download;

import com.yinshua.sqlitedemo.http.HttpTask;

/**
 * Created by marc on 2017/7/5.
 */

public class DownloadItemInfo extends BaseEntity<DownloadItemInfo> {
    private long currentLength;

    private long totalLength;

    private String url;

    private String filePath;
    //序列化需要所有成员变量都实现序列化，所以transient 排除序列化，
    private transient HttpTask httpTask;
    //下载的状态
    private DownloadStatus status;

    public DownloadItemInfo() {
    }

    public DownloadItemInfo(String url, String filePath) {
        this.url = url;
        this.filePath = filePath;
    }

    public DownloadStatus getStatus() {
        return status;
    }

    public void setStatus(DownloadStatus status) {
        this.status = status;
    }

    public long getCurrentLength() {
        return currentLength;
    }

    public void setCurrentLength(long currentLength) {
        this.currentLength = currentLength;
    }

    public long getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(long totalLength) {
        this.totalLength = totalLength;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public HttpTask getHttpTask() {
        return httpTask;
    }

    public void setHttpTask(HttpTask httpTask) {
        this.httpTask = httpTask;
    }

}
