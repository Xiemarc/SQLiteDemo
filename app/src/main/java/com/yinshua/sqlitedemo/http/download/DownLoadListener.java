package com.yinshua.sqlitedemo.http.download;

import android.os.Handler;
import android.os.Looper;

import com.yinshua.sqlitedemo.http.download.enums.DownloadStatus;
import com.yinshua.sqlitedemo.http.download.interfaces.IDownListener;
import com.yinshua.sqlitedemo.http.download.interfaces.IDownloadServiceCallable;
import com.yinshua.sqlitedemo.http.interfaces.IHttpService;

import org.apache.http.HttpEntity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 下载任务listener
 * Created by marc on 2017/7/5.
 */

public class DownLoadListener implements IDownListener {
    private DownloadItemInfo downloadItemInfo;//封装的下载
    private File file;//文件
    protected String url;//下载地址
    private long breakPoint;
    private IDownloadServiceCallable downloadServiceCallable;
    private IHttpService httpService;

    Handler handler = new Handler(Looper.getMainLooper());//得到主线程

    public DownLoadListener(DownloadItemInfo downloadItemInfo, IDownloadServiceCallable downloadServiceCallable,
                            IHttpService httpService) {
        this.downloadItemInfo = downloadItemInfo;
        this.downloadServiceCallable = downloadServiceCallable;
        this.httpService = httpService;
        this.file = new File(downloadItemInfo.getFilePath());
        this.breakPoint = file.length();//已经下载的长度
    }


    public DownLoadListener(DownloadItemInfo downloadItemInfo) {
        this.downloadItemInfo = downloadItemInfo;
    }


    @Override
    public void setHttpService(IHttpService httpService) {
        this.httpService = httpService;
    }

    /**
     * 设置取消接口
     */
    @Override
    public void setCancleCanable() {

    }

    /**
     * 设置暂停接口
     */
    @Override
    public void setPauseCanalbe() {

    }

    @Override
    public void onSuccess(HttpEntity httpEntity) {
        InputStream inputStream = null;
        try {
            inputStream = httpEntity.getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long startTime = System.currentTimeMillis();
        //用于计算每秒速度
        long speed = 0;
        long useTime = 0;
        //下载的长度
        long getLen = 0L;
        //接受的长度
        long receiveLen = 0L;
        boolean bufferLen = false;
        //得到下载的长度
        long dataLength = httpEntity.getContentLength();
        //单位时间下载的字节数
        long calcSpeedLen = 0L;
        //总数
        long totalLength = this.breakPoint + dataLength;
        //更新数量
        this.receviceTotalLength(totalLength);
        //更新状态,把状态设置成下载状态
        this.downloadStatusChange(DownloadStatus.downloading);
        byte[] buffer = new byte[1024];
        int count = 0;
        long currentTime = System.currentTimeMillis();
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            if (!makeDir(this.getFile().getParentFile())) {
                downloadServiceCallable.onDownloadError(downloadItemInfo, 1, "创建文件夹失败");
            } else {
                fos = new FileOutputStream(this.getFile(), true);//追加模式，不覆盖
                bos = new BufferedOutputStream(fos);
                int length = 1;
                while ((length = inputStream.read(buffer)) != -1) {
                    if (this.getHttpService().isCancle()) {
                        //用户取消
                        downloadServiceCallable.onDownloadError(downloadItemInfo, 1, "用户取消了");
                        return;
                    }
                    if (this.getHttpService().isPause()) {
                        //用户暂停了
                        downloadServiceCallable.onDownloadPause(downloadItemInfo);
                        return;
                    }
                    bos.write(buffer, 0, length);
                    getLen += (long) length;
                    receiveLen += (long) length;
                    calcSpeedLen += (long) length;
                    ++count;
                    if (receiveLen * 10L / totalLength >= 1L || count >= 5000) {
                        currentTime = System.currentTimeMillis();
                        useTime = currentTime - startTime;
                        startTime = currentTime;
                        speed = 1000L * calcSpeedLen / useTime;
                        count = 0;
                        calcSpeedLen = 0L;
                        receiveLen = 0L;
                        this.downloadLengthChange(this.breakPoint + getLen, totalLength, speed);
                    }
                }
                bos.close();
                inputStream.close();
                if (dataLength != getLen) {
                    downloadServiceCallable.onDownloadError(downloadItemInfo, 3, "下载长度不相等");
                } else {
                    this.downloadLengthChange(this.breakPoint + getLen, totalLength, speed);
                    this.downloadServiceCallable.onDownloadSuccess(downloadItemInfo.copy());
                }
            }
        } catch (IOException ioException) {
            if (this.getHttpService() != null) {
//                this.getHttpService().abortRequest();
            }
            return;
        } catch (Exception e) {
            if (this.getHttpService() != null) {
//                this.getHttpService().abortRequest();
            }
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (httpEntity != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onFail() {

    }

    @Override
    public void addHttpHeader(Map<String, String> headerMap) {
        long length = getFile().length();
        if (length > 0L) {
            //断点下载
            headerMap.put("RANGE", "bytes=" + length + "-");
        }
    }

    /**
     * 创建文件夹
     *
     * @param parentFile
     * @return
     */
    private boolean makeDir(File parentFile) {
        return parentFile.exists() && !parentFile.isFile() ?
                parentFile.exists() && parentFile.isDirectory() :
                parentFile.mkdirs();
    }

    /**
     * 下载长度变化监听回调
     *
     * @param downlength  当前下载的文件长度
     * @param totalLength 文件总长度
     * @param speed       下载速度
     */
    private void downloadLengthChange(final long downlength, final long totalLength, final long speed) {
        downloadItemInfo.setCurrentLength(downlength);
        if (downloadServiceCallable != null) {
            final DownloadItemInfo copyDownloadItemInfo = downloadItemInfo.copy();
            synchronized (this.downloadServiceCallable) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
//                        downloadServiceCallable.onCurrentSizeChanged(copyDownloadItemInfo, downlength / totalLength, speed);
                        downloadServiceCallable.onCurrentSizeChanged(copyDownloadItemInfo, downlength, speed);
                    }
                });
            }
        }
    }

    /**
     * 更改下载状态
     *
     * @param downloading
     */
    private void downloadStatusChange(DownloadStatus downloading) {
        downloadItemInfo.setStatus(downloading.getValue());
        final DownloadItemInfo copyDownloadItemInfo = downloadItemInfo.copy();
        if (downloadServiceCallable != null) {
            synchronized (this.downloadServiceCallable) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        downloadServiceCallable.onDownloadStatusChanged(copyDownloadItemInfo);
                    }
                });
            }
        }
    }

    /**
     * 回调 长度的变化
     *
     * @param totalLength
     */
    private void receviceTotalLength(long totalLength) {
        downloadItemInfo.setTotalLength(totalLength);
        final DownloadItemInfo copyDownloadItemInfo = downloadItemInfo.copy();
        if (downloadServiceCallable != null) {
            synchronized (this.downloadServiceCallable) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        downloadServiceCallable.onTotalLengthReceived(copyDownloadItemInfo);
                    }
                });
            }
        }
    }

    public IHttpService getHttpService() {
        return httpService;
    }

    public File getFile() {
        return file;
    }
}
