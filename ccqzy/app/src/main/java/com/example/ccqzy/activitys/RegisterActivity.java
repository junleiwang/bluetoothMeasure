package com.example.ccqzy.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ccqzy.ChangChunActivity;
import com.example.ccqzy.GlobalContext;
import com.example.ccqzy.QzyApplication;
import com.example.ccqzy.R;
import com.example.ccqzy.androidutils.NetConnectManager;
import com.example.ccqzy.daos.BaseServiceCallBack;
import com.example.ccqzy.services.RegistService;
import com.lidroid.xutils.util.LogUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends Activity {

    /*
    * 注册界面
    * */

    private EditText etCommany;//单位
    private EditText etRegistUser;//用户名
    private EditText etRegistPassword;//密码
    private EditText etRegistPassword2;
    private EditText etMacAddress;//mac地址
    private TextView tvRegister;//注册

    private String commany;
    private String registUser;
    private String registPassword;
    private String registPassword2;
   private String macAddress;

    RegistService service;
    String registUrl = "";//注册的url地址
    Map<String,String> map = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //设置导航栏透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        service = new RegistService(this);
        registUrl = GlobalContext.getUrl(R.string.url_regist_test);

        macAddress = getIpAndMacAddress();

        LogUtils.d("马克地址="+macAddress);
        initView();
        initTitle();

    }

    /*
    * 初始化标题头
    * */
    private void initTitle() {
        TextView back = findView(R.id.tv_title_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.this.finish();
            }
        });

        TextView title = findView(R.id.tv_title_name);
        title.setText("注册");
    }

    /*
    * 获取mac地址
    * */
    private String getIpAndMacAddress() {
        String ip = "";
        boolean isBreak = false;
        String name = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                name = intf.getName();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        ip = inetAddress.getHostAddress().toString();
                        isBreak = true;
                        break;
                    }
                }
                if (isBreak) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String mac = "";
        if (!TextUtils.isEmpty(name)) {
            try {
                byte[] address = NetworkInterface.getByName(name)
                        .getHardwareAddress();
                if (address != null) {
                    mac = byte2hex(address, address.length);
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(mac) && !TextUtils.isEmpty(ip)) {
           // return ip + "_" + mac;
            return mac;
        }
        return "";
    }
    /*byte转换成十进制
    * */
    private static String byte2hex(byte[] b, int length) {
        StringBuffer hs = new StringBuffer(length);
        String stmp = "";
        int len = length;
        for (int n = 0; n < len; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                hs = hs.append("0").append(stmp);
            else {
                hs = hs.append(stmp);
            }
            if (n != len - 1) {
                hs.append(":");
            }
        }
        return String.valueOf(hs);
    }

    //初始化控件
    private void initView() {
        etCommany = findView(R.id.et_commany_register);
        etRegistUser = findView(R.id.et_user_register);
        etRegistPassword = findView(R.id.et_password_register);
        etRegistPassword2 = findView(R.id.et_password_register2);
        etMacAddress = findView(R.id.et_mac_address);
        tvRegister = findView(R.id.tv_register);

        etMacAddress.setText(macAddress);

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getViewText();

                //注册信息不能为空且两次密码输入必须一致才能注册
                if (commany.equals("") || registUser.equals("") || registPassword.equals("")
                        || registPassword2.equals("") || macAddress.equals("")) {
                    ChangChunActivity.instance.showTip("注册信息不能为空，请输入信息");
                } else {
                    if (registPassword.equals(registPassword2)) {

                        if (NetConnectManager.isNetWorkAvailable(QzyApplication.getAppContext())){
                            register();
                        }else {
                            ChangChunActivity.instance.showTip("网络不可用,请检测网络连接");

                        }

                    }else {
                        ChangChunActivity.instance.showTip("两次输入的密码不一致，请重新输入");
                    }
                }

            }
        });
    }

    /*
    * 获取用户输入的信息
    * */
    public void getViewText() {
        commany = etCommany.getText().toString();
        registUser = etRegistUser.getText().toString();
        registPassword = etRegistPassword.getText().toString();
        registPassword2 = etRegistPassword2.getText().toString();
        macAddress = etMacAddress.getText().toString();

        map.put("TenancyName",commany);
        map.put("UserName",registUser);
        map.put("Password",registPassword);
        map.put("MAC",macAddress);
    }

    /*
    * 请求注册
    * */
    private void register() {

        service.personRegist(registUrl, map, new BaseServiceCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFailed(int type, String showMessage, String detailMessage) {
                super.onFailed(type, showMessage, detailMessage);
                ChangChunActivity.instance.showTip("注册失败");
            }

            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);

                LogUtils.d("注册成功");
                ChangChunActivity.instance.showTip("注册成功!");
                Intent intent = new Intent(RegisterActivity.this,ChangChunActivity.class);
                startActivity(intent);
                RegisterActivity.this.finish();
            }

            @Override
            public void onCancel() {
                super.onCancel();
            }
        });

    }

    public <T> T findView(int view) {
        return (T) super.findViewById(view);
    }
}
