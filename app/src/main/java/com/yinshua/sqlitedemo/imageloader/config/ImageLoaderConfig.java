package com.yinshua.sqlitedemo.imageloader.config;

import com.yinshua.sqlitedemo.imageloader.cache.BitmapCache;
import com.yinshua.sqlitedemo.imageloader.policy.LoadPolicy;

/**
 * 配置
 * 采用建造者模式
 * Created by marc on 2017/7/11.
 */

public class ImageLoaderConfig {
    //缓存策略
    private BitmapCache bitmapCache;
    //加载策略
    private LoadPolicy loadPolicy;
    //默认值，根据cpu数给定默认线程数
    private int threadCount = Runtime.getRuntime().availableProcessors();
    private DisplayConfig displayConfig = new DisplayConfig();

    private ImageLoaderConfig() {

    }

    /**
     * 建造者模式
     * 和alertdialog建造过程类似
     */
    public static class Builder {
        private ImageLoaderConfig config;

        public Builder() {
            config = new ImageLoaderConfig();
        }

        /**
         * 设置缓存策略
         *
         * @param bitmapCache
         * @return
         */
        public Builder setCachePolicy(BitmapCache bitmapCache) {
            config.bitmapCache = bitmapCache;
            return this;
        }

        /**
         * 设置加载策略
         *
         * @param loadPolicy
         * @return
         */
        public Builder setLoadPolicy(LoadPolicy loadPolicy) {
            config.loadPolicy = loadPolicy;
            return this;
        }

        /**
         * 设置线程个数
         *
         * @param count
         * @return
         */
        public Builder setThreadCount(int count) {
            config.threadCount = count;
            return this;
        }

        /**
         * 图片加载过程中显示的图片
         *
         * @param resId
         * @return
         */
        public Builder setLoadingPlaceHolder(int resId) {
            config.displayConfig.loadingImg = resId;
            return this;
        }

        /**
         * 图片加载失败显示的图片
         *
         * @param resId
         * @return
         */
        public Builder setFailedPlaceHolder(int resId) {
            config.displayConfig.failedImg = resId;
            return this;
        }

        public ImageLoaderConfig build() {
            return config;
        }
    }

    public int getThreadCount() {
        return threadCount;
    }

    public LoadPolicy getLoadPolicy() {
        return loadPolicy;
    }

    public BitmapCache getBitmapCache() {
        return bitmapCache;
    }

    public DisplayConfig getDisplayConfig() {
        return displayConfig;
    }
}
