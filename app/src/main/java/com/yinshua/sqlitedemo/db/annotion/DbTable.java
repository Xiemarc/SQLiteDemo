package com.yinshua.sqlitedemo.db.annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表名
 * Created by marc on 2017/6/27.
 */

@Target(ElementType.TYPE)//应用在属性上
@Retention(RetentionPolicy.RUNTIME)//应用在运行时
public @interface DbTable {
    String value();
}
