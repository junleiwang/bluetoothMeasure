package com.example.ccqzy.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.ccqzy.activitys.AddPointActivity;
import com.example.ccqzy.activitys.DeflectionActivity;
import com.example.ccqzy.activitys.FourCornersHighActivity;
import com.example.ccqzy.activitys.LoginActivity;
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
 * Created by Administrator on 2018/4/26.
 */

public class MessageService extends Service {
    private static final String TAG = MessageService.class.getSimpleName();
    Timer timer = new Timer();
    private final IBinder binder = new MyBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "普通的服务创建了"+className);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, className+"普通的服务启动了");
        return START_NOT_STICKY;
    }

    public class MyBinder extends Binder {
        public MessageService getService() {
            Log.d(TAG, "普通的服务里的方法启动了1");
            LogUtils.d(className+"普通的服务里的方法启动了1");
            readDates();
            return MessageService.this;
        }
    }

    /**
     *正则
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
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
                Thread.sleep(1300);
                byte[] byteRead = new byte[inputStream.available()];
                if (byteRead.length > 11 && isQuanZY) {
                    inputStream.read(byteRead, 0, byteRead.length);
                    readDate = new String(byteRead, "UTF-8");
                    readDate = replaceBlank(readDate);
                    LogUtils.d("普通的服务的所有数据有数据==" + readDate + "byet数据=" + byteRead.toString());
                    if (splitService!=null) {
                        splitService = null;
                        LogUtils.d("splitService已至空");
                    }
                    splitService = readDate.split("[,]");
                    if (splitService.length == 6 ||splitService.length ==5) {
                       if (className.equals("FourCornersHighActivity")){
                            FourCornersHighActivity.instance.handler.sendEmptyMessage(0);
                        }else if (className.equals("DeflectionActivity")){
                            DeflectionActivity.instance.handler.sendEmptyMessage(0);
                        }else if (className.equals("AddPointActivity")){
                            AddPointActivity.instance.handler.sendEmptyMessage(0);
                        }else {
                            LogUtils.d("没有数据了啊市劳动局法拉盛江东父老骄傲的说");
                        }
                        readDates();
                    }else {
                        LogUtils.d("普通的所有数据000000000==" + splitService.length + "///" + splitService[0].toString());
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
                LoginActivity.instance.showTip("蓝牙没有连接");
                isConnect = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /*
 *  处理返回的坐标数据保留小数点后四位,四舍五入
 * */
    private String getFourPoint(String point) {
        double value = Double.parseDouble(point);
        String result = String.format("%.3f", value);
        LogUtils.d("返回的结果" + result);
        return result;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onBind...普通的开始绑定");
        fistLogin = true;
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        LogUtils.d(TAG +"onUnBind...普通的服务被停止了");
        fistLogin = false;
        return super.onUnbind(intent);

    }

}

