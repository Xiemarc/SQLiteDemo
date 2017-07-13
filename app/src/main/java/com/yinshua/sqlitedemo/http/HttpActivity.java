package com.yinshua.sqlitedemo.http;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.yinshua.sqlitedemo.R;
import com.yinshua.sqlitedemo.bean.NewsPager;
import com.yinshua.sqlitedemo.http.download.DownFileManager;
import com.yinshua.sqlitedemo.http.download.enums.DownloadStatus;
import com.yinshua.sqlitedemo.http.download.enums.DownloadStopMode;
import com.yinshua.sqlitedemo.http.download.interfaces.IDownloadCallable;
import com.yinshua.sqlitedemo.http.interfaces.IDataListener;
import com.yinshua.sqlitedemo.utils.logger.Logger;

/**
 * Created by marc on 2017/7/5.
 */

public class HttpActivity extends AppCompatActivity implements IDownloadCallable {
    private static String url = "http://v.juhe.cn/toutiao/index?type=top&key=29da5e8be9ba88b932394b7261092f71";
    private static final String TAG = "marc";
    private int downloadId = -1;
    private DownFileManager downFileManager;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    public void test(View v) {
        for (int i = 0; i < 20; i++) {
            Volley.sendRequest(null, url, NewsPager.class, new IDataListener<NewsPager>() {
                @Override
                public void onSuccess(NewsPager newsPager) {
                    Log.i("marc", newsPager.toString());
                }

                @Override
                public void onFail() {
                    Log.i("marc", "失败了");
                }
            });
        }

    }

    public void download(View v) {
        downFileManager = new DownFileManager();
        downFileManager.setDownCallable(this);
        downFileManager.download("http://gdown.baidu.com/data/wisegame/8be18d2c0dc8a9c9/WPSOffice_177.apk");
    }

    public void pause(View v) {
        downFileManager.pause(downloadId, DownloadStopMode.hand);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        downFileManager.pause(downloadId, DownloadStopMode.auto);
    }

    @Override
    public void onDownloadInfoAdd(int downloadId) {
        this.downloadId = downloadId;
        Toast.makeText(this, "添加任务成功 ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownloadInfoRemove(int downloadId) {
        if (downloadId == this.downloadId) {
            Toast.makeText(this, " onDownloadInfoRemove成功", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDownloadStatusChanged(int downloadId, DownloadStatus status) {
        Logger.i(TAG, "onDownloadStatusChanged ");
        Toast.makeText(this, "暂停了", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTotalLengthReceived(int downloadId, long totalLength) {
        Logger.i(TAG, "onTotalLengthReceived ");
        if (downloadId == this.downloadId) {
            progressBar.setMax((int) totalLength);
        }
    }

    @Override
    public void onCurrentSizeChanged(int downloadId, double downloadpercent, long speed) {
        Logger.i(TAG, "onCurrentSizeChanged ");
        if (downloadId == this.downloadId) {
            progressBar.setProgress((int) downloadpercent);
        }
    }

    @Override
    public void onDownloadSuccess(int downloadId) {
        Logger.i(TAG, "下载成功 ");
    }

    @Override
    public void onDownloadError(int downloadId, int errorCode, String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
        Logger.i(TAG, "下载id" + downloadId + "   出错信息" + errorMsg);
    }
}
