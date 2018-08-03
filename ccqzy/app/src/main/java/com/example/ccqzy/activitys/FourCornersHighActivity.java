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
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ccqzy.ChangChunActivity;
import com.example.ccqzy.QzyApplication;
import com.example.ccqzy.R;
import com.example.ccqzy.androidutils.FormulaTools;
import com.example.ccqzy.services.MessageService;
import com.lidroid.xutils.util.LogUtils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.ccqzy.QzyApplication.MEASURE;
import static com.example.ccqzy.QzyApplication.cimcs;
import static com.example.ccqzy.QzyApplication.className;
import static com.example.ccqzy.QzyApplication.inputStream;
import static com.example.ccqzy.QzyApplication.isQuanZY;
import static com.example.ccqzy.QzyApplication.outputStream;
import static com.example.ccqzy.QzyApplication.rotMatrix;
import static com.example.ccqzy.QzyApplication.splitService;
import static java.lang.Math.PI;
import static java.lang.Math.abs;

public class FourCornersHighActivity extends Activity implements View.OnClickListener {

    int tvInt = 1;
    /*
    * 四个foke点的变化量
    * */
    private TextView tvFokeL1;
    private TextView tvFokeL2;
    private TextView tvFokeR1;
    private TextView tvFokeR2;

    /*
    * 四个原始foke点
    * */
    private EditText tvOriginalFoke1;
    private EditText tvOriginalFoke2;
    private EditText tvOriginalFoke3;
    private EditText tvOriginalFoke4;

    /*
   * 四个测量foke点
   * */
    private TextView tvMeasureFoke1;
    private TextView tvMeasureFoke2;
    private TextView tvMeasureFoke3;
    private TextView tvMeasureFoke4;

    /**
     * 四个foke点的测量按钮
     */
    private RelativeLayout rlMeasureFoke1;
    private RelativeLayout rlMeasureFoke2;
    private RelativeLayout rlMeasureFoke3;
    private RelativeLayout rlMeasureFoke4;

    //升降值
    private RelativeLayout rlFokeL1;
    private RelativeLayout rlFokeL2;
    private RelativeLayout rlFokeR1;
    private RelativeLayout rlFokeR2;

    //string类型的测量foke点
    String measuringFokeL1;
    String measuringFokeL2;
    String measuringFokeL3;
    String measuringFokeL4;

    //string类型的原始foke点
    String originalFoke1;
    String originalFoke2;
    String originalFoke3;
    String originalFoke4;

    //接收仪器返回的坐标值弧度值
    QzyApplication.CPDObvValue point1;
    QzyApplication.CImsPointEx pointx;

    FormulaTools formulaTools;
    //判断服务是否已经解绑
    private boolean isBound = false;

    public static FourCornersHighActivity instance;

    public FourCornersHighActivity() {
        instance = FourCornersHighActivity.this;
    }

    //全站仪返回的数据处理方法
    public Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //////////////////////////修改的数据
                    if (splitService != null && isQuanZY && (splitService.length == 6 ||splitService.length==5)) {

                        pointx = new QzyApplication.CImsPointEx();
                        pointx.X = getFourDouble(splitService[2]) * 1000;
                        pointx.Y = -(getFourDouble(splitService[1]) * 1000);
                        pointx.Z = getFourDouble(splitService[3]) * 1000;
                        LogUtils.d("pointx=" + pointx.X + "y=" + pointx.Y + "z=" + pointx.Z);

                        //得到车体坐标系上的坐标
                        if (cimcs.X > 0 || cimcs.Y > 0 || cimcs.Z > 0) {
                            pointx = formulaTools.TranslateMeaPoint(cimcs, rotMatrix, pointx);
                        }

                        if (tvInt == 1) {
                            tvMeasureFoke1.setText(getFourPoint(String.valueOf(pointx.Z)));
                            rlMeasureFoke1.setBackgroundColor(Color.parseColor("#08f710"));
                        } else if (tvInt == 2) {
                            tvMeasureFoke2.setText(getFourPoint(String.valueOf(pointx.Z)));
                            rlMeasureFoke2.setBackgroundColor(Color.parseColor("#08f710"));
                        } else if (tvInt == 3) {
                            tvMeasureFoke3.setText(getFourPoint(String.valueOf(pointx.Z)));
                            rlMeasureFoke3.setBackgroundColor(Color.parseColor("#08f710"));
                        } else if (tvInt == 4) {
                            tvMeasureFoke4.setText(getFourPoint(String.valueOf(pointx.Z)));
                            rlMeasureFoke4.setBackgroundColor(Color.parseColor("#08f710"));
                        }
                        //蜂鸣
                        ChangChunActivity.instance.getVoice();
                        //自动寻找下一个测量点
                        if (tvInt == 4) {
                            tvInt = 1;
                        } else {
                            tvInt = tvInt + 1;
                        }

                    } else {
                        splitService = null;
                        LogUtils.d("全站仪里没有数据!!");
                    }
                    ///////////////////////修改的数据

