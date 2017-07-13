package com.yinshua.sqlitedemo.http.download.enums;

/**
 * des:下载权限
 * author: marc
 * date:  2017/7/5 22:17
 * email：aliali_ha@yeah.net
 */

public enum Priority {
    /**
     * 手动下载的优先级
     */
    low(0),
    /**
     * 默认下载的优先ji
     */
    middle(1),
    /**
     * 主动推送资源的优先级
     */
    high(2);


    private int value;

    Priority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    public static Priority getInstance(int value)
    {
        for (Priority priority : Priority.values())
        {
            if (priority.getValue() == value)
            {
                return priority;
            }
        }
        return Priority.middle;
    }
}
