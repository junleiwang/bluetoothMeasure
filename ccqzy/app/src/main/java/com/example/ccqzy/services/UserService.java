package com.example.ccqzy.services;


import com.example.ccqzy.beans.ObjectAll;
import com.example.ccqzy.daos.AjaxCallBack;
import com.example.ccqzy.daos.BaseDaoNet;
import com.example.ccqzy.daos.BaseDaoNetTest;
import com.example.ccqzy.daos.ServiceCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class UserService extends BaseService {

    /**
     * 通过网络，登陆
     *
     * @param method
     * @param loginUrl
     * @param
     * @param callback
     */
    public void loginNet(HttpRequest.HttpMethod method, String loginUrl,
                         Map<String, String> map,
                         final ServiceCallBack<String> callback) {

        loginNet(90000, method, loginUrl, map, callback);
    }


    public void loginNet(int timeout, HttpRequest.HttpMethod method,
                         String loginUrl, Map<String, String> map,
                         final ServiceCallBack<String> callback) {
        BaseDaoNet baseDaoNet = new BaseDaoNet();
//		HDApp.cookieStore.clear(); // 清除原来的cookie,在登录时清空

        baseDaoNet.swap(timeout, method, loginUrl, map, new AjaxCallBack<String>() {
            @Override
            public void onSuccess(String t) {
                LogUtils.d("登陆接口返回的json字符串为:" + t);
                try {
                    JSONObject jsonObject = new JSONObject(t);
                    boolean sussce = new JSONObject(t).getBoolean("success");
                    String result = jsonObject.getString("result");
                    LogUtils.d("登陆接口返回的success=:" + result + "---" + sussce);
                    if (sussce) {
                        callback.onSuccess(result);
                    } else {
                        int code = jsonObject.getJSONObject("error").getInt("code");
                        String showMessage = jsonObject.getJSONObject("error").getString("message");
                        String detailMeaase = jsonObject.getJSONObject("error").getString("details");
                        callback.onFailed(code, "登录失败", detailMeaase);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStart() {
                callback.onStart();
            }

            @Override
            public void onFailed(String showMessage,
                                 String detailMessage) {
                callback.onFailed(ServiceCallBack.ERROR_TYPE_NET, showMessage, detailMessage);
            }

            @Override
            public void onCancel() {
                callback.onCancel();
            }

            @Override
            public void onLoading(long current, long total) {
                callback.onLoading(current, total);
            }
        });
    }


    /*
* 上传数据服务方法
* */
    public void uploadDatesTest(String loginUrl, String mac,
                                ObjectAll object, String token,
                                final ServiceCallBack<String> callback) {

        uploadDateTest(loginUrl, mac, object, token, callback);
    }

    /*
    * 请求站点坐标
    * */
    public void requesDates(HttpRequest.HttpMethod method, String loginUrl,
                            Map<String, String> map, String token,
                            final ServiceCallBack<String> callback) {

        request(90000, method, loginUrl, map, token, callback);
    }


	/*
    * 数据上传接口
	* */

    public void uploadDateTest(String updateUrl, String mac,
                               ObjectAll object, String token,
                               final ServiceCallBack<String> callBack) {
        BaseDaoNetTest baseDaoNetTest = new BaseDaoNetTest();
        baseDaoNetTest.swap1(updateUrl, mac, object, token, new AjaxCallBack<String>() {
            @Override
            public void onSuccess(String s) {
                LogUtils.d("提交数据成功!" + "返回的数据是" + s);

                try {
                    JSONObject jsonObject = new JSONObject(s);

                    String sucess = jsonObject.getString("success");
                    if (sucess.equals("true")) {
                        LogUtils.d("上传数据成功");
                        callBack.onSuccess(sucess);
                    } else {
                        LogUtils.d("上传数据失败");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //callBack.onSuccess(s);
            }

            @Override
            public void onFailed(String showMessage, String detailMessage) {
                callBack.onFailed(2, "上传数据有问题", "失败");
                LogUtils.d("提交数据失败= =");
            }

            @Override
            public void onLoading(long current, long total) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onStart() {

            }
        });

    }

    /*
* 请求数据的服务接口
* */
    public void request(int timeout, HttpRequest.HttpMethod method,
                        String loginUrl, Map<String, String> map, String token,
                        final ServiceCallBack<String> callback) {
        BaseDaoNet baseDaoNet = new BaseDaoNet();
//		HDApp.cookieStore.clear(); // 清除原来的cookie,在登录时清空

        baseDaoNet.swap2(timeout, method, loginUrl, map, token, new AjaxCallBack<String>() {
            @Override
            public void onSuccess(String t) {
                LogUtils.d("登陆接口返回的json字符串为:" + t);
                try {
                    JSONObject jsonObject = new JSONObject(t);
                    boolean sussce = new JSONObject(t).getBoolean("success");
                    boolean result = jsonObject.getBoolean("result");
                    LogUtils.d("登陆接口返回的success=:" + result + "---" + sussce);
                    if (sussce) {
                        callback.onSuccess("" + result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStart() {
                callback.onStart();
            }

            @Override
            public void onFailed(String showMessage,
                                 String detailMessage) {
                callback.onFailed(ServiceCallBack.ERROR_TYPE_NET, showMessage, detailMessage);
            }

            @Override
            public void onCancel() {
                callback.onCancel();
            }

            @Override
            public void onLoading(long current, long total) {
                callback.onLoading(current, total);
            }
        });
    }
}
