package com.yinshua.plugincore;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 替身
 * <p>
 * Created by marc on 2017/7/12.
 */

public class ProxyActivity extends Activity {

    //替换插件apk中的activity的全类名
    String className;
    private PluginInterface pluginInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        className = intent.getStringExtra("classname");
        lauchActivity();
    }

    /**
     * 通过反射加载外置卡的apk的activity class文件
     */
    private void lauchActivity() {
        try {
            //通过的dexClassLoader加载外置卡class
            Class<?> loadClass = PluginManager.getInstance().getDexClassLoader().loadClass(className);
            Constructor constructor = loadClass.getConstructor(new Class[]{});
            //反射得到acitivity实例
            Object instance = constructor.newInstance(new Object[]{});
            //利用标准接口，将插件apk里的class强转成接口
            pluginInterface = (PluginInterface) instance;
            pluginInterface.attach(this);
            Bundle bundle = new Bundle();
            //值传递
            pluginInterface.onCreate(bundle);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        pluginInterface.onStart();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pluginInterface.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pluginInterface.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pluginInterface.onPause();
    }


    /**
     * 若不重写本方法，那么加载的资源就是本apk的资源
     * 必须重写
     *
     * @return
     */
    @Override
    public Resources getResources() {
        return PluginManager.getInstance().getResources();
    }
}
