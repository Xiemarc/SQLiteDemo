package com.yinshua.sqlitedemo.bean;

import com.yinshua.sqlitedemo.db.annotion.DbField;
import com.yinshua.sqlitedemo.db.annotion.DbTable;

/**
 * Created by marc on 2017/6/27.
 */

@DbTable("tb_user")
public class User {
    @DbField("name")
    public String name;
    @DbField("password")
    public String password;

    public User() {
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
