package com.example.ccqzy;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ccqzy.activitys.Login2Activity;
import com.lidroid.xutils.util.LogUtils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class ChangChunActivity extends Activity {

    Timer timer = new Timer();
    private final int CAMERA_OK = 1;
    MediaPlayer mp = new MediaPlayer();
    public static ChangChunActivity instance;
    public ChangChunActivity(){
        instance =ChangChunActivity.this ;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String filePath = Environment.getExternalStorageDirectory() + "/PD_IM_TS/";
        File file1 = new File(filePath);
        if (!file1.exists()) {
            file1.mkdir();
            LogUtils.d("pd_im_ts文件创建成功");
        }

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

        //设置蜂鸣声
        mp = MediaPlayer.create(this,R.raw.beep1);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(ChangChunActivity.this, Login2Activity.class);
                startActivity(intent);
                ChangChunActivity.this.finish();
            }
        },2000);
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
}
