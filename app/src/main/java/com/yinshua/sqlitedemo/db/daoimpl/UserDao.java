package com.yinshua.sqlitedemo.db.daoimpl;

import com.yinshua.sqlitedemo.db.BaseDao;

import java.util.List;

/**
 * Created by marc on 2017/6/27.
 */

public class UserDao extends BaseDao {
    @Override
    protected String createTable() {
        return "create table if not exists tb_user(name varchar(20),password varchar(10))";
    }

    @Override
    public List query(String sql) {
        return null;
    }
}
