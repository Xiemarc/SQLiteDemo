package com.yinshua.sqlitedemo.imageloader.policy;

import com.yinshua.sqlitedemo.imageloader.request.BitmapRequest;

/**
 * 正序加载策略
 * Created by marc on 2017/7/11.
 */

public class SerialPolicy implements LoadPolicy{
    @Override
    public int compareTo(BitmapRequest request1, BitmapRequest request2) {
        return request1.getSerialNo()-request2.getSerialNo();
    }
}
