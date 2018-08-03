package com.example.ccqzy.daos;

import com.example.ccqzy.QzyApplication;
import com.example.ccqzy.beans.ObjectAll;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * http协议 dao层 返回String类型，不作处理返回到service层，所以命名曰baseDao
 * 数据请求类
 *
 * @author mbb
 */
public class BaseDaoNet {
    private String TAG = "数据请求成功";

    /**
     * 向服务器交换数据。返回的结果为String类型
     * Map<String, String> map
     */
    public synchronized void swap(int timeout, HttpRequest.HttpMethod method,
                                  final String url, Map<String, String> map,
                                  final AjaxCallBack<String> callback) {
        LogUtils.d("请求数据  请求地址：" + url);
        RequestParams params = new RequestParams();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            params.addBodyParameter(entry.getKey(), entry.getValue());
            LogUtils.d("请求参数：" + entry.getKey() + ">" + entry.getValue());
        }
        //params.addHeader();

        final HttpUtils http = new HttpUtils();
        http.configTimeout(timeout);
        http.configSoTimeout(timeout);
        http.configCookieStore(QzyApplication.cookieStore);
        http.send(method, url, params, new RequestCallBack<String>() {
            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                callback.onLoading(current, total);
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                LogUtils.d("http请求数据成功：\n 请求的地址：" + url + "\n返回的结果：" + result);
                callback.onSuccess(result);
            }

            @Override
            public void onStart() {
                super.onStart();
                callback.onStart();
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                //进行解析失败数据
                callback.onFailed(msg,"请求失败");
            }

            @Override
            public void onCancelled() {
                super.onCancelled();
                callback.onCancel();
            }
        });
    }


    /**
     * 向服务器交换数据。返回的结果为String类型
     * Map<String, String> map
     */
    public synchronized void swap2(int timeout, HttpRequest.HttpMethod method,
                                   final String url, Map<String, String> map, String token,
                                   final AjaxCallBack<String> callback) {
        LogUtils.d("请求数据  请求地址：" + url);
        RequestParams params = new RequestParams();
        params.addHeader("Authorization",token);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            params.addBodyParameter(entry.getKey(), entry.getValue());
            LogUtils.d("请求参数：" + entry.getKey() + ">" + entry.getValue());
        }
        //params.addHeader("Content-Type","application/json");

        LogUtils.d("请求头参数：" + "Authorization"+ ">" + token);

        final HttpUtils http = new HttpUtils();
        http.configTimeout(timeout);
        http.configSoTimeout(timeout);
        http.configCookieStore(QzyApplication.cookieStore);
        http.send(method, url, params, new RequestCallBack<String>() {
            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                callback.onLoading(current, total);
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                LogUtils.d("http请求数据成功：\n 请求的地址：" + url + "\n返回的结果：" + result);
                callback.onSuccess(result);
            }

            @Override
            public void onStart() {
                super.onStart();
                callback.onStart();
            }

            @Override
            public void onFailure(HttpException error, String msg) {
               // 进行解析失败数据
                LogUtils.e("http请求数据失败： \n  请求的地址：" + url + "\n exceptionCode:"
                        + error.getExceptionCode() + "\n msg:" + msg, error);
                callback.onFailed("网络异常", msg);
                LogUtils.d("请求数据失败");
            }

            @Override
            public void onCancelled() {
                super.onCancelled();
                callback.onCancel();
            }
        });

    }


    public synchronized void swap4(int timeout, HttpRequest.HttpMethod method,
                                   final String url, ObjectAll map, String token,
                                   final AjaxCallBack<String> callback) {
        LogUtils.d("请求数据  请求地址：" + url);
        RequestParams params = new RequestParams();
        params.addHeader("Authorization",token);


           // LogUtils.d("请求参数：" + map .getMAC()+ ">和" + entry.getValue());

        //params.addHeader("Content-Type","application/json");

        LogUtils.d("请求头参数：" + "Authorization"+ ">" + token);

        final HttpUtils http = new HttpUtils();
        http.configTimeout(timeout);
        http.configSoTimeout(timeout);
        http.configCookieStore(QzyApplication.cookieStore);
        http.send(method, url, params, new RequestCallBack<String>() {
            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                callback.onLoading(current, total);
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                LogUtils.d("http请求数据成功：\n 请求的地址：" + url + "\n返回的结果：" + result);
                callback.onSuccess(result);
            }

            @Override
            public void onStart() {
                super.onStart();
                callback.onStart();
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                // 进行解析失败数据
                LogUtils.e("http请求数据失败： \n  请求的地址：" + url + "\n exceptionCode:"
                        + error.getExceptionCode() + "\n msg:" + msg, error);
                callback.onFailed("网络异常", msg);
                LogUtils.d("请求数据失败");
            }

            @Override
            public void onCancelled() {
                super.onCancelled();
                callback.onCancel();
            }
        });

}

    /**
     * 同步提交：向服务器交换数据。返回的结果为String类型
     * Map<String, String> map
     */
    public String sendSync(int timeout, HttpRequest.HttpMethod method, final String url, Map<String, String> map) throws HttpException, IOException {
        LogUtils.d("请求数据  请求地址：" + url);
        RequestParams params = new RequestParams();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            params.addBodyParameter(entry.getKey(), entry.getValue());
            LogUtils.d("请求参数：" + entry.getKey() + ">" + entry.getValue());
        }
        final HttpUtils http = new HttpUtils();
        http.configTimeout(timeout);
        http.configSoTimeout(timeout);
        http.configCookieStore(QzyApplication.cookieStore);
        ResponseStream responseStream = http.sendSync(method, url, params);
        return responseStream.readString();
    }

    public synchronized void swap1(int timeout, HttpRequest.HttpMethod method,
                                   final String url, final AjaxCallBack<String> callback) {
        LogUtils.d("请求数据  请求地址：" + url);
        final HttpUtils http = new HttpUtils();

        http.configTimeout(timeout);
        http.configSoTimeout(timeout);
        http.configCookieStore(QzyApplication.cookieStore);
        http.send(method, url, new RequestCallBack<String>() {
            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                callback.onLoading(current, total);
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                LogUtils.d("http请求数据成功：\n 请求的地址：" + url + "\n返回的结果：" + result);
                callback.onSuccess(result);
            }

            @Override
            public void onStart() {
                super.onStart();
                callback.onStart();
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                LogUtils.e("http请求数据失败：\n  请求的地址：" + url + "\n exceptionCode:"
                        + error.getExceptionCode() + "\n msg:" + msg, error);
                callback.onFailed("网络异常", msg);
            }

            @Override
            public void onCancelled() {
                super.onCancelled();
                callback.onCancel();
            }
        });
    }

    /**
     * 向服务器上传数据。返回的结果为String类型 该方法也可与上面的方法合并，犹豫上传文件时不包含文字信息，这里选择分开两个方法也ok
     */
    public synchronized void upDateFiles(int timeout,
                                         HttpRequest.HttpMethod method, String url, Map<String, File> map,
                                         final AjaxCallBack<String> callback) {

        LogUtils.d("上传文件  请求地址：" + url);
        RequestParams params = new RequestParams();
        for (Map.Entry<String, File> entry : map.entrySet()) {
            LogUtils.d("上传文件     文件名：>" + entry.getValue().getAbsolutePath());
            params.addBodyParameter(entry.getKey(), entry.getValue());
        }

        final HttpUtils http = new HttpUtils();
        http.configTimeout(timeout);
        http.configSoTimeout(timeout);
        http.configCookieStore(QzyApplication.cookieStore);
        http.send(method, url, params, new RequestCallBack<String>() {
            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                callback.onLoading(current, total);
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                LogUtils.d("http上传文件成功：\n" + result);
                callback.onSuccess(result);
            }

            @Override
            public void onStart() {
                super.onStart();
                callback.onStart();
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                LogUtils.e(
                        "http上传文件失败：\nexceptionCode:"
                                + error.getExceptionCode() + "\n msg:" + msg,
                        error);
                callback.onFailed("网络异常", msg);
            }

            @Override
            public void onCancelled() {
                super.onCancelled();
                callback.onCancel();
            }
        });
    }

    public synchronized void upData(int timeout, HttpRequest.HttpMethod method,
                                    String url, Map<String, String> map,
                                    final AjaxCallBack<String> callback) {
        LogUtils.d("上传文件  请求地址：" + url);
        final HttpUtils http = new HttpUtils();
        RequestParams params = new RequestParams();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            params.addBodyParameter(entry.getKey(), entry.getValue());
            LogUtils.d("请求参数：" + entry.getKey() + ">" + entry.getValue());
        }
        http.configTimeout(timeout);
        http.configSoTimeout(timeout);
        http.configCookieStore(QzyApplication.cookieStore);
        http.send(method, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                LogUtils.d("http上传数据成功：\n" + result);
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
            }
        });
    }
}
