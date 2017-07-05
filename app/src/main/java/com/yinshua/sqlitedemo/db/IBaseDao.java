package com.yinshua.sqlitedemo.db;

import java.util.List;

/**
 * Created by marc on 2017/6/27.
 */

public interface IBaseDao<T> {
    /**
     * 插入数据
     *
     * @param entity
     * @return
     */
    Long insert(T entity);

    /**
     * 更新数据
     *
     * @param entity
     * @param where
     * @return
     */
    int update(T entity, T where);

    /**
     * 删除数据
     *
     * @param where
     * @return
     */
    int delete(T where);

    /**
     * 查询数据
     *
     * @param where 条件对象
     * @return
     */
    List<T> query(T where);

    /**
     * 查询数据
     * @param where 条件对象
     * @param orderBy 排序
     * @param startIndex 第几条数据
     * @param limit 限制几条数据
     * @return
     */
    List<T> query(T where, String orderBy, Integer startIndex, Integer limit);

    /**
     * 交给子类自己实现
     * @param sql 查询sql语句
     */
    List<T> query(String sql);
}
