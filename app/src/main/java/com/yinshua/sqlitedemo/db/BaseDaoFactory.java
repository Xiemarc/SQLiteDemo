package com.yinshua.sqlitedemo.db;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

/**
 * 简单工厂模式，操作数据库
 * Created by marc on 2017/6/27.
 */

public class BaseDaoFactory {
    private String sqliteDatabasePath;

    private SQLiteDatabase sqLiteDatabase;

    private static BaseDaoFactory instance = new BaseDaoFactory();

    private BaseDaoFactory() {
        //外置卡
        sqliteDatabasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/marc.db";
        openDatabase();
    }

    public static BaseDaoFactory getInstance() {
        return instance;
    }

    /**
     * @param clazz       具体实现basedao的实现类，userDao，FileDao等
     * @param entityClass 具体的java实体类，User
     * @param <T>         baseDao的实现类
     * @param <M>         具体的java实体bean
     * @return
     */
    public synchronized <T extends BaseDao<M>, M> T getDataHelper(Class<T> clazz, Class<M> entityClass) {
        BaseDao baseDao = null;
        try {
            baseDao = clazz.newInstance();
            baseDao.init(entityClass, sqLiteDatabase);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return (T) baseDao;
    }

    /**
     * 打开数据库
     */
    private void openDatabase() {
        this.sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqliteDatabasePath, null);
    }


}
