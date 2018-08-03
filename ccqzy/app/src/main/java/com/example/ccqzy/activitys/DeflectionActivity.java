package com.example.ccqzy.activitys;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ccqzy.ChangChunActivity;
import com.example.ccqzy.QzyApplication;
import com.example.ccqzy.R;
import com.example.ccqzy.androidutils.AlertDialogToast;
import com.example.ccqzy.androidutils.FormulaTools;
import com.example.ccqzy.services.MessageService;
import com.lidroid.xutils.util.LogUtils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.ccqzy.QzyApplication.MEASURE;
import static com.example.ccqzy.QzyApplication.className;
import static com.example.ccqzy.QzyApplication.inputStream;
import static com.example.ccqzy.QzyApplication.isQuanZY;
import static com.example.ccqzy.QzyApplication.outputStream;
import static com.example.ccqzy.QzyApplication.splitService;
import static java.lang.Math.PI;

public class DeflectionActivity extends Activity implements View.OnClickListener {
    //三个点的测量按钮
    private TextView tvMeasure1;
    private TextView tvMeasure2;
    private TextView tvMeasure3;

    //point1点坐标
    private TextView tvX1;
    private TextView tvY1;
    private TextView tvZ1;
    //point2点坐标
    private TextView tvX2;
    private TextView tvY2;
    private TextView tvZ2;
    //point3点坐标
    private TextView tvX3;
    private TextView tvY3;
    private TextView tvZ3;

    //计算挠度的按钮
    private TextView tvMeasure;

    //挠度值显示的view
    private LinearLayout llResult;
    private TextView tvResult;
    private TextView tvResultValue;

    //计算的工具类
    FormulaTools formulaTools;
    QzyApplication.CImsPointEx point11 = new QzyApplication.CImsPointEx();
    QzyApplication.CImsPointEx point2 = new QzyApplication.CImsPointEx();
    QzyApplication.CImsPointEx point3 = new QzyApplication.CImsPointEx();
    QzyApplication.CImsPointEx point4 = new QzyApplication.CImsPointEx();
    double result;

    //读取全站仪的数据
    String readDate;
    //数据节点
    String[] split;

    private long firstTime = 0;
    //判断服务是否已经解绑
    private boolean isBound = false;

    public static DeflectionActivity instance;

    public DeflectionActivity() {
        instance = DeflectionActivity.this;
    }

    //测量数据
    //接收仪器返回的坐标值弧度值
    QzyApplication.CPDObvValue point1;
    QzyApplication.CImsPointEx pointx;
    int tvInt = 1;
    //全站仪返回的数据处理方法
    public Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //////////////////////////修改的数据
                    if (splitService != null && isQuanZY && (splitService.length ==6 ||splitService.length ==5)) {
                        pointx = new QzyApplication.CImsPointEx();
                        pointx.X = getFourDouble(splitService[2]) * 1000;
                        pointx.Y = -getFourDouble(splitService[1]) * 1000;
                        pointx.Z = getFourDouble(splitService[3]) * 1000;
                        if (tvInt == 1) {
                            tvX1.setText(getFourPoint(String.valueOf(pointx.X)));
                            tvY1.setText(getFourPoint(String.valueOf(pointx.Y)));
                            tvZ1.setText(getFourPoint(String.valueOf(pointx.Z)));
                            tvX1.setTextColor(Color.parseColor("#08f710"));
                            tvY1.setTextColor(Color.parseColor("#08f710"));
                            tvZ1.setTextColor(Color.parseColor("#08f710"));
                            point11 = pointx;
                            LogUtils.d("point11_x=" + point11.X + "point11_y=" + point11.Y + "point11_z=" + point11.Z);
                        } else if (tvInt == 2) {
                            tvX2.setText(getFourPoint(String.valueOf(pointx.X)));
                            tvY2.setText(getFourPoint(String.valueOf(pointx.Y)));
                            tvZ2.setText(getFourPoint(String.valueOf(pointx.Z)));
                            tvX2.setTextColor(Color.parseColor("#08f710"));
                            tvY2.setTextColor(Color.parseColor("#08f710"));
                            tvZ2.setTextColor(Color.parseColor("#08f710"));
                            point2 = pointx;
                            LogUtils.d("point2_x=" + point2.X + "point2_y=" + point2.Y + "point2_z=" + point2.Z);
                        } else if (tvInt == 3) {
                            tvX3.setText(getFourPoint(String.valueOf(pointx.X)));
                            tvY3.setText(getFourPoint(String.valueOf(pointx.Y)));
                            tvZ3.setText(getFourPoint(String.valueOf(pointx.Z)));
                            tvX3.setTextColor(Color.parseColor("#08f710"));
                            tvY3.setTextColor(Color.parseColor("#08f710"));
                            tvZ3.setTextColor(Color.parseColor("#08f710"));
                            point3 = pointx;
                            LogUtils.d("point3_x=" + point3.X + "point3_y=" + point3.Y + "point3_z=" + point3.Z);
                        }
                        //蜂鸣
                        ChangChunActivity.instance.getVoice();
                        if (point11.X != 0 && point11.Y != 0 && point11.Z != 0 && point2.X != 0 &&
                                point2.Y != 0 && point2.Z != 0 && point3.X != 0 && point3.Y != 0 && point3.Z != 0) {
                            //进行计算
                            measureResult(point11, point2, point3, point4);
                        }

                        if (tvInt == 3) {
                            tvInt = 1;
                        } else {
                            tvInt = tvInt + 1;
                        }

                    } else {
                        splitService = null;
                        LogUtils.d("全站仪里没有数据!!");
                    }

