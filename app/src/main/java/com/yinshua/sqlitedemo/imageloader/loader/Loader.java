package com.yinshua.sqlitedemo.imageloader.loader;

import com.yinshua.sqlitedemo.imageloader.request.BitmapRequest;

/**
 * 加载图片接口
 * Created by marc on 2017/7/11.
 */

public interface Loader {

    void loadImage(BitmapRequest request);
}
