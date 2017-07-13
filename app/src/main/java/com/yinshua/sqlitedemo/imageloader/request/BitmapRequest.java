package com.yinshua.sqlitedemo.imageloader.request;

import android.widget.ImageView;

import com.yinshua.sqlitedemo.imageloader.config.DisplayConfig;
import com.yinshua.sqlitedemo.imageloader.core.SimpleImageLoader;
import com.yinshua.sqlitedemo.imageloader.policy.LoadPolicy;
import com.yinshua.sqlitedemo.utils.MD5Utils;

import java.lang.ref.SoftReference;
import java.util.Comparator;

/**
 * Created by marc on 2017/7/11.
 */

public class BitmapRequest implements Comparator<BitmapRequest> {
    //加载策略
    private LoadPolicy loadPolicy = SimpleImageLoader.getInstance().getConfig().getLoadPolicy();
    /**
     * 编号
     */
    private int serialNo;
    //图片控件
    //当系统内存不足时，把引用的对象进行回收
    private SoftReference<ImageView> mimageViewRef;
    //图片路径
    private String imageUrl;
    //MD5的图片路径
    private String imageUriMD5;
    private DisplayConfig displayConfig = SimpleImageLoader.getInstance().getConfig().getDisplayConfig();
    //下载完成监听
    public SimpleImageLoader.ImageListener imageListener;

    public BitmapRequest(ImageView imageView, String imageUrl, DisplayConfig displayConfig,
                         SimpleImageLoader.ImageListener listener) {
        this.mimageViewRef = new SoftReference<>(imageView);
        //设置可见的ImageView的tag为，要下载的图片路径
        imageView.setTag(imageUrl);//解决图片错位
        this.imageUrl = imageUrl;
        this.imageUriMD5 = MD5Utils.toMD5(this.imageUrl);
        if (null != displayConfig) {
            this.displayConfig = displayConfig;
        }
        this.imageListener = listener;
    }

    /**
     * 优先级的确定
     *
     * @param o1
     * @param o2
     * @return
     */
    @Override
    public int compare(BitmapRequest o1, BitmapRequest o2) {
        return loadPolicy.compareTo(o1, o2);
    }

    public int getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BitmapRequest that = (BitmapRequest) o;

        if (serialNo != that.serialNo) return false;
        return loadPolicy != null ? loadPolicy.equals(that.loadPolicy) : that.loadPolicy == null;

    }

    @Override
    public int hashCode() {
        int result = loadPolicy != null ? loadPolicy.hashCode() : 0;
        result = 31 * result + serialNo;
        return result;
    }
}
