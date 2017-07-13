package com.yinshua.sqlitedemo.imageloader.policy;

import com.yinshua.sqlitedemo.imageloader.request.BitmapRequest;

/**
 * 反序加载策略
 * 后进先出
 * Created by marc on 2017/7/11.
 */

public class ReversePolicy implements LoadPolicy {

    @Override
    public int compareTo(BitmapRequest request1, BitmapRequest request2) {
        return request2.getSerialNo() - request1.getSerialNo();
    }
}
