package com.yinshua.pluginapk;

import android.os.Bundle;
import android.widget.Toast;

import com.yinshua.plugincore.BasePluginActivity;

public class MainActivity extends BasePluginActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(that, "第一个app弹出个东西", Toast.LENGTH_SHORT).show();
    }
}
