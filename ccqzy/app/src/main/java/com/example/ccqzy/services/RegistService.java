package com.example.ccqzy.services;

import android.content.Context;
import android.widget.Toast;

import com.example.ccqzy.daos.AjaxCallBack;
import com.example.ccqzy.daos.BaseDaoNet;
import com.example.ccqzy.daos.BaseServiceCallBack;
import com.example.ccqzy.daos.ServiceCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wjl on 2017/7/25 17:01
 * details:
 */

public class RegistService extends BaseService {

    private BaseDaoNet baseDaoNet;
    private Context context;

    public RegistService(Context context){
        this.context = context;
        baseDaoNet = new BaseDaoNet();
    }



    //个人注册或者公司注册
    public void personRegist(String url, Map map, final BaseServiceCallBack<String> callBack){
        HttpRequest.HttpMethod method = HttpRequest.HttpMethod.POST;
        getHimData(true, timeout, method, url, map, callBack);
    }

    //验证用户名是否注册过
    public void getTestName(String url, String loginName, final BaseServiceCallBack<String> callBack){
        HttpRequest.HttpMethod method = HttpRequest.HttpMethod.POST;
        Map<String,String> map = new HashMap<String, String>();
        map.put("loginName",loginName);
        getTestData(true,timeout,method,url,map,callBack);
    }

    //验证手机号码是否注册过
    public void getTestMobile(String url, String mobile, final BaseServiceCallBack<String> callBack){
        HttpRequest.HttpMethod method = HttpRequest.HttpMethod.POST;
        Map<String,String> map = new HashMap<String, String>();
        map.put("mobile",mobile);
        getTestData(true,timeout,method,url,map,callBack);
    }

    //验证邮箱是否注册过
    public void getTestEmail(String url, String email, final BaseServiceCallBack<String> callBack){
        HttpRequest.HttpMethod method = HttpRequest.HttpMethod.POST;
        Map<String,String> map = new HashMap<String, String>();
        map.put("email",email);
        getTestData(true,timeout,method,url,map,callBack);
    }

    //验证组织机构是否注册过
    public void getTestCompany(String url, String orgName, final BaseServiceCallBack<String> callBack){
        HttpRequest.HttpMethod method = HttpRequest.HttpMethod.POST;
        Map<String,String> map = new HashMap<String, String>();
        map.put("orgName",orgName);
        getTestData(true,timeout,method,url,map,callBack);
    }

    //得到验证码
    public void getCode(String url, String mobile, String templateid, final BaseServiceCallBack<String> callBack){
        HttpRequest.HttpMethod method = HttpRequest.HttpMethod.POST;
        Map<String,String> map = new HashMap<String, String>();
        map.put("mobile",mobile);
        map.put("templateid",templateid);
        getTestData(true,timeout,method,url,map,callBack);
    }

    //测试验证码
    public void getTestCode(String url, String mobile, String code, final BaseServiceCallBack<String> callBack){
        HttpRequest.HttpMethod method = HttpRequest.HttpMethod.POST;
        Map<String,String> map = new HashMap<String, String>();
        map.put("mobile",mobile);
        map.put("code",code);
        getTestData(true,timeout,method,url,map,callBack);
    }

    //验证当前密码是否正确
    public void getTestPasswd(String url, String id, String passwd, final BaseServiceCallBack<String> callBack){
        HttpRequest.HttpMethod method = HttpRequest.HttpMethod.POST;
        Map<String,String> map = new HashMap<String, String>();
        map.put("passwd",passwd);
        map.put("id",id);
        getTestData(true,timeout,method,url,map,callBack);
    }

    //修改密码
    public void getChangePasswd(String url, String id, String newpassword, final BaseServiceCallBack<String> callBack){
        HttpRequest.HttpMethod method = HttpRequest.HttpMethod.POST;
        Map<String,String> map = new HashMap<String, String>();
        map.put("id",id);
        map.put("newpassword",newpassword);
        getTestData(true,timeout,method,url,map,callBack);
    }

    //报名服务
    public void enterTeam(String url, String matchId, String unitSource, final BaseServiceCallBack<String> callBack){
        HttpRequest.HttpMethod method = HttpRequest.HttpMethod.POST;
        Map<String,String> map = new HashMap<String, String>();
        map.put("matchId",matchId);
        map.put("unitSource",unitSource);
        getHimData(true,timeout,method,url,map,callBack);
    }

    private void getHimData(boolean b, int timeout, HttpRequest.HttpMethod method, String url, Map<String, String> map,
                            final BaseServiceCallBack<String> callBack){
        baseDaoNet.swap(timeout, method, url, map, new AjaxCallBack<String>() {
            @Override
            public void onSuccess(String s) {
                LogUtils.d("请求得到的数据="+s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        callBack.onSuccess(s);
                    }else {
                        Toast.makeText(context, "注册失败,请检测输入的数据", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    LogUtils.e("json数据保存失败：" + e.getMessage(), e);
                } catch (Exception e) {
                    LogUtils.e("发送数据失败：" + e.getMessage(), e);
                }
            }

            @Override
            public void onFailed(String showMessage, String detailMessage) {
                callBack.onFailed(ServiceCallBack.ERROR_TYPE_NET, showMessage, detailMessage);
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

    private void getTestData(boolean b, int timeout, HttpRequest.HttpMethod method, String url, Map<String, String> map,
                             final BaseServiceCallBack<String> callBack){
        baseDaoNet.swap(timeout, method, url, map, new AjaxCallBack<String>() {
            @Override
            public void onSuccess(String s) {
                LogUtils.d("返回数据s="+s);
                   callBack.onSuccess(s);
            }

            @Override
            public void onFailed(String showMessage, String detailMessage) {
                LogUtils.d("返回数据失败");
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
}
