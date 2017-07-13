package com.yinshua.sqlitedemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.yinshua.plugincore.PluginManager;
import com.yinshua.plugincore.ProxyActivity;
import com.yinshua.sqlitedemo.db.DbActivity;
import com.yinshua.sqlitedemo.http.HttpActivity;

import java.io.File;

/**
 * Created by marc on 2017/7/5.
 */

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        PluginManager.getInstance(this);
    }

    public void jumpsql(View view) {
        Intent intent = new Intent(this, DbActivity.class);
        startActivity(intent);
    }

    public void jumpinternet(View v) {
        Intent intent = new Intent(this, HttpActivity.class);
        startActivity(intent);
    }

    public void loadapkone(View v) {
        File apkFile = new File(Environment.getExternalStorageDirectory(), "pluginone.apk");
        PluginManager.getInstance().loadPath(apkFile.getAbsolutePath());
    }

    public void loadapktwo(View v) {
        File apkFile = new File(Environment.getExternalStorageDirectory(), "plugapk2-debug.apk");
        PluginManager.getInstance().loadPath(apkFile.getAbsolutePath());
    }

    public void jumpapk(View v) {
        Intent intent = new Intent(WelcomeActivity.this, ProxyActivity.class);
        //跳到插件的activity
        intent.putExtra("classname", PluginManager.getInstance().getPackageInfo().activities[0].name);
        startActivity(intent);
    }

}
