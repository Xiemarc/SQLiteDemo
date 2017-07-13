package com.yinshua.sqlitedemo.imageloader.core;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.yinshua.sqlitedemo.imageloader.config.DisplayConfig;
import com.yinshua.sqlitedemo.imageloader.config.ImageLoaderConfig;
import com.yinshua.sqlitedemo.imageloader.request.RequestQueue;

/**
 * 对外暴露方法
 * Created by marc on 2017/7/11.
 */

public class SimpleImageLoader {
    private ImageLoaderConfig mConfig;
    //请求队列
    private RequestQueue mRequestQueue;
    //单例模式
    private static volatile SimpleImageLoader mInstance;

    private SimpleImageLoader() {

    }

    private SimpleImageLoader(ImageLoaderConfig config) {
        this.mConfig = config;
        //初始化请求队列
        mRequestQueue = new RequestQueue();
    }

    private static SimpleImageLoader getInstance(ImageLoaderConfig config) {
        if (mInstance == null) {
            synchronized (SimpleImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new SimpleImageLoader(config);
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取SimpleImageLoader的实例
     *
     * @return 只有在SimpleImageLoader getInstance(ImageLoaderConfig config)调用过之后，才能返回一个实例化了的SimpleImageLoader对象
     */
    public static SimpleImageLoader getInstance() {
        if (mInstance == null) {
            throw new UnsupportedOperationException("getInstance(ImageLoaderConfig config) 没有执行过！");
        }
        return mInstance;
    }

    /**
     * 暴露获取图片
     *
     * @param imageView
     * @param uri       分为http和file
     */
    public void displayImage(ImageView imageView, String uri) {
        displayImage(imageView, uri, null, null);
    }

    /**
     * 重载
     *
     * @param imageView
     * @param uri
     * @param config
     * @param imageListener
     */
    public void displayImage(ImageView imageView, String uri, DisplayConfig config, ImageListener imageListener) {
        //设置具体请求

    }

    /**
     * 图片加载的监听
     */
    public static interface ImageListener {
        /**
         * 加载完成
         *
         * @param imageView
         * @param bitmap
         * @param uri
         */
        void onComplete(ImageView imageView, Bitmap bitmap, String uri);
    }


    public ImageLoaderConfig getConfig() {
        return mConfig;
    }
}
