package com.yinshua.sqlitedemo.http.download.interfaces;

import com.yinshua.sqlitedemo.http.download.DownloadItemInfo;

/**
 * Created by marc on 2017/7/5.
 */

public interface IDownloadServiceCallable {
    /**
     * 状态改变
     * @param downloadItemInfo
     */
    void onDownloadStatusChanged(DownloadItemInfo downloadItemInfo);

    /**
     *
     * @param downloadItemInfo
     */
    void onTotalLengthReceived(DownloadItemInfo downloadItemInfo);

    /**
     * 当前文件大小变化
     * @param downloadItemInfo
     * @param downLenth
     * @param speed
     */
    void onCurrentSizeChanged(DownloadItemInfo downloadItemInfo, float downLenth, long speed);

    /**
     * 下载成功了
     * @param downloadItemInfo
     */
    void onDownloadSuccess(DownloadItemInfo downloadItemInfo);

    /**
     * 用户暂停了
     * @param downloadItemInfo
     */
    void onDownloadPause(DownloadItemInfo downloadItemInfo);

    /**
     *下载错误
     * @param downloadItemInfo
     * @param var2
     * @param var3
     */
    void onDownloadError(DownloadItemInfo downloadItemInfo, int var2, String var3);
}
