package com.example.ccqzy.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ccqzy.ChangChunActivity;
import com.example.ccqzy.R;
import com.example.ccqzy.androidutils.EncryptManager;
import com.lidroid.xutils.util.LogUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Login2Activity extends Activity {

    private TextView etMac;
    private EditText etCode;
    private TextView tvLogin;

    String code;
    EncryptManager encryptManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        initView();
        encryptManager = new EncryptManager();

        SharedPreferences sharedPre=getSharedPreferences("config", MODE_PRIVATE);
       // String username=sharedPre.getString("username", "");
        String password=sharedPre.getString("password", "");
        LogUtils.d("已经存在的密码"+password);
        if (!TextUtils.isEmpty(password)){
            etCode.setText(password);
        }else {
            LogUtils.d("没有缓存数据");
        }
    }

    private void initView(){
        etMac = findViewById(R.id.et_mac);
        etCode = findViewById(R.id.et_code);
        tvLogin = findViewById(R.id.tv_login);

        etMac.setText(getIpAndMacAddress());

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
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
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                name = intf.getName();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
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


    //将授权码解压成字符串与mac地址比较
    private String getMacCode(){
        code = etMac.getText().toString();
        LogUtils.d("修改前mac"+code);
        code = code.replace(":","");
        LogUtils.d("修改后mac"+code);
        String encryptCode = encryptManager.MD5Encrypt(code);
        LogUtils.d("输出的数据为"+encryptCode);
        return encryptCode;
    }

    /**
     * 验证mac地址后进行登录
     * */
    private void login(){
        if (etCode.getText().toString().equals(getMacCode())){
            saveLoginInfo(this,etMac.getText().toString(),etCode.getText().toString());
            Intent intent = new Intent(Login2Activity.this, ActionActivity.class);
            startActivity(intent);
            Login2Activity.this.finish();
            ChangChunActivity.instance.showTip("登录成功!!!");
        }else {
            ChangChunActivity.instance.showTip("授权码不正确,请重新输入");
        }

    }

    /**
     * 使用SharedPreferences保存用户登录信息
     * @param context
     * @param username
     * @param password
     */
    public static void saveLoginInfo(Context context, String username, String password){
        //获取SharedPreferences对象
        SharedPreferences sharedPre=context.getSharedPreferences("config", context.MODE_PRIVATE);
        //获取Editor对象
        SharedPreferences.Editor editor=sharedPre.edit();
        //设置参数
        editor.putString("username", username);
        editor.putString("password", password);
        //提交
        editor.commit();
    }
}
