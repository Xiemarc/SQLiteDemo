package com.yinshua.sqlitedemo.http.jsonstring;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.yinshua.sqlitedemo.http.interfaces.IDataListener;
import com.yinshua.sqlitedemo.http.interfaces.IHttpListener;

import org.apache.http.HttpEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * 转化成json类型的响应
 * m 是响应类
 * Created by marc on 2017/7/4.
 */

public class JsonDealListener<M> implements IHttpListener {
    private Class<M> response;
    /**
     * 回调给调用层的接口
     */
    private IDataListener<M> dataListener;
    /**
     * 拿到主线程
     */
    Handler handler = new Handler(Looper.getMainLooper());

    public JsonDealListener(Class<M> response, IDataListener<M> dataListener) {
        this.response = response;
        this.dataListener = dataListener;
    }

    @Override
    public void onSuccess(HttpEntity httpEntity) {
        try {
            InputStream inputStream = httpEntity.getContent();
            String content = getContent(inputStream);//得到网络返回数据
            final M m = JSON.parseObject(content, response); //将string文本转化成响应基类
            handler.post(new Runnable() {
                @Override
                public void run() {
                    dataListener.onSuccess(m);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            dataListener.onFail();
        }
    }


    @Override
    public void onFail() {
        dataListener.onFail();
    }

    @Override
    public void addHttpHeader(Map<String, String> headerMap) {

    }

    /**
     * 通过流的方式转字符串
     *
     * @param inputStream
     * @return
     */
    private String getContent(InputStream inputStream) {
        String content = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error=" + e.toString());
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    System.out.println("Error=" + e.toString());
                }
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            dataListener.onFail();
        }
        return content;
    }
}
