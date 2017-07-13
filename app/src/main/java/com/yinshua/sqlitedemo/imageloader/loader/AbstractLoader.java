package com.yinshua.sqlitedemo.imageloader.loader;

import android.graphics.Bitmap;

import com.yinshua.sqlitedemo.imageloader.request.BitmapRequest;

/**
 * 抽象加载器
 * Created by marc on 2017/7/11.
 */

public abstract class AbstractLoader implements Loader{
    @Override
    public void loadImage(BitmapRequest request) {

    }


    /**
     * 具体的加载实现
     * @param request
     * @return
     */
    protected abstract Bitmap onLoad(BitmapRequest request);
}
