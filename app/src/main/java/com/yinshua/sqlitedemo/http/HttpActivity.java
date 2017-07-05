package com.yinshua.sqlitedemo.http;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.yinshua.sqlitedemo.R;
import com.yinshua.sqlitedemo.bean.NewsPager;
import com.yinshua.sqlitedemo.http.download.DownFileManager;
import com.yinshua.sqlitedemo.http.interfaces.IDataListener;

/**
 * Created by marc on 2017/7/5.
 */

public class HttpActivity extends AppCompatActivity {
    private static String url = "http://v.juhe.cn/toutiao/index?type=top&key=29da5e8be9ba88b932394b7261092f71";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);
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
        DownFileManager downFileManager = new DownFileManager();
        downFileManager.down("http://gdown.baidu.com/data/wisegame/8be18d2c0dc8a9c9/WPSOffice_177.apk");
    }
}
