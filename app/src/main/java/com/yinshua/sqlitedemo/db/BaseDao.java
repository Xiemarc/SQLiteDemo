package com.yinshua.sqlitedemo.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.yinshua.sqlitedemo.db.annotion.DbField;
import com.yinshua.sqlitedemo.db.annotion.DbTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * T 代表了要操作的数据类型
 * Created by marc on 2017/6/27.
 */

public abstract class BaseDao<T> implements IBaseDao<T> {
    /**
     * ]
     * 持有数据库操作类的引用
     */
    protected SQLiteDatabase sqLiteDatabase;
    /**
     * 保证实例化一次
     */
    private boolean isInit = false;
    /**
     * 持有操作数据库表所对应的java类型
     */
    private Class<T> entityClass;
    /**
     * 维护这表名与成员变量名的映射关系
     * key---》表名
     * value --》Field
     */
    private HashMap<String, Field> cacheMap;
    private String tableName;

    /**
     * @param entity         具体的java实体bean对象
     * @param sqLiteDatabase
     * @return
     */
    protected synchronized boolean init(Class<T> entity, SQLiteDatabase sqLiteDatabase) {
        if (!isInit) {
            //如果未实例化
            entityClass = entity;
            this.sqLiteDatabase = sqLiteDatabase;
            if (entity.getAnnotation(DbTable.class) == null) {
                //如果没有使用注解，那么就直接使用实体bean对象的类名当做表名
                tableName = entity.getClass().getSimpleName();
            } else {
                //使用注解，那么就直接拿到注解的value值当做表名
                tableName = entity.getAnnotation(DbTable.class).value();//
            }
            if (!sqLiteDatabase.isOpen()) {
                return false;
            }
            if (!TextUtils.isEmpty(createTable())) {
                sqLiteDatabase.execSQL(createTable());
            }
            cacheMap = new HashMap<>();
            initCacheMap();
            isInit = true;
        }
        return isInit;
    }

