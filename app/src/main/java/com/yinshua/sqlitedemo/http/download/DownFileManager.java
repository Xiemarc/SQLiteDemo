package com.yinshua.sqlitedemo.http.download;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.yinshua.sqlitedemo.db.BaseDaoFactory;
import com.yinshua.sqlitedemo.http.HttpTask;
import com.yinshua.sqlitedemo.http.RequestHodler;
import com.yinshua.sqlitedemo.http.download.dao.DownLoadDao;
import com.yinshua.sqlitedemo.http.download.enums.DownloadStatus;
import com.yinshua.sqlitedemo.http.download.enums.DownloadStopMode;
import com.yinshua.sqlitedemo.http.download.enums.Priority;
import com.yinshua.sqlitedemo.http.download.interfaces.IDownloadCallable;
import com.yinshua.sqlitedemo.http.download.interfaces.IDownloadServiceCallable;
import com.yinshua.sqlitedemo.http.interfaces.IHttpListener;
import com.yinshua.sqlitedemo.http.interfaces.IHttpService;
import com.yinshua.sqlitedemo.utils.logger.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 下载管理控制器
 * Created by marc on 2017/7/5.
 */

public class DownFileManager implements IDownloadServiceCallable {
    private static final String TAG = "marc";
    private byte[] lock = new byte[0];
    DownLoadDao downLoadDao = BaseDaoFactory.getInstance().getDataHelper(DownLoadDao.class, DownloadItemInfo.class);
    //时间格式化
    SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    /**
     * 观察者模式
     */
    private final List<IDownloadCallable> applisteners = new CopyOnWriteArrayList<>();
    /**
     * 正在下载的所有任务
     */
    private static List<DownloadItemInfo> downloadFileTaskList = new CopyOnWriteArrayList();
    /**
     * 主线程handler
     */
    Handler handler = new Handler(Looper.getMainLooper());

    /**
     * 针对调用层只传url
     *
     * @param url 下载地址
     * @return 下载任务id ,返回-1出错
     */
    public int download(String url) {
        String[] preFix = url.split("/");
        return this.download(url, Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + preFix[preFix.length - 1]);
    }

    /**
     * 针对调用层只传递url和路径
     *
     * @param url  下载地址
     * @param path 保存路径
     * @return 下载任务id ,返回-1出错
     */
    public int download(String url, String path) {
        String[] preFix = url.split("/");
        String displayName = preFix[preFix.length - 1];
        return this.download(url, path, displayName);
    }

    /**
     * 针对调用层只传递url、文件路径和名字的下载任务
     *
     * @param url         下载地址
     * @param filePath    文件路径
     * @param displayName 文件名称
     * @return 下载任务id ,返回-1出错
     */
    public int download(String url, String filePath, String displayName) {
        return this.download(url, filePath, displayName, Priority.middle);
    }