                    if (split != null && split.length == 7) {
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

                        if (abs(cimcs.X) > 0 || abs(cimcs.Y) > 0 || abs(cimcs.Z) > 0) {
                            pointx = formulaTools.TranslateMeaPoint(cimcs, rotMatrix, pointx);
                        }

                        if (tvInt == 1) {
                            tvMeasureFoke1.setText(getFourPoint(String.valueOf(pointx.Z)));
                            rlMeasureFoke1.setBackgroundColor(Color.parseColor("#08f710"));
                            isQuanZY = true;
                            splitService = null;
                            split = null;
                        } else if (tvInt == 2) {
                            tvMeasureFoke2.setText(getFourPoint(String.valueOf(pointx.Z)));
                            rlMeasureFoke2.setBackgroundColor(Color.parseColor("#08f710"));
                            isQuanZY = true;
                            splitService = null;
                            split = null;
                        } else if (tvInt == 3) {
                            tvMeasureFoke3.setText(getFourPoint(String.valueOf(pointx.Z)));
                            rlMeasureFoke3.setBackgroundColor(Color.parseColor("#08f710"));
                            isQuanZY = true;
                            splitService = null;
                            split = null;
                        } else if (tvInt == 4) {
                            tvMeasureFoke4.setText(getFourPoint(String.valueOf(pointx.Z)));
                            rlMeasureFoke4.setBackgroundColor(Color.parseColor("#08f710"));
                            isQuanZY = true;
                            splitService = null;
                            split = null;
                        }
                        //蜂鸣
                        ChangChunActivity.instance.getVoice();

                        //自动寻找下一个测量点
                        if (tvInt == 4) {
                            tvInt = 1;
                        } else {
                            tvInt = tvInt + 1;
                        }
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four_corners_high);
        //设置导航栏透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        className = "FourCornersHighActivity";
        formulaTools = new FormulaTools();

        initTitle();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(FourCornersHighActivity.this, MessageService.class);
        isBound = bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    /*
        * 初始化view控件
        * */
    private void initView() {
        tvFokeL1 = (TextView) findViewById(R.id.tv_foke_l1);
        tvFokeL2 = (TextView) findViewById(R.id.tv_foke_l2);
        tvFokeR1 = (TextView) findViewById(R.id.tv_foke_r1);
        tvFokeR2 = (TextView) findViewById(R.id.tv_foke_r2);

        tvOriginalFoke1 = findViewById(R.id.tv_original_foke1);
        tvOriginalFoke2 = findViewById(R.id.tv_original_foke2);
        tvOriginalFoke3 = findViewById(R.id.tv_original_foke3);
        tvOriginalFoke4 = findViewById(R.id.tv_original_foke4);

        tvMeasureFoke1 = (TextView) findViewById(R.id.tv_measuring_foke1);
        tvMeasureFoke2 = (TextView) findViewById(R.id.tv_measuring_foke2);
        tvMeasureFoke3 = (TextView) findViewById(R.id.tv_measuring_foke3);
        tvMeasureFoke4 = (TextView) findViewById(R.id.tv_measuring_foke4);

        rlMeasureFoke1 = (RelativeLayout) findViewById(R.id.rl_measuring_foke_l1);
        rlMeasureFoke2 = (RelativeLayout) findViewById(R.id.rl_measuring_foke_l2);
        rlMeasureFoke3 = (RelativeLayout) findViewById(R.id.rl_measuring_foke_l3);
        rlMeasureFoke4 = (RelativeLayout) findViewById(R.id.rl_measuring_foke_l4);

        rlFokeL1 = (RelativeLayout) findViewById(R.id.rl_foke_l1);
        rlFokeL2 = (RelativeLayout) findViewById(R.id.rl_foke_l2);
        rlFokeR1 = (RelativeLayout) findViewById(R.id.rl_foke_r1);
        rlFokeR2 = (RelativeLayout) findViewById(R.id.rl_foke_r2);

        rlMeasureFoke1.setOnClickListener(this);
        rlMeasureFoke2.setOnClickListener(this);
        rlMeasureFoke3.setOnClickListener(this);
        rlMeasureFoke4.setOnClickListener(this);

        rlFokeL1.setOnClickListener(this);
        rlFokeL2.setOnClickListener(this);
        rlFokeR1.setOnClickListener(this);
        rlFokeR2.setOnClickListener(this);

    }

