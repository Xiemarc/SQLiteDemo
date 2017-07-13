package com.yinshua.sqlitedemo.imageloader.policy;

import com.yinshua.sqlitedemo.imageloader.request.BitmapRequest;

/**
 * 加载策略
 * Created by marc on 2017/7/11.
 */

public interface LoadPolicy {
    /**
     * 2个BitmapRequest 进行优先级比较
     *
     * @param request1
     * @param request2
     * @return 小于0，request1 < request2，大于0，request1 > request2，等于
     */
    public int compareTo(BitmapRequest request1, BitmapRequest request2);
}
