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
    private SQLiteDatabase database;
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
            database = sqLiteDatabase;
            if (entity.getAnnotation(DbTable.class) == null) {
                //如果没有使用注解，那么就直接使用实体bean对象的类名当做表名
                tableName = entity.getClass().getSimpleName();
            } else {
                //使用注解，那么就直接拿到注解的value值当做表名
                tableName = entity.getAnnotation(DbTable.class).value();//
            }
            if (!database.isOpen()) {
                return false;
            }
            if (!TextUtils.isEmpty(createTable())) {
                database.execSQL(createTable());
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
            cursor = database.rawQuery(sql, null);
            String[] columnNames = cursor.getColumnNames();//列名拿到
            Field[] colmunFields = entityClass.getDeclaredFields();
            for (Field field : colmunFields) {
                field.setAccessible(true);
            }
            //对应关系
            for (String columnName : columnNames) {
                //如果找到对应的Filed就赋值给他  User的成员变量
                Field colmunFiled = null;//列名
                for (Field field : colmunFields) {
                    String fieldName = null;
                    if (field.getAnnotation(DbField.class) != null) {//成员变量有注解
                        fieldName = field.getAnnotation(DbField.class).value();
                    } else {
                        //没有DbField注解
                        fieldName = field.getName();
                    }
                    //如果表的列名，等于了成员变量的注解名字
                    if (columnName.equals(fieldName)) {
                        colmunFiled = field;
                        break;
                    }
                }
                //找到对应关系
                if (colmunFiled != null) {
                    //表的名字，属性名字
                    cacheMap.put(columnName, colmunFiled);
                }
            }
        } catch (Exception e) {

        } finally {
            cursor.close();
        }
    }


    @Override
    public Long insert(T entity) {
        Map<String, String> map = getValues(entity);
        ContentValues values = getContentValues(map);
        long result = database.insert(tableName, null, values);
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
     * 将map转化为ContentValues
     *
     * @param map
     * @return
     */
    private ContentValues getContentValues(Map<String, String> map) {
        ContentValues contentValues = new ContentValues();
        Set<String> keys = map.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = map.get(key);
            if (value != null) {
                contentValues.put(key, value);
            }
        }
        return contentValues;
    }

    @Override
    public int update(T entity, T where) {
        int result = -1;
        // 将目标对象转化为map
        Map<String, String> values = getValues(entity);
        //将条件对象转化成map
        Map<String, String> whereClause = getValues(where);
        Condition condition = new Condition(whereClause);
        ContentValues contentValues = getContentValues(values);
        result = database.update(tableName, contentValues, condition.getWhereClause(), condition.getWhereArgs());
        return result;
    }

    /**
     * 删除
     *
     * @param where
     * @return
     */
    @Override
    public int delete(T where) {
        Map<String, String> whereValues = getValues(where);
        Condition condition = new Condition(whereValues);
        int result = database.delete(tableName, condition.getWhereClause(), condition.getWhereArgs());
        return result;
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
        Map<String, String> values = getValues(where);
        String limitString = null;//限制条件语句
        if (startIndex != null && limit != null) {
            limitString = startIndex + " , " + limit;
        }
        Condition condition = new Condition(values);
        Cursor cursor = database.query(tableName, null, condition.getWhereClause(),
                condition.getWhereArgs(), null, null, orderBy, limitString);
        List<T> result = getResult(cursor, where);
        cursor.close();
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
        public Condition(Map<String, String> whereClause) {
            ArrayList list = new ArrayList();//拼接条件值的
            StringBuilder stringBuilder = new StringBuilder();// 拼接条件语句的
            stringBuilder.append(" 1=1 ");//让它恒成立 1=1 and name = ? ……
            Set<String> keys = whereClause.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = whereClause.get(key);
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