    /**
     * 下载任务
     *
     * @param url         下载地址
     * @param filePath    下载路径
     * @param displayName 文件名称
     * @param priority    下载优先级别
     * @return 下载任务id ,返回-1出错
     */
    public int download(String url, String filePath,
                        String displayName, Priority priority) {
        if (priority == null) {
            priority = Priority.low;
        }
        File file = new File(filePath);
        DownloadItemInfo downloadItemInfo = null;
        //检查数据库 是否有下载记录
        downloadItemInfo = downLoadDao.findRecord(url, filePath);
        if (downloadItemInfo == null) {
            //没有下载,根据文件路径查找，有没有记录
            List<DownloadItemInfo> samesFile = downLoadDao.findRecord(filePath);
            if (samesFile.size() > 0) {
                //大于0 表示下载
                DownloadItemInfo sameDown = samesFile.get(0);
                if (sameDown.getCurrentLen() == sameDown.getTotalLen()) {
                    synchronized (applisteners) {
                        for (IDownloadCallable downloadcallable : applisteners) {
                            downloadcallable.onDownloadError(sameDown.getId(), 2, "文件已经下载了");
                        }
                    }
                }
            }
            //插入数据库
            int recrodId = downLoadDao.addRecord(url, filePath, displayName, priority.getValue());
            if (recrodId != -1) {
                //插入数据库成功
                synchronized (applisteners) {
                    for (IDownloadCallable downloadcallable : applisteners) {
                        //通知应用层 被添加了
                        downloadcallable.onDownloadInfoAdd(recrodId);
                        downloadItemInfo = downLoadDao.findRecordById(recrodId);
                    }
                }
            } else {
                downloadItemInfo = downLoadDao.findRecord(url, filePath);//再查询一遍
            }
        }
        //是否正在下载
        if (isDowning(file.getAbsolutePath())) {
            synchronized (applisteners) {
                //当前正在下载，通知被观察者正在下载
                for (IDownloadCallable downloadcalable : applisteners) {
                    reallyDown(downloadItemInfo);
                    downloadcalable.onDownloadError(downloadItemInfo.getId(), 4, "正在下载，请勿重复添加");
                }
            }
            return downloadItemInfo.getId();
        }
        if (downloadItemInfo != null) {
            downloadItemInfo.setPriority(priority.getValue());
            downloadItemInfo.setStopMode(DownloadStopMode.auto.getValue());

            //判断数据库存的 状态是否是完成
            if (downloadItemInfo.getStatus() != DownloadStatus.finish.getValue()) {
                if (downloadItemInfo.getTotalLen() == 0L || file.length() == 0L) {
                    Logger.i(TAG, "还未开始下载");
                    downloadItemInfo.setStatus(DownloadStatus.failed.getValue());
                }
                //判断数据库中 总长度是否等于文件长度
                if (downloadItemInfo.getTotalLen() == file.length() && downloadItemInfo.getTotalLen() != 0) {
                    downloadItemInfo.setStatus(DownloadStatus.finish.getValue());//把状态设置成已完成
                    synchronized (applisteners) {
                        for (IDownloadCallable downloadCallable : applisteners) {
                            try {
                                downloadCallable.onDownloadError(downloadItemInfo.getId(), 4, "已经下载了");
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            } else {
                if (!file.exists() || (downloadItemInfo.getTotalLen() != downloadItemInfo.getCurrentLen())) {
                    downloadItemInfo.setStatus(DownloadStatus.failed.getValue());
                }
            }
            //更新数据库
            downLoadDao.updateRecord(downloadItemInfo);

            if (downloadItemInfo.getStatus() == DownloadStatus.finish.getValue()) {
                Logger.i(TAG, "已经下载完成  回调应用层");
                final int downId = downloadItemInfo.getId();
                synchronized (applisteners) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            for (IDownloadCallable downloadCallable : applisteners) {
                                downloadCallable.onDownloadStatusChanged(downId, DownloadStatus.finish);
                            }
                        }
                    });
                }
                downLoadDao.removeRecordFromMemery(downId);
                return downloadItemInfo.getId();
            }
            //之前的下载 状态为暂停状态
            List<DownloadItemInfo> allDowning = DownFileManager.downloadFileTaskList;
            //当前下载不是最高级  则先退出下载
            if (priority != Priority.high) {
                for (DownloadItemInfo downling : allDowning) {
                    //从下载表中  获取到全部正在下载的任务
                    downling = downLoadDao.findSigleRecord(downling.getFilePath());
                    if (downling != null && downling.getPriority() == Priority.high.getValue()) {
                        // 当前下载级别不是最高级 传进来的是middle    但是在数据库中查到路径一模一样 的记录   所以他也是最高级------------------------------
                        // 比如 第一次下载是用最高级下载，app闪退后，没有下载完成，第二次传的是默认级别，这样就应该是最高级别下载
                        if (downling.getFilePath().equals(downloadItemInfo.getFilePath())) {
                            break;
                        } else {
                            return downloadItemInfo.getId();
                        }
                    }
                }
            }
            reallyDown(downloadItemInfo);
            if (priority == Priority.high || priority == Priority.middle) {
                synchronized (allDowning) {
                    for (DownloadItemInfo downloadItemInfo1 : allDowning) {
                        if (!downloadItemInfo.getFilePath().equals(downloadItemInfo1.getFilePath())) {
                            DownloadItemInfo downingInfo = downLoadDao.findSigleRecord(downloadItemInfo1.getFilePath());
                            if (downingInfo != null) {
                                pause(downloadItemInfo.getId(), DownloadStopMode.auto);
                            }
                        }
                    }
                }
                return downloadItemInfo.getId();
            }
        }
        return -1;
    }

