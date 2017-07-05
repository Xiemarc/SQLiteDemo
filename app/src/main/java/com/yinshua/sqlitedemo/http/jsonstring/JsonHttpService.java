package com.yinshua.sqlitedemo.http.jsonstring;

import com.yinshua.sqlitedemo.http.interfaces.IHttpListener;
import com.yinshua.sqlitedemo.http.interfaces.IHttpService;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.Map;

/**
 * json字符串请求service
 * Created by marc on 2017/7/4.
 */

public class JsonHttpService implements IHttpService {
    private IHttpListener httpListener;
    private HttpClient httpClient = new DefaultHttpClient();
    private HttpPost httpPost;//post请求方式
    private String url;//请求地址
    private byte[] requestData;//请求数据

    /**
     * httpClient获取网络的回调
     */
    private HttpResponseHandler httpResponseHandler = new HttpResponseHandler();

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void excute() {
        httpPost = new HttpPost(url);
        if (requestData != null) {
            ByteArrayEntity byteArrayEntity = new ByteArrayEntity(requestData);
            httpPost.setEntity(byteArrayEntity);
        }
        try {
            httpClient.execute(httpPost, httpResponseHandler);
        } catch (IOException e) {
            e.printStackTrace();
            httpListener.onFail();
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
    public Map<String, String> getHttpHeadMap() {
        return null;
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
        return false;
    }

    @Override
    public void pause() {

    }

    private class HttpResponseHandler extends BasicResponseHandler {
        @Override
        public String handleResponse(HttpResponse response) throws ClientProtocolException {
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                httpListener.onSuccess(response.getEntity());
            } else {
                httpListener.onFail();
            }
            return null;
        }
    }
}
