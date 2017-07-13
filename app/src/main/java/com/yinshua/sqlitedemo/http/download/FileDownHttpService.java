package com.yinshua.sqlitedemo.http.download;

import com.yinshua.sqlitedemo.http.interfaces.IHttpListener;
import com.yinshua.sqlitedemo.http.interfaces.IHttpService;
import com.yinshua.sqlitedemo.utils.logger.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by marc on 2017/7/5.
 */

public class FileDownHttpService implements IHttpService {
    private static final String TAG = "marc";
    /**
     * 即将添加请求头信息
     */
    private Map<String, String> headerMap = Collections.synchronizedMap(new HashMap<String, String>());
    /**
     * 含有合理请求的接口
     */
    private IHttpListener httpListener;
    private HttpClient httpClient = new DefaultHttpClient();
    /**
     * 下载使用get
     */
    private HttpGet httpGet;
    private String url;
    /**
     * 请求参数
     */
    private byte[] requestData;

    /**
     * httpClient获取网络的回调
     */
    private HttpRespnceHandler httpRespnceHandler = new HttpRespnceHandler();

    /**
     * 只允许一个线程去改变，线程安全
     */
    private AtomicBoolean pause = new AtomicBoolean(false);

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    @Override
    public void excute() {
        httpGet = new HttpGet(url);
        constrcutHeader();
        //下载策略，不要请求参数
//        ByteArrayEntity byteArrayEntity=new ByteArrayEntity(requestDate);
//        httpPost.setEntity(byteArrayEntity);
        try {
            httpClient.execute(httpGet, httpRespnceHandler);
        } catch (IOException e) {
            e.printStackTrace();
            httpListener.onFail();
        }
    }

    /**
     * 构建请求头
     */
    private void constrcutHeader() {
        Iterator<String> iterator = headerMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = headerMap.get(key);
            Logger.i(TAG, "请求头信息:" + key + " value =" + value);
            httpGet.addHeader(key, value);
        }
    }

    @Override
    public void setHttpListener(IHttpListener httpListener) {
        this.httpListener = httpListener;
    }

    @Override
    public void setRequestData(byte[] requestData) {
        this.requestData = requestData;
    }

    @Override
    public void pause() {
        this.pause.compareAndSet(false, true);
    }

    @Override
    public Map<String, String> getHttpHeadMap() {
        return headerMap;
    }

    @Override
    public boolean cancle() {
        return false;
    }

    @Override
    public boolean isCancle() {
        return false;
    }

    @Override
    public boolean isPause() {
        return pause.get();
    }

    /**
     * 请求回调
     */
    private class HttpRespnceHandler extends BasicResponseHandler {
        @Override
        public String handleResponse(HttpResponse response) throws ClientProtocolException {
            int code = response.getStatusLine().getStatusCode();
            if (code == 200 || code == 206) {
                httpListener.onSuccess(response.getEntity());
            } else {
                httpListener.onFail();
            }
            return null;
        }
    }
}
