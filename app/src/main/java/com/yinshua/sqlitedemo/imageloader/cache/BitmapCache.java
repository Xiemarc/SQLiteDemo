package com.yinshua.sqlitedemo.imageloader.cache;

import android.graphics.Bitmap;

import com.yinshua.sqlitedemo.imageloader.request.BitmapRequest;

/**
 * 图片缓存
 * Created by marc on 2017/7/11.
 */

public interface BitmapCache {

    /**
     * 缓存
     * @param request
     * @param bitmap
     */
    void put(BitmapRequest request, Bitmap bitmap);

    /**
     * 获取缓存
     * @param request
     */
    void get(BitmapRequest request);

    /**
     * 移除缓存
     * @param request
     */
    void remove(BitmapRequest request);
}
