package com.example.ccqzy.activitys;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ccqzy.ChangChunActivity;
import com.example.ccqzy.QzyApplication;
import com.example.ccqzy.R;
import com.example.ccqzy.androidutils.FormulaTools;
import com.example.ccqzy.beans.BuildStationPoint;
import com.example.ccqzy.services.MessageService;
import com.lidroid.xutils.util.LogUtils;

import org.xutils.DbManager;
import org.xutils.db.Selector;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

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

public class AddPointActivity extends Activity implements View.OnClickListener {

    /*
    * 定义控件
    * */
    private TextView tvAddPoint; //添加点
    private TextView tvSavePoint;//保存点
    private TextView tvCancle;//取消
    private TextView tvListTable;//展示列表
    private TextView tvMeasure;

    private EditText etNumber;//序号
    private EditText etPointName;//点名
    private EditText etX;
    private EditText etY;
    private EditText etZ;
    private EditText etHigh;//仪器高
    private EditText etNote;//备注

    //接收添加的数据
    String number;
    String pointName;
    String X;
    String Y;
    String Z;
    String high;
    String note;

    DbManager dbStationPoint;
    List<BuildStationPoint> allPoint = new ArrayList<>();
    String itemId;
    boolean isHaveValue = false;
    boolean isBound = false;
    //接收仪器返回的坐标值弧度值
    QzyApplication.CPDObvValue point1;
    QzyApplication.CImsPointEx pointx;

    FormulaTools formulaTools;
    long firstTime = 0;

    public static AddPointActivity instance;