    /**
     * 维护映射关系
     */
    private void initCacheMap() {
        String sql = "select * from " + this.tableName + " limit 1 , 0";//查询一条，看下有数据没
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery(sql, null);
            /**
             * 表的列名数组
             */
            String[] columnNames = cursor.getColumnNames();
            /**
             * 拿到Filed数组
             */
            Field[] columnFields = entityClass.getDeclaredFields();
            for (Field filed : columnFields) {
                filed.setAccessible(true);

                Field columnFiled = null;
                String colmunName = null;
                /**
                 * 开始找对应关系
                 */
                for (String cn : columnNames) {
                    String filedName = null;
                    if (filed.getAnnotation(DbField.class) != null) {
                        filedName = filed.getAnnotation(DbField.class).value();
                    } else {
                        filedName = filed.getName();
                    }
                    if (cn.equals(filedName)) {
                        columnFiled = filed;
                        colmunName = cn;
                        break;
                    }
                }
                //找到了对应关系
                if (columnFiled != null) {
                    cacheMap.put(colmunName, columnFiled);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public Long insert(T entity) {
        ContentValues values = getContentValues(entity);
        long result = sqLiteDatabase.insert(tableName, null, values);
        return result;
    }


    /**
     * 将java实体对象转化为HashMap
     *
     * @param entity
     * @return
     */
    private Map<String, String> getValues(T entity) {
        HashMap<String, String> result = new HashMap<>();
        Iterator<Field> fieldsIterator = cacheMap.values().iterator();
        //循环遍历映射map的Field
        while (fieldsIterator.hasNext()) {
            Field colmunToField = fieldsIterator.next();
            String cacheKey = null;
            String cacheValue = null;
            if (colmunToField.getAnnotation(DbField.class) != null) {
                //属性使用注解
                cacheKey = colmunToField.getAnnotation(DbField.class).value();//user的password属性
            } else {
                cacheKey = colmunToField.getName();
            }
            try {
                if (null == colmunToField.get(entity)) {
                    continue;
                }
                cacheValue = colmunToField.get(entity).toString();//具体的值,password的值
            } catch (Exception e) {
                e.printStackTrace();
            }
            result.put(cacheKey, cacheValue);
        }
        return result;
    }

    /**
     * 将
     *
     * @param entity
     * @return
     */
    private ContentValues getContentValues(T entity) {
        ContentValues contentValues = new ContentValues();
        try {
            for (Map.Entry<String, Field> me : cacheMap.entrySet()) {
                if (me.getValue().get(entity) == null) {
                    continue;
                }
                contentValues.put(me.getKey(), me.getValue().get(entity).toString());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return contentValues;
    }

    @Override
    public int update(T entity, T where) {
        ContentValues contentValues = getContentValues(entity);
        Condition condition = new Condition(getContentValues(where));
        int update = sqLiteDatabase.update(tableName, contentValues, condition.whereClause, condition.whereArgs);
        return update;
    }

    /**
     * 删除
     *
     * @param where
     * @return
     */
    @Override
    public int delete(T where) {
        ContentValues whereValues = getContentValues(where);
        Condition condition = new Condition(whereValues);
        int delete = sqLiteDatabase.delete(tableName, condition.getWhereClause(), condition.getWhereArgs());
        return delete;
    }

    /**
     * 查询
     *
     * @param where 条件对象
     * @return
     */
    @Override
    public List query(T where) {
        return query(where, null, null, null);
    }

    /**
     * 查询数据
     *
     * @param where      条件对象
     * @param orderBy    排序
     * @param startIndex 第几条数据
     * @param limit      限制几条数据
     * @return
     */
    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        String limitString = null;
        if (startIndex != null && limit != null) {
            limitString = startIndex + "," + limit;
        }
        Condition condition = new Condition(getContentValues(where));
        Cursor cursor = null;
        List<T> result = new ArrayList<>();
        try {
            cursor = sqLiteDatabase.query(tableName, null, condition.getWhereClause(), condition.whereArgs, null, null, orderBy, limitString);
            result = getResult(cursor, where);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    /**
     * 查询结果
     *
     * @param cursor
     * @param where
     * @return
     */
    private List<T> getResult(Cursor cursor, T where) {
        ArrayList list = new ArrayList();
        Object item;
        while (cursor.moveToNext()) {
            try {
                item = where.getClass().newInstance();
                Iterator<Map.Entry<String, Field>> iterator = cacheMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Field> entry = iterator.next();
                    String colomunName = entry.getKey();// 第一步拿到的表的列名
                    int columnIndex = cursor.getColumnIndex(colomunName);//字段名在数据库的位置
                    Field field = entry.getValue();
                    Class<?> type = field.getType();//拿到属性的类型
                    if (columnIndex != -1) {//等于-1 没有查到
                        if (type == String.class) {
                            //反射方式赋值
                            field.set(item, cursor.getString(columnIndex));
                        } else if (type == Double.class) {
                            field.set(item, cursor.getDouble(columnIndex));
                        } else if (type == Integer.class) {
                            field.set(item, cursor.getInt(columnIndex));
                        } else if (type == Long.class) {
                            field.set(item, cursor.getLong(columnIndex));
                        } else if (type == byte[].class) {
                            //字符数组，可以存图片 什么的
                            field.set(item, cursor.getBlob(columnIndex));
                        } else {
                            //不支持类型，跳过本次循环，进入下次循环
                            continue;
                        }
                    }
                }
                list.add(item);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return list;
    }


    /**
     * 封装修改语句
     */
    class Condition {
        public Condition(ContentValues whereClause) {
            ArrayList list = new ArrayList();//拼接条件值的
            StringBuilder stringBuilder = new StringBuilder();// 拼接条件语句的
            stringBuilder.append(" 1=1 ");//让它恒成立 1=1 and name = ? ……
            Set<String> keys = whereClause.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = (String) whereClause.get(key);
                if (null != value) {
                    //拼接条件查询语句 1=1 and name = ? and password = ?
                    stringBuilder.append(" and " + key + " =?");
                    list.add(value);
                }
            }
            //条件语句拼接完成
            this.whereClause = stringBuilder.toString();
            this.whereArgs = (String[]) list.toArray(new String[list.size()]);
        }

        /**
         * 查询条件
         * name=? && password=?
         */
        private String whereClause;
        /**
         * 查询参数
         */
        private String[] whereArgs;

        public String getWhereClause() {
            return whereClause;
        }

        public String[] getWhereArgs() {
            return whereArgs;
        }
    }

    /**
     * 交给子类具体实现，分别实现UserDao，FileDao等
     *
     * @return
     */
    protected abstract String createTable();
}