    /**
     * 停止任务
     *
     * @param id   下载id
     * @param mode 停止类型
     */
    public void pause(Integer id, DownloadStopMode mode) {
        if (mode == null) {
            mode = DownloadStopMode.auto;
        }
        DownloadItemInfo downloadInfo = downLoadDao.findRecordById(id);
        if (downloadInfo != null) {
            // 更新停止状态
            if (downloadInfo != null) {
                downloadInfo.setStopMode(mode.getValue());
                downloadInfo.setStatus(DownloadStatus.pause.getValue());
                downLoadDao.updateRecord(downloadInfo);
            }
            for (DownloadItemInfo downing : downloadFileTaskList) {
                if (id == downing.getId()) {
                    downing.getHttpTask().pause();
                }
            }
        }
    }

    /**
     * 判断当前下载任务是否正在下载
     *
     * @param absolutePath 下载文件的绝对路径
     * @return true, 正在下载， false 没有在下载
     */
    private boolean isDowning(String absolutePath) {
        for (DownloadItemInfo downloaditeminfo : downloadFileTaskList) {
            if (downloaditeminfo.getFilePath().equals(absolutePath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 给调用层暴露，添加观察者
     *
     * @param downloadcalable 观察者
     */
    public void setDownCallable(IDownloadCallable downloadcalable) {
        synchronized (applisteners) {
            applisteners.add(downloadcalable);
        }
    }

    /**
     * 真正开始下载
     *
     * @param downloadItemInfo 封装的下载实体类
     */
    public DownloadItemInfo reallyDown(DownloadItemInfo downloadItemInfo) {
        synchronized (lock) {
            //实例化DownloadItem
            RequestHodler requestHodler = new RequestHodler();
            //设置请求下载的策略
            IHttpService httpService = new FileDownHttpService();
            //得到请求头的参数 map
            Map<String, String> map = httpService.getHttpHeadMap();
            /**
             * 处理结果的策略
             */
            IHttpListener httpListener = new DownLoadListener(downloadItemInfo, this, httpService);
            requestHodler.setHttpListener(httpListener);
            requestHodler.setHttpService(httpService);
            /**
             *  bug  url
             */
            requestHodler.setUrl(downloadItemInfo.getUrl());
            HttpTask httpTask = new HttpTask(requestHodler);
            downloadItemInfo.setHttpTask(httpTask);

            /**
             * 添加
             */
            downloadFileTaskList.add(downloadItemInfo);
            httpTask.start();
        }
        return downloadItemInfo;
    }


    @Override
    public void onDownloadStatusChanged(DownloadItemInfo downloadItemInfo) {
        DownloadItemInfo downloadinfo = downLoadDao.findSigleRecord(downloadItemInfo.getFilePath());
        if (downloadinfo != null) {
            downloadinfo.setStatus(downloadItemInfo.getStatus());
            downLoadDao.updateRecord(downloadinfo);
            synchronized (applisteners) {
                for (IDownloadCallable downloadcallable : applisteners) {
                    downloadcallable.onDownloadStatusChanged(downloadinfo.getId(), DownloadStatus.starting);
                }
            }
        }
    }

    @Override
    public void onTotalLengthReceived(DownloadItemInfo downloadItemInfo) {
        for (IDownloadCallable callable : applisteners) {
            Logger.i(TAG, "获取到文件总长度  " + downloadItemInfo.getFilePath() + "  url " + downloadItemInfo.getUrl());
            callable.onTotalLengthReceived(downloadItemInfo.getId(), downloadItemInfo.getTotalLength());
        }
    }

    @Override
    public void onCurrentSizeChanged(DownloadItemInfo downloadItemInfo, float downLenth, long speed) {
        for (IDownloadCallable callable : applisteners) {
            Logger.i(TAG, "正在下载    路径  " + downloadItemInfo.getFilePath() + "  url " + downloadItemInfo.getUrl());
            callable.onCurrentSizeChanged(downloadItemInfo.getId(), downLenth, speed);
        }
    }

    @Override
    public void onDownloadSuccess(DownloadItemInfo downloadItemInfo) {
        for (IDownloadCallable callable : applisteners) {
            Logger.i(TAG, "下载成功    路径  " + downloadItemInfo.getFilePath() + "  url " + downloadItemInfo.getUrl());
            callable.onDownloadSuccess(downloadItemInfo.getId());
        }
    }

    @Override
    public void onDownloadPause(DownloadItemInfo downloadItemInfo) {
        for (IDownloadCallable callable : applisteners) {
            Logger.i(TAG, "暂停   路径  ");
            callable.onDownloadStatusChanged(downloadItemInfo.getId(), null);
        }
    }

    @Override
    public void onDownloadError(DownloadItemInfo downloadItemInfo, int var2, String var3) {
        Logger.i(TAG, "下载出错   " + var3);
    }
}