                    ///////////////////////修改的数据

                    if (split != null &&split.length==7) {
                        //测量返回的值 double类型
                        point1 = new QzyApplication.CPDObvValue();
                        point1.m_dHzAngle = getFourDouble(split[3]);//水平角
                        point1.m_dVAngle = getFourDouble(split[4]);//垂直角
                        point1.m_dSlopeDist = getFourDouble(split[5]) * 1000;//斜距(米)
                        LogUtils.d("point1获取值成功");

                        //定义全站仪上的坐标点(x,y,z)
                        pointx = new QzyApplication.CImsPointEx();
                        pointx = getPointCoordinate(point1, pointx);
                        LogUtils.d("pointx=" + pointx.X + "y=" + pointx.Y + "z=" + pointx.Z);
                        if (tvInt == 1) {
                            tvX1.setText(getFourPoint(String.valueOf(pointx.X)));
                            tvY1.setText(getFourPoint(String.valueOf(pointx.Y)));
                            tvZ1.setText(getFourPoint(String.valueOf(pointx.Z)));
                            tvX1.setTextColor(Color.parseColor("#08f710"));
                            tvY1.setTextColor(Color.parseColor("#08f710"));
                            tvZ1.setTextColor(Color.parseColor("#08f710"));
                            point11 = pointx;
                            LogUtils.d("point11_x=" + point11.X + "point11_y=" + point11.Y + "point11_z=" + point11.Z);
                        } else if (tvInt == 2) {
                            tvX2.setText(getFourPoint(String.valueOf(pointx.X)));
                            tvY2.setText(getFourPoint(String.valueOf(pointx.Y)));
                            tvZ2.setText(getFourPoint(String.valueOf(pointx.Z)));
                            tvX2.setTextColor(Color.parseColor("#08f710"));
                            tvY2.setTextColor(Color.parseColor("#08f710"));
                            tvZ2.setTextColor(Color.parseColor("#08f710"));
                            point2 = pointx;
                            LogUtils.d("point2_x=" + point2.X + "point2_y=" + point2.Y + "point2_z=" + point2.Z);
                        } else if (tvInt == 3) {
                            tvX3.setText(getFourPoint(String.valueOf(pointx.X)));
                            tvY3.setText(getFourPoint(String.valueOf(pointx.Y)));
                            tvZ3.setText(getFourPoint(String.valueOf(pointx.Z)));
                            tvX3.setTextColor(Color.parseColor("#08f710"));
                            tvY3.setTextColor(Color.parseColor("#08f710"));
                            tvZ3.setTextColor(Color.parseColor("#08f710"));
                            point3 = pointx;
                            LogUtils.d("point3_x=" + point3.X + "point3_y=" + point3.Y + "point3_z=" + point3.Z);
                        }

                        //蜂鸣
                        ChangChunActivity.instance.getVoice();
                        if (point11.X != 0 && point11.Y != 0 && point11.Z != 0 && point2.X != 0 &&
                                point2.Y != 0 && point2.Z != 0 && point3.X != 0 && point3.Y != 0 && point3.Z != 0) {
                            //进行计算
                            measureResult(point11, point2, point3, point4);
                        }

                        if (tvInt == 3) {
                            tvInt = 1;
                        } else {
                            tvInt = tvInt + 1;
                        }
                        isQuanZY = true;
                        splitService = null;
                    } else {
                        LogUtils.d("split[]里没有数据!!");
                    }
                    break;
            }
            return false;
        }
    });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deflection);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        formulaTools = new FormulaTools();

        //给服务端的信息处理赋值
        className = "DeflectionActivity";

        initTitle();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(DeflectionActivity.this, MessageService.class);
        isBound = bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    /*
    * 初始化view控件
    * */
    private void initView() {
        tvMeasure = (TextView) findViewById(R.id.tv_measure_result);
        tvMeasure1 = (TextView) findViewById(R.id.tv_btn_measure1);
        tvMeasure2 = (TextView) findViewById(R.id.tv_btn_measure2);
        tvMeasure3 = (TextView) findViewById(R.id.tv_btn_measure3);

        tvX1 = (TextView) findViewById(R.id.tv_pointX1);
        tvY1 = (TextView) findViewById(R.id.tv_pointY1);
        tvZ1 = (TextView) findViewById(R.id.tv_pointH1);
        tvX2 = (TextView) findViewById(R.id.tv_pointX2);
        tvY2 = (TextView) findViewById(R.id.tv_pointY2);
        tvZ2 = (TextView) findViewById(R.id.tv_pointH2);
        tvX3 = (TextView) findViewById(R.id.tv_pointX3);
        tvY3 = (TextView) findViewById(R.id.tv_pointY3);
        tvZ3 = (TextView) findViewById(R.id.tv_pointH3);

        llResult = (LinearLayout) findViewById(R.id.ll_deflection);
        tvResult = (TextView) findViewById(R.id.tv_result);
        tvResultValue = (TextView) findViewById(R.id.tv_result_value);

        tvMeasure.setOnClickListener(this);
        tvMeasure1.setOnClickListener(this);
        tvMeasure2.setOnClickListener(this);
        tvMeasure3.setOnClickListener(this);

    }

    /*
    * 初始化标题头
    * */
    private void initTitle() {

        TextView back = (TextView) findViewById(R.id.tv_title_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeflectionActivity.this.finish();
            }
        });

        TextView title = (TextView) findViewById(R.id.tv_title_name);
        title.setText("挠度值");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_measure_result:

                if (point11.X != 0 && point11.Y != 0 && point11.Z != 0 && point2.X != 0 &&
                        point2.Y != 0 && point2.Z != 0 && point3.X != 0 && point3.Y != 0 && point3.Z != 0) {
                    //进行计算
                    showMeasureDialog();
                } else {
                    ChangChunActivity.instance.showTip("三个测量点必须都完成测量,才能计算结果");
                }
                break;
            case R.id.tv_btn_measure1:
                long nowTime = System.currentTimeMillis();
                if (nowTime - firstTime > 3000) {
                    isQuanZY = false;
                    splitService = null;
                    //点击测量
                    sendMessage(MEASURE);
                    tvInt = 1;
                    firstTime = nowTime;
                } else {
                    ChangChunActivity.instance.showTip("请勿连续点击");
                }
                break;
            case R.id.tv_btn_measure2:

                long nowTime2 = System.currentTimeMillis();
                if (nowTime2 - firstTime > 3000) {
                    isQuanZY = false;
                    splitService = null;
                    //点击测量
                    sendMessage(MEASURE);
                    tvInt = 2;
                    firstTime = nowTime2;
                } else {
                    ChangChunActivity.instance.showTip("请勿连续点击");
                }
                //sendMessage(MEASURE);
                break;
            case R.id.tv_btn_measure3:
                long nowTime3 = System.currentTimeMillis();
                if (nowTime3 - firstTime > 3000) {
                    isQuanZY = false;
                    splitService = null;
                    //点击测量
                    sendMessage(MEASURE);
                    tvInt = 3;
                    firstTime = nowTime3;
                } else {
                    ChangChunActivity.instance.showTip("请勿连续点击");
                }
                break;

            default:
                break;
        }
    }

    //////////////////////////////测量数据/////////////////////////

    /*
  * 全站仪返回的水平角,垂直角和斜距算出点的坐标值
  * */
    private QzyApplication.CImsPointEx getPointCoordinate(QzyApplication.CPDObvValue testPoint, QzyApplication.CImsPointEx outPoint) {
        // return formulaTools.coordinateFromHzVD(1, testPoint, outPoint);
        if (testPoint.m_dVAngle < PI) {
            return formulaTools.coordinateFromHzVD(1, testPoint, outPoint);
        } else {
            return formulaTools.coordinateFromHzVD(2, testPoint, outPoint);
        }
    }

    /*
* 与全站仪进行数据交换
* */
    Timer timer = new Timer();
    int num = 0;

    public void sendMessage(final String mesg) {
        write(mesg);
        LogUtils.d("messages==" + mesg);

        // 实例化Timer类
        num = 0;
        timer.schedule(new TimerTask() {
            public void run() {
                LogUtils.d("方法执行了");
                readDates();
            }
        }, 2000);// 这里百毫秒

    }

    /**
     * 传输数据
     *
     * @param message
     */
    public void write(String message) {

        try {

            if (outputStream != null) {
                outputStream.write(message.getBytes("UTF-8"));
            } else {
                LogUtils.d("os为空");
            }
            LogUtils.e("write:" + message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取全站仪返回的信息
     * readDate 为自定义的接收变量
     */
    public void readDates() {
        try {
            if (inputStream != null) {
                //修改流读取功能
                int connum = 0;
                byte[] byteRead = null;
                while (connum == 0) {
                    for (int i = 0; i < 5; i++) {
                        try {
                            Thread.sleep(150);
                            byteRead = new byte[inputStream.available()];
                            LogUtils.d("byte的长度为;"+byteRead.length+"循环次数"+i);
                            connum = inputStream.available();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    LogUtils.d("byte的长度为;"+byteRead.length);
                }

                //byte[] byteRead = new byte[inputStream.available()];
                if (byteRead.length > 0) {
                    inputStream.read(byteRead, 0, byteRead.length);
                    readDate = new String(byteRead, "UTF-8");
                    split = readDate.split("[,]");
                    if (split.length == 7) {
                        LogUtils.d("所有数据==" + split.length + "///" + split[0] + split[1] + split[2] + "//" + getFourPoint(split[3]) + "//"
                                + getFourPoint(split[4]) + "//" + getFourPoint(split[5]));
                        handler.sendEmptyMessage(0);

                    } else {
                        LogUtils.d("所有数据==" + split.length + "///" + split[0]);
                        split = null;
                        LogUtils.d(className+"spilt已至空");
                    }
                } else {
                    //超时后不在读取数据
                    if (num < 15) {
                        timer.schedule(new TimerTask() {
                            public void run() {
                                LogUtils.d("方法执行了");
                                num++;
                                readDates();

                            }
                        }, 500);// 这里毫秒
                        LogUtils.d("num=" + num);
                    } else {
                        ChangChunActivity.instance.showTip("连接异常");
                    }
                }
            } else {
                ChangChunActivity.instance.showTip("数据异常");
            }
        } catch (IOException e) {
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

    /*
 *  处理返回的坐标数据保留小数点后四位,四舍五入
 * */
    private double getFourDouble(String point) {
        double value = Double.parseDouble(point);
        return value;
    }

    /*
    *  处理返回的坐标数据保留小数点后六位,四舍五入
    * */
    private String getSixPoint(String point) {
        double value = Double.parseDouble(point);
        String result = String.format("%.6f", value);
        LogUtils.d("返回的结果" + result);
        return result;
    }

    //输入挠度点和两端点的坐标,计算出挠度值
    private void measureResult(QzyApplication.CImsPointEx point1, QzyApplication.CImsPointEx point2,
                               QzyApplication.CImsPointEx point3, QzyApplication.CImsPointEx point4) {
        result = formulaTools.measureDeflection(point1, point2, point3, point4);
        if (point2.Z > point4.Z) {
            llResult.setVisibility(View.VISIBLE);
            tvResult.setText("上挠度值为;");
            tvResultValue.setText(getFourPoint(String.valueOf(result)));
        } else {
            llResult.setVisibility(View.VISIBLE);
            tvResult.setText("下挠度值为;");
            tvResultValue.setText(getFourPoint(String.valueOf(result)));
        }

    }

    //弹出框,确认orient1和orient2点都已经测量完毕
    public void showMeasureDialog() {
        final AlertDialogToast dialog = new AlertDialogToast(DeflectionActivity.this);
        dialog.setTitle("确认三个点都已经测量完毕?");
        dialog.setPositiveButton("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                measureResult(point11, point2, point3, point4);
                dialog.dismiss();
            }
        });


        dialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            //onServiceDisconnected()方法在正常情况下是不被调用的，它的调用时机是当Service服务被异外销毁时     ，例如内存的资源不足时
        }

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder binder) {
            MessageService.MyBinder mybinder = (MessageService.MyBinder) binder;
            MessageService myservice = mybinder.getService();  //获得该服务
            //在这里获取有关服务的各种信息包括状态等等
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
        LogUtils.d("服务接触绑定");
    }
}