    public AddPointActivity() {
        instance = AddPointActivity.this;
    }

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
                            etX.setText(getFourPoint(pointx.X));
                            etY.setText(getFourPoint(pointx.Y));
                            etZ.setText(getFourPoint(pointx.Z));
                            ChangChunActivity.instance.getVoice();
                        }

                    } else {
                        splitService = null;
                        LogUtils.d("全站仪里没有数据!!");
                    }
                    ///////////////////////以上为修改的数据

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

                        //得到车体坐标系上的坐标
                        if (abs(cimcs.X) > 0 || abs(cimcs.Y) > 0 || abs(cimcs.Z) > 0) {
                            pointx = formulaTools.TranslateMeaPoint(cimcs, rotMatrix, pointx);
                        }
                        etX.setText(getFourPoint(pointx.X));
                        etY.setText(getFourPoint(pointx.Y));
                        etZ.setText(getFourPoint(pointx.Z));
                        //蜂鸣
                        ChangChunActivity.instance.getVoice();

                        isQuanZY = true;
                        splitService = null;
                    } else {
                        LogUtils.d("split[]里没有数据!!");
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_point);

        //标题栏透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        className = "AddPointActivity";
        formulaTools = new FormulaTools();

        //初始化建站点的数据库
        dbStationPoint = x.getDb(((QzyApplication) getApplication()).getBuildStationConfig());
        itemId = getIntent().getStringExtra("itemId");
        //初始化头部标题
        initTitle();
        // 初始化view控件
        initView();

        queryPoint();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(AddPointActivity.this, MessageService.class);
        isBound = bindService(intent, connection, Context.BIND_AUTO_CREATE);
        LogUtils.d("服务开启了");
    }

    /*
   * 初始化标题头
   * */
    private void initTitle() {
        TextView back = findView(R.id.tv_title_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPointActivity.this.finish();
            }
        });

        TextView title = findView(R.id.tv_title_name);
        title.setText("添加点坐标");
    }

    /*
    * 初始化view
    * */
    private void initView() {
        tvAddPoint = findView(R.id.tv_add_point);
        tvSavePoint = findView(R.id.tv_save_point);
        tvCancle = findView(R.id.tv_cancle_point);
        tvListTable = findView(R.id.tv_measure_result);
        tvMeasure = findView(R.id.tv_measure_btn);
        etNumber = findView(R.id.tv_number);
        etPointName = findView(R.id.tv_point_number);
        etX = findView(R.id.tv_pointX);
        etY = findView(R.id.tv_pointY);
        etZ = findView(R.id.tv_pointH);
        etHigh = findView(R.id.tv_equipmentH);
        etNote = findView(R.id.tv_remarks);

        tvAddPoint.setOnClickListener(this);
        tvSavePoint.setOnClickListener(this);
        tvCancle.setOnClickListener(this);
        tvListTable.setOnClickListener(this);
        tvMeasure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_save_point:

                number = etNumber.getText().toString();
                pointName = etPointName.getText().toString();
                X = etX.getText().toString();
                Y = etY.getText().toString();
                Z = etZ.getText().toString();
                high = etHigh.getText().toString();
                note = etNote.getText().toString();

                LogUtils.d("保存数据的时间;" + number + "///" + pointName + "///" + X
                        + "///" + Y + "///" + Z + "///" + high + "///" + note);
                if (!number.equals("") && !pointName.equals("")) {
                    try {
                        if (isHaveValue) {
                            update(itemId);
                        } else {
                            insertPointName();
                        }
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                } else {
                    ChangChunActivity.instance.showTip("序号,点名,不能为空!");
                }
                break;
            case R.id.tv_cancle_point:
                //点击退出该界面
                AddPointActivity.this.finish();

                break;
            case R.id.tv_measure_result:

                //点击进入列表界面
                intent = new Intent(AddPointActivity.this, PointListActivity.class);
                startActivity(intent);
                AddPointActivity.this.finish();
                break;
            case R.id.tv_measure_btn:
                long nowTime = System.currentTimeMillis();
                if (nowTime - firstTime > 3000) {
                    isQuanZY = false;
                    splitService = null;
                    //点击测量
                    sendMessage(MEASURE);

                    firstTime = nowTime;
                } else {
                    Toast.makeText(AddPointActivity.this, "请勿连续点击!", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }

    }

      /*
* 发送信息
* */

    Timer timer = new Timer(); // 实例化Timer类
    int num = 0;

    private void sendMessage(final String mesg) {
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
                            Thread.sleep(200);
                            byteRead = new byte[inputStream.available()];
                            LogUtils.d("byte的长度为;" + byteRead.length + "循环次数" + i);
                            connum = inputStream.available();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    LogUtils.d("byte的长度为;" + byteRead.length);
                }

                // byte[] byteRead = new byte[inputStream.available()];
                if (byteRead.length > 0) {
                    inputStream.read(byteRead, 0, byteRead.length);
                    if (split != null) {
                        split = null;
                    }
                    readDate = new String(byteRead, "UTF-8");
                    split = readDate.split("[,]");
                    if (split.length == 7) {
                        LogUtils.d("所有数据==" + split.length + "///" + split[0] + split[1] + split[2] + "//" + split[3] + "//"
                                + split[4] + "//" + split[5]);
                        handler.sendEmptyMessage(0);
                    } else {
                        LogUtils.d("所有数据==" + split.length + "///" + split[0]);
                        split = null;
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
                        ChangChunActivity.instance.showTip("数据异常");
                    }
                }
            } else {
                ChangChunActivity.instance.showTip("仪器连接异常");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    /*
    * 自定义初始化控件方法
    * */
    public <T> T findView(int view) {
        return (T) super.findViewById(view);
    }

    //点数据添加
    private void insertPointName() throws DbException {

        String id = UUID.randomUUID().toString();
        BuildStationPoint user = null;
        user = new BuildStationPoint(id, Integer.valueOf(number),
                pointName, X, Y, Z, high, note);
        LogUtils.d("保存数据的时间;" + id);


        try {
            dbStationPoint.save(user);
            ChangChunActivity.instance.showTip("保存点成功,请到列表查看");
            LogUtils.d("添加数据到模板点导出模板点数据库成功" + dbStationPoint.toString());
            cancleEt();
        } catch (DbException e) {

        }
    }

    //更改数据
    public void update(String id) {
        try {
            BuildStationPoint user = new BuildStationPoint();
            user.setId(id);
            user.setOrderNum(Integer.valueOf(number));
            user.setPointName(pointName);
            user.setX(X);
            user.setY(Y);
            user.setH(Z);

            dbStationPoint.update(user, "orderNum", "pointName", "x", "y", "h");
            LogUtils.d("模板数据更新成功");
            ChangChunActivity.instance.showTip("保存点成功,请到列表查看");
            //更新列表//应该更新单条目
            queryPoint();

        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /*
    * 清除上次添加的数据
    * */
    private void cancleEt() {
        etNumber.setText("");
        etPointName.setText("");
        etX.setText("");
        etY.setText("");
        etZ.setText("");
        etHigh.setText("");
        etNote.setText("");
    }

    //从数据库中获取数据
    public void queryPoint() {

        try {
            Selector<BuildStationPoint> selector = dbStationPoint.selector(BuildStationPoint.class);
            allPoint = selector.findAll();
            if (allPoint != null) {
                LogUtils.d("详情的建站坐标点数据库db=" + allPoint + allPoint.size());
                for (int i = 0; i < allPoint.size(); i++) {
                    if (allPoint.get(i).getId().equals(itemId)) {
                        isHaveValue = true;
                        etNumber.setText(String.valueOf(allPoint.get(i).getOrderNum()));
                        etPointName.setText(allPoint.get(i).getPointName());
                        etX.setText(allPoint.get(i).getX());
                        etY.setText(allPoint.get(i).getY());
                        etZ.setText(allPoint.get(i).getH());
                    }
                }

            } else {
                LogUtils.d("没有数据");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    private double getFourDouble(String point) {
        double value = Double.parseDouble(point);
        return value;
    }

    /*
  *  处理返回的坐标数据保留小数点后四位,四舍五入
  * */
    private String getFourPoint(double value) {
        String result = String.format("%.3f", value);
        LogUtils.d("返回的结果" + result);
        return result;
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
