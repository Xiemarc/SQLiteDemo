package com.xie.plugapk2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.yinshua.plugincore.BasePluginActivity;

/**
 * Created by marc on 2017/7/13.
 */

public class SecondActivity extends BasePluginActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Toast.makeText(that, "第二个activityde seonada", Toast.LENGTH_SHORT).show();
    }
}
