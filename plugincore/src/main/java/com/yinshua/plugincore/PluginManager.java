package com.yinshua.plugincore;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * 加载apk
 * Created by marc on 2017/7/12.
 */

public class PluginManager {
    //加载外置卡中的的class
    private DexClassLoader dexClassLoader;
    //上下文
    private Context context;
    private Resources resources;
    private static PluginManager instance;
    //包含acitivty的全类名
    private PackageInfo packageInfo;

    private PluginManager(Context context) {
        this.context = context;
    }

    public static PluginManager getInstance(Context context) {
        if (instance == null) {
            synchronized (PluginManager.class) {
                if (instance == null) {
                    instance = new PluginManager(context);
                }
            }
        }
        return instance;
    }

    public static PluginManager getInstance() {
        if (instance == null) {
            throw new RuntimeException("you must new intance construct with contxt");
        }
        return instance;
    }

    /**
     * 加载apk的路径
     *
     * @param path 外置卡的绝对路径
     */
    public void loadPath(String path) {
        File dexOutFile = context.getDir("dex", Context.MODE_PRIVATE);
        //实例化一个dexClassLoader
        dexClassLoader = new DexClassLoader(path, dexOutFile.getAbsolutePath(), null, context.getClassLoader());
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            //反射给assetManager设置路径
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, path);
            Resources superResource = context.getResources();
            //实例化resource
            resources = new Resources(assetManager, superResource.getDisplayMetrics(), superResource.getConfiguration());
            PackageManager packageManager = context.getPackageManager();
            packageInfo = packageManager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public DexClassLoader getDexClassLoader() {
        return dexClassLoader;
    }

    public Resources getResources() {
        return resources;
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }
}
