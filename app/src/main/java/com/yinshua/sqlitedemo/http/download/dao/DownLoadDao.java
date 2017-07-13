package com.yinshua.sqlitedemo.http.download.dao;

import android.database.Cursor;

import com.yinshua.sqlitedemo.db.BaseDao;
import com.yinshua.sqlitedemo.http.download.DownloadItemInfo;
import com.yinshua.sqlitedemo.http.download.enums.DownloadStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * des:
 * author: marc
 * date:  2017/7/5 21:51
 * email：aliali_ha@yeah.net
 */

public class DownLoadDao extends BaseDao<DownloadItemInfo> {
    /**
     * 保存应该下载的集合
     * 不包括已经下载成功的
     */
    private List<DownloadItemInfo> downloadItemInfoList = Collections.synchronizedList(new ArrayList<DownloadItemInfo>());//线程安全的
    private DownloadInfoComparator downloadInfoComparator = new DownloadInfoComparator();

    @Override
    protected String createTable() {
        return "create table if not exists  t_downloadInfo(" + "id Integer primary key, " + "url TEXT not null," +
                "filePath TEXT not null, " + "displayName TEXT, " + "status Integer, " + "totalLen Long, " + "currentLen Long," +
                "startTime TEXT," + "finishTime TEXT," + "userId TEXT, " + "httpTaskType TEXT," + "priority  Integer," +
                "stopMode Integer," + "downloadMaxSizeKey TEXT," + "unique(filePath))";
    }


    @Override
    public List<DownloadItemInfo> query(String sql) {

        return null;
    }

    /**
     * 生成下载id
     *
     * @return 返回下载id
     */
    private Integer generateRecordId() {
        int maxId = 0;
        String sql = "select max(id)  from " + getTableName();
        synchronized (DownLoadDao.class) {
            Cursor cursor = this.sqLiteDatabase.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                String[] colmName = cursor.getColumnNames();

                int index = cursor.getColumnIndex("max(id)");
                if (index != -1) {
                    Object value = cursor.getInt(index);
                    if (value != null) {
                        maxId = Integer.parseInt(String.valueOf(value));
                    }
                }
            }

        }
        return maxId + 1;
    }

    /**
     * 下载文件路径查找下载记录
     *
     * @param path
     * @return
     */
    public List<DownloadItemInfo> findRecord(String path) {
        synchronized (DownLoadDao.class) {
            DownloadItemInfo where = new DownloadItemInfo();//新建条件对象
            where.setFilePath(path);
            List<DownloadItemInfo> resutList = super.query(where);
            return resutList;
        }
    }

    /**
     * 根据url和路径查找下载记录
     *
     * @param url
     * @param filePath
     * @return
     */
    public DownloadItemInfo findRecord(String url, String filePath) {
        synchronized (DownLoadDao.class) {
            for (DownloadItemInfo record : downloadItemInfoList) {
                if (record.equals(url) && record.getFilePath().equals(filePath)) {
                    return record;
                }
            }
            //内存集合中找不到就从数据库中查找
            DownloadItemInfo where = new DownloadItemInfo();
            where.setUrl(url);
            where.setFilePath(filePath);
            List<DownloadItemInfo> resultList = super.query(where);
            if (resultList.size() > 0) {
                return resultList.get(0);
            }
        }
        return null;
    }

    /**
     * 添加下载记录
     *
     * @param url         下载地址
     * @param filePath    下载文件路径
     * @param displayName 文件显示名
     * @param priority    小组优先级
     *                    TODO
     * @return 下载id
     */
    public int addRecord(String url, String filePath, String displayName, int priority) {
        synchronized (DownLoadDao.class) {
            DownloadItemInfo existDownloadInfo = findRecord(url, filePath);
            //如果没有找到现在记录，就新建个下载记录
            if (existDownloadInfo == null) {
                DownloadItemInfo record = new DownloadItemInfo();
                record.setId(generateRecordId());
                record.setUrl(url);
                record.setFilePath(filePath);
                record.setDisplayName(displayName);
                record.setStatus(DownloadStatus.waitting.getValue());
                record.setTotalLen(0L);
                record.setCurrentLen(0L);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                record.setStartTime(dateFormat.format(new Date()));
                record.setFinishTime("0");
                record.setPriority(priority);
                super.insert(record);
                downloadItemInfoList.add(record);
                return record.getId();
            }
            return -1;
        }
    }

    public int updateRecord(DownloadItemInfo record) {
        DownloadItemInfo where = new DownloadItemInfo();
        where.setId(record.getId());
        int result = 0;
        synchronized (DownLoadDao.class) {
            try {
                result = super.update(record, where);
            } catch (Throwable e) {

            }
            if (result > 0) {
                for (int i = 0; i < downloadItemInfoList.size(); i++) {
                    if (downloadItemInfoList.get(i).getId().intValue() == record.getId()) {
                        downloadItemInfoList.set(i, record);
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * 根据id从内存中移除下载记录
     *
     * @param id 下载id
     * @return true标示删除成功，否则false
     */
    public boolean removeRecordFromMemery(int id) {
        synchronized (DownloadItemInfo.class) {
            for (int i = 0; i < downloadItemInfoList.size(); i++) {
                if (downloadItemInfoList.get(i).getId() == id) {
                    downloadItemInfoList.remove(i);
                    break;
                }
            }
            return true;
        }
    }

    /**
     * 根据下载地址和下载文件路径查找下载记录
     * 下载地址
     *
     * @param filePath 下载文件路径
     * @return
     */
    public DownloadItemInfo findSigleRecord(String filePath) {
        List<DownloadItemInfo> downloadInfoList = findRecord(filePath);
        if (downloadInfoList.isEmpty()) {
            return null;
        }
        return downloadInfoList.get(0);
    }

    /**
     * 通过下载地址查找对象
     *
     * @param url
     * @return
     */
    public DownloadItemInfo findByUrl(String url) {
        DownloadItemInfo where = new DownloadItemInfo();
        where.setUrl(url);
        List<DownloadItemInfo> resultList = super.query(where);
        if (resultList.isEmpty()) {
            return null;
        }
        return resultList.get(0);
    }

    /**
     * 根据id查找记录对象
     *
     * @param id 下载任务id
     * @return
     */
    public DownloadItemInfo findRecordById(Integer id) {
        synchronized (DownLoadDao.class) {
            for (DownloadItemInfo record : downloadItemInfoList) {
                if (record.getId() == id) {
                    return record;
                }
            }
            DownloadItemInfo where = new DownloadItemInfo();
            where.setId(id);
            List<DownloadItemInfo> resultList = super.query(where);
            if (resultList.size() > 0) {
                return resultList.get(0);
            }
            return null;
        }
    }


    class DownloadInfoComparator implements Comparator<DownloadItemInfo> {

        @Override
        public int compare(DownloadItemInfo lhs, DownloadItemInfo rhs) {
            return rhs.getId() - lhs.getId();
        }
    }
}