    /*
    * 初始化标题头
    * */
    private void initTitle() {

        TextView back = (TextView) findViewById(R.id.tv_title_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FourCornersHighActivity.this.finish();
            }
        });

        TextView title = (TextView) findViewById(R.id.tv_title_name);
        title.setText("四角高测平");
    }

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

    long firstTime = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_foke_l1:
                    //点击显示以该测量点变为0其它点的变化量
                    findLeveling(1);

                break;

            case R.id.rl_foke_l2:
                //点击显示以该测量点变为0其它点的变化量
                findLeveling(2);

                break;
            case R.id.rl_foke_r1:
                //点击显示以该测量点变为0其它点的变化量
                findLeveling(3);

                break;
            case R.id.rl_foke_r2:

                //点击显示以该测量点变为0其它点的变化量
              findLeveling(4);

                break;
            case R.id.rl_measuring_foke_l1:
                //点击测量点坐标,并把该点的z值赋给该textview上
                long nowTime = System.currentTimeMillis();
                if (nowTime-firstTime>3000) {
                    firstTime = nowTime;
                    tvInt = 1;
                    sendMessage(MEASURE);
                }else {
                    ChangChunActivity.instance.showTip("请勿连续点击");
                }


//                tvInt = 1;
//                sendMessage(MEASURE);

                break;
            case R.id.rl_measuring_foke_l2:
                //点击测量点坐标,并把该点的z值赋给该textview上
                long nowTime2 = System.currentTimeMillis();
                LogUtils.d("现在时间是============"+nowTime2+"//firstTime========"+firstTime);
                if (nowTime2-firstTime>3000) {
                    firstTime = nowTime2;
                    tvInt = 2;
                    sendMessage(MEASURE);
                }else {
                    ChangChunActivity.instance.showTip("请勿连续点击");
                }

//                tvInt = 2;
//                sendMessage(MEASURE);

                break;
            case R.id.rl_measuring_foke_l3:
                //点击测量点坐标,并把该点的z值赋给该textview上
                long nowTime3 = System.currentTimeMillis();
                if (nowTime3-firstTime>3000) {
                    firstTime = nowTime3;
                    tvInt = 3;
                    sendMessage(MEASURE);
                }else {
                    ChangChunActivity.instance.showTip("请勿连续点击");
                }

//                tvInt = 3;
//                sendMessage(MEASURE);

                break;
            case R.id.rl_measuring_foke_l4:
                //点击测量点坐标,并把该点的z值赋给该textview上
                long nowTime4 = System.currentTimeMillis();
                if (nowTime4-firstTime>3000) {
                    firstTime = nowTime4;
                    tvInt = 4;
                    sendMessage(MEASURE);
                }else {
                    ChangChunActivity.instance.showTip("请勿连续点击");
                }

