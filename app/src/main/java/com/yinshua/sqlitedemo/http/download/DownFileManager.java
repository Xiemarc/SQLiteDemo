package com.yinshua.sqlitedemo.http.download;

import android.os.Environment;
import android.util.Log;

import com.yinshua.sqlitedemo.http.HttpTask;
import com.yinshua.sqlitedemo.http.RequestHodler;
import com.yinshua.sqlitedemo.http.ThreadPoolManager;
import com.yinshua.sqlitedemo.http.download.interfaces.IDownloadServiceCallable;
import com.yinshua.sqlitedemo.http.interfaces.IHttpListener;
import com.yinshua.sqlitedemo.http.interfaces.IHttpService;

import java.io.File;
import java.util.Map;
import java.util.concurrent.FutureTask;

/**
 * 下载管理控制器
 * Created by marc on 2017/7/5.
 */

public class DownFileManager implements IDownloadServiceCallable {
    private static final String TAG = "marc";
    //    private  static
    private byte[] lock = new byte[0];

    public void down(String url) {
        String[] preFixs = url.split("/");
        String afterFixs = preFixs[preFixs.length - 1];
        File file = new File(Environment.getExternalStorageDirectory(), afterFixs);
        //实例化downloaditeminfo
        DownloadItemInfo downloadItemInfo = new DownloadItemInfo(url, file.getAbsolutePath());
        downloadItemInfo.setUrl(url);
        synchronized (lock) {
            RequestHodler requestHodler = new RequestHodler();
            //设置请求下载的策略
            IHttpService httpService = new FileDownHttpService();
            //得到请求头的参数map
            Map<String, String> httpHeadMap = httpService.getHttpHeadMap();
            //处理结果的策略
            IHttpListener httpListener = new DownLoadListener(downloadItemInfo, this, httpService);
            requestHodler.setHttpListener(httpListener);
            requestHodler.setHttpService(httpService);
            requestHodler.setUrl(url);
            HttpTask httpTask = new HttpTask(requestHodler);
            try {
                ThreadPoolManager.getInstance().execte(new FutureTask<Object>(httpTask, null));
            } catch (Exception e) {
                e.printStackTrace();
                httpListener.onFail();
            }
        }
    }

    @Override
    public void onDownloadStatusChanged(DownloadItemInfo downloadItemInfo) {
        Log.i(TAG, "onDownloadStatusChanged");
    }

    @Override
    public void onTotalLengthReceived(DownloadItemInfo downloadItemInfo) {
        Log.i(TAG, "onTotalLengthReceived");
    }

    @Override
    public void onCurrentSizeChanged(DownloadItemInfo downloadItemInfo, float downLenth, long speed) {
        Log.i(TAG, "下载速度：" + speed / 1000 + "k/s");
        Log.i(TAG, "-----路径  " + downloadItemInfo.getFilePath() + "  下载长度  " + downLenth + "   速度  " + speed);
    }

    @Override
    public void onDownloadSuccess(DownloadItemInfo downloadItemInfo) {
        Log.i(TAG, "下载成功    路径  " + downloadItemInfo.getFilePath() + "  url " + downloadItemInfo.getUrl());
    }

    @Override
    public void onDownloadPause(DownloadItemInfo downloadItemInfo) {
        Log.i(TAG, "暂停");
    }

    @Override
    public void onDownloadError(DownloadItemInfo downloadItemInfo, int var2, String var3) {
        Log.i(TAG, "下载出错   " + var3);
    }
}
