package com.example.ccqzy.activitys;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ccqzy.GlobalContext;
import com.example.ccqzy.QzyApplication;
import com.example.ccqzy.R;
import com.example.ccqzy.androidutils.NetConnectManager;
import com.example.ccqzy.beans.User;
import com.example.ccqzy.daos.ServiceCallBack;
import com.example.ccqzy.services.UserService;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;

import org.xutils.DbManager;
import org.xutils.db.Selector;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends Activity implements View.OnClickListener {

    //定义各个view控件
    private TextView tvLogin;//登录按钮
    private TextView tvRegister;//注册链接
    private EditText etCompany;//单位
    private EditText etName;//用户名
    private EditText etPassword;//密码

    private ImageView ivTest;
    String companyName;
    String userName;
    String password;

    private final int CAMERA_OK = 1;

    UserService service;
    String loginUrl = "";//登录的url地址
    Map<String, String> loginMap = new HashMap();

    //用户信息数据库
    DbManager dbUser;
    public List<User> usrePoint = new ArrayList<>();

    MediaPlayer mp = new MediaPlayer();
    Timer timer = new Timer();

    public static LoginActivity instance;

    public LoginActivity() {
        instance = LoginActivity.this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //设置导航栏透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //初始化用户信息的数据库
        dbUser = x.getDb(((QzyApplication) getApplication()).getUserInfoConfig());

        service = new UserService();
        loginUrl = GlobalContext.getUrl(R.string.url_login);
        //设置蜂鸣声
        mp = MediaPlayer.create(this,R.raw.beep1);

        initView();

        //判断user数据库是否有信息,有就直接显示,没有则不显示
        userMessage();

        //6.0已上版本权限申请
        if (Build.VERSION.SDK_INT > 22) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //先判断有没有权限 ，没有就在这里进行权限的申请
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_OK);

            } else {
                //说明已经获取到摄像头权限了 想干嘛干嘛
            }
        } else {
//这个说明系统版本在6.0之下，不需要动态获取权限。

        }

    }

    /*
    * 初始化view控件
    * */
    private void initView() {
        tvLogin = findView(R.id.tv_login);
        tvRegister = findView(R.id.tv_regist);
        etCompany = findView(R.id.et_company);
        etName = findView(R.id.et_user);
        etPassword = findView(R.id.et_password);


        tvLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);

        ivTest = (ImageView) findViewById(R.id.iv_show_pic);
    }

    /*
    * 获取用户输入的信息
    * */
    private void getTextToString() {
        companyName = etCompany.getText().toString();
        userName = etName.getText().toString();
        password = etPassword.getText().toString();

        loginMap.put("tenancyName", companyName);
        loginMap.put("UsernameOrEmailAddress", userName);
        loginMap.put("password", password);
        loginMap.put("MAC", getIpAndMacAddress());
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

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_login:
                //如果有网络进行判断登录,没有网络,提示没有网络,测试可以直接使用
                if (NetConnectManager.isNetWorkAvailable(QzyApplication.getAppContext())) {
                    login();
                } else {
                    showTip("网络不可用,请检檫网络!");
                    intent = new Intent(LoginActivity.this, ActionActivity.class);
                    startActivity(intent);
                }

                break;
            case R.id.tv_regist:
                //注册界面
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
                break;
            default:
                break;
        }
    }

    /*
    * 登录方法
    * */
    private void login() {
        getTextToString();
        LogUtils.d("输入的用户信息=" + companyName + "\n" + userName + "\n" + password);
        if (!companyName.equals("") && !userName.equals("") && !password.equals("")) {
            service.loginNet(HttpRequest.HttpMethod.POST, loginUrl, loginMap, new ServiceCallBack<String>() {
                @Override
                public void onStart() {

                }

                @Override
                public void onFailed(int type, String showMessage, String detailMessage) {
                    showTip("登录失败" + showMessage);
                }

                @Override
                public void onSuccess(String result) {
                    try {
                        Selector<User> selector = dbUser.selector(User.class);
                        List<User> userAll = selector.findAll();
                        if (userAll ==null){
                            insertPointName();
                        }else {
                            LogUtils.d("用户名已经存储!");
                        }

                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    showTip("登录成功!");
                    Intent intent = new Intent(LoginActivity.this, ActionActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                }

                @Override
                public void onLoading(long current, long total) {

                }

                @Override
                public void onCancel() {

                }
            });
        } else {
            showTip("输入信息不能为空");
        }
    }

    /**
     * 注册权限申请回调
     *
     * @param requestCode  申请码
     * @param permissions  申请的权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CAMERA_OK:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Toast.makeText(MainActivity.this, "相机权限，悬浮窗权限，sd存储权限已经打开", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(MainActivity.this, "请手动打开，悬浮窗权限，sd存储权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //点数据添加
    private void insertPointName() throws DbException {
        User user = null;
        user = new User("1", companyName, userName, password);

        try {
            LogUtils.d("这里出现的问题" + user.toString());
            dbUser.save(user);
            LogUtils.d("添加数据到dbUser成功" + dbUser.toString());
        } catch (DbException e) {

        }
    }

    //显示数据
    private void userMessage(){
        try {
            Selector<User> selector = dbUser.selector(User.class);
            usrePoint = selector.findAll();
            if (usrePoint !=null){
                etCompany.setText(usrePoint.get(0).getTenancyName());
                etName.setText(usrePoint.get(0).getUsernameOrEmailAddress());
                etPassword.setText(usrePoint.get(0).getPassword());

            }else {
                etCompany.setText("");
                etName.setText("");
                etPassword.setText("");
                LogUtils.d("DBUSER数据库没有数据");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private Toast mToast;

    //修改Toast显示的字体大小
    public void showTip(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mToast == null) {
                    mToast = Toast.makeText(getApplicationContext(), "",
                            Toast.LENGTH_SHORT);
                    LinearLayout layout = (LinearLayout) mToast.getView();
                    TextView tv = (TextView) layout.getChildAt(0);
                    tv.setTextSize(22);
                }
                mToast.setGravity(Gravity.CENTER, 0, 0);
                mToast.setDuration(Toast.LENGTH_SHORT);
                mToast.setText(str);
                mToast.show();
            }
        });
    }

    //实现手机蜂鸣的方法
    public void getVoice(){
        mp.start();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //mp.stop();
                mp.pause();
            }
        },1000);
    }

    public <T> T findView(int view) {
        return (T) super.findViewById(view);
    }
}