//                tvInt = 4;
//                sendMessage(MEASURE);

                break;

            default:
                break;
        }

    }

    /*
    * 点击某个foke值,得到其变化量
    * */
    private void findLeveling(int fokeNum) {
        measuringFokeL1 = tvMeasureFoke1.getText().toString();
        measuringFokeL2 = tvMeasureFoke2.getText().toString();
        measuringFokeL3 = tvMeasureFoke3.getText().toString();
        measuringFokeL4 = tvMeasureFoke4.getText().toString();

        originalFoke1 = tvOriginalFoke1.getText().toString();
        originalFoke2 = tvOriginalFoke2.getText().toString();
        originalFoke3 = tvOriginalFoke3.getText().toString();
        originalFoke4 = tvOriginalFoke4.getText().toString();
        LogUtils.d("得到的原始数据=" + originalFoke1 + "//" + originalFoke2 + "////" + originalFoke3 + "//" + originalFoke4
                + "测量数据=" + measuringFokeL1 + "///" + measuringFokeL2 + "//" + measuringFokeL3 + "///" + measuringFokeL4);

        if (!measuringFokeL1.equals("") && !measuringFokeL2.equals("")
                && !measuringFokeL1.equals("") && !measuringFokeL2.equals("")
                && !originalFoke1.equals("") && !originalFoke2.equals("")
                && !originalFoke3.equals("") && !originalFoke3.equals("")) {

            //四个差值
            double l1 = Double.parseDouble(measuringFokeL1) - Double.parseDouble(originalFoke1);
            double l2 = Double.parseDouble(measuringFokeL2) - Double.parseDouble(originalFoke2);
            double l3 = Double.parseDouble(measuringFokeL3) - Double.parseDouble(originalFoke3);
            double l4 = Double.parseDouble(measuringFokeL4) - Double.parseDouble(originalFoke4);
            LogUtils.d("差值为;" + l1 + "/r" + l2 + "//" + l3 + "//" + l4);

            if (fokeNum == 1) {
                tvFokeL1.setText("0.000");
                tvFokeL2.setText(getFourPoint(String.valueOf(l2 - l1)));
                tvFokeR1.setText(getFourPoint(String.valueOf(l3 - l1)));
                tvFokeR2.setText(getFourPoint(String.valueOf(l4 - l1)));
            } else if (fokeNum == 2) {
                tvFokeL2.setText("0.000");
                tvFokeL1.setText(getFourPoint(String.valueOf(l1 - l2)));
                tvFokeR1.setText(getFourPoint(String.valueOf(l3 - l2)));
                tvFokeR2.setText(getFourPoint(String.valueOf(l4 - l2)));
            } else if (fokeNum == 3) {
                tvFokeR1.setText("0.000");
                tvFokeL1.setText(getFourPoint(String.valueOf(l1 - l3)));
                tvFokeL2.setText(getFourPoint(String.valueOf(l2 - l3)));
                tvFokeR2.setText(getFourPoint(String.valueOf(l4 - l3)));
            } else if (fokeNum == 4) {
                tvFokeR2.setText("0.000");
                tvFokeL1.setText(getFourPoint(String.valueOf(l1 - l4)));
                tvFokeR1.setText(getFourPoint(String.valueOf(l3 - l4)));
                tvFokeL2.setText(getFourPoint(String.valueOf(l2 - l4)));
            }

            LogUtils.d("获取数据成功!!!");
        } else {
            ChangChunActivity.instance.showTip("任何一个foke值都不能为空");

        }

    }


    /*
* 发送信息
* */

    Timer timer = new Timer(); // 实例化Timer类
    int num = 0;

    private void sendMessage(final String mesg) {
        isQuanZY = false;
        splitService = null;
        write(mesg);
        LogUtils.d("messages==" + mesg);


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

    /*
    * 读取全站仪返回的信息
    * readDate 为自定义的接收变量
    * */
    String readDate;
    String[] split;

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
                            LogUtils.d("byte的长度为;" + byteRead.length + "循环次数" + i);
                            connum = inputStream.available();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    LogUtils.d("byte的长度为;" + byteRead.length);
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
                        LogUtils.d(className + "split已至空");
                    }
                } else {
                    // 实例化Timer类
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
   *  处理返回的坐标数据保留小数点后六位,四舍五入
   * */
    private String getSixPoint(String point) {
        double value = Double.parseDouble(point);
        String result = String.format("%.6f", value);
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
