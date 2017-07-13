package com.yinshua.sqlitedemo.imageloader.cache;

import android.graphics.Bitmap;

import com.yinshua.sqlitedemo.imageloader.request.BitmapRequest;

/**
 * 硬盘缓存 支持（LRU算法）
 * Created by marc on 2017/7/11.
 */

public class DiskCache implements BitmapCache {
    @Override
    public void put(BitmapRequest request, Bitmap bitmap) {

    }

    @Override
    public void get(BitmapRequest request) {

    }

    @Override
    public void remove(BitmapRequest request) {

    }
}
