package com.xie.plugapk2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.yinshua.plugincore.BasePluginActivity;
import com.yinshua.plugincore.PluginManager;
import com.yinshua.plugincore.ProxyActivity;

public class MainActivity extends BasePluginActivity {
    ImageView iv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(that, "第二个app", Toast.LENGTH_SHORT).show();
        iv = findViewById(R.id.image);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(that, ProxyActivity.class);
                intent.putExtra("classname", PluginManager.getInstance().getPackageInfo().activities[1].name);
                startActivity(intent);
            }
        });
        findViewById(R.id.chanimage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv.setImageResource(R.drawable.girl_1);
            }
        });
    }

}
