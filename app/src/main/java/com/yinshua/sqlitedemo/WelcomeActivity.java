package com.yinshua.sqlitedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.yinshua.sqlitedemo.db.DbActivity;
import com.yinshua.sqlitedemo.http.HttpActivity;

/**
 * Created by marc on 2017/7/5.
 */

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void jumpsql(View view) {
        Intent intent = new Intent(this, DbActivity.class);
        startActivity(intent);
    }

    public void jumpinternet(View v) {
        Intent intent = new Intent(this, HttpActivity.class);
        startActivity(intent);
    }
}
