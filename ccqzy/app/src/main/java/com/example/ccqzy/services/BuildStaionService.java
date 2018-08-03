package com.example.ccqzy.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.ccqzy.ChangChunActivity;
import com.example.ccqzy.activitys.BuildStationActivity;
import com.lidroid.xutils.util.LogUtils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.ccqzy.QzyApplication.className;
import static com.example.ccqzy.QzyApplication.fistLogin;
import static com.example.ccqzy.QzyApplication.inputStream;
import static com.example.ccqzy.QzyApplication.isConnect;
import static com.example.ccqzy.QzyApplication.isQuanZY;
import static com.example.ccqzy.QzyApplication.splitService;

/**
 * Created by Administrator on 2018/6/7.
 */

public class BuildStaionService extends Service {
    private static final String TAG = BuildStaionService.class.getSimpleName();
    private final IBinder myIBinder = new MyBuildService();
    Timer timer = new Timer();

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d("BuidlStationService开起来了");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, className + "服务启动了");
        return START_NOT_STICKY;
    }

    public class MyBuildService extends Binder {
        public BuildStaionService getBuildS() {
            LogUtils.d(className + "服务里的方法启动了1");
            readDates();
            return BuildStaionService.this;
        }
    }

    /**
     * 正则
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }



    /*
    * 读取全站仪返回的信息
    * readDate 为自定义的接收变量
    * */

    String readDate;

    public void readDates() {
        try {
            if (inputStream != null) {
                Thread.sleep(1500);
                byte[] byteRead = new byte[inputStream.available()];
                if (byteRead.length > 10 && isQuanZY) {
                    inputStream.read(byteRead, 0, byteRead.length);
                    readDate = new String(byteRead, "UTF-8");
                    readDate = replaceBlank(readDate);
                    LogUtils.d("所服务的所有数据有数据==" + readDate + "byet数据=" + byteRead.toString());
                    if (splitService != null) {
                        splitService = null;
                        LogUtils.d("splitService已至空");
                    }
                    splitService = readDate.split("[,]");
                    if (splitService.length == 6) {
                        if (className.equals("BuildStationActivity")) {
                            BuildStationActivity.instance.handler.sendEmptyMessage(0);
                        } else {
                            LogUtils.d("没有数据了啊市劳动局法拉盛江东父老骄傲的说");
                        }
                        readDates();
                    }else if (splitService.length == 5){
                        BuildStationActivity.instance.handler.sendEmptyMessage(0);
                    }else {
                        LogUtils.d("所有数据000000000==" + splitService.length + "///" + splitService[0].toString());
                        splitService = null;
                        readDates();
                    }

                } else {
                    // 实例化Timer类
                    if (fistLogin) {
                        timer.schedule(new TimerTask() {
                            public void run() {
                                // LogUtils.d("MessageService类中没有数据的方法执行了");
                                readDates();

                            }
                        }, 500);// 这里毫秒
                    }
                }
            } else {
                ChangChunActivity.instance.showTip("蓝牙没有连接");
                isConnect = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind...开始绑定");
        fistLogin = true;
        return myIBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtils.d(TAG + "onUnBind...服务被停止了");
        fistLogin = false;
        return super.onUnbind(intent);
    }
}
