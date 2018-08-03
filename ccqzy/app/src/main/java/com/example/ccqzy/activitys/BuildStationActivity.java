package com.example.ccqzy.activitys;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ccqzy.ChangChunActivity;
import com.example.ccqzy.QzyApplication;
import com.example.ccqzy.R;
import com.example.ccqzy.adapters.OrientPointAdapter;
import com.example.ccqzy.androidutils.AlertDialogToast;
import com.example.ccqzy.androidutils.FormulaTools;
import com.example.ccqzy.beans.CimcsBean;
import com.example.ccqzy.beans.StationPoint;
import com.example.ccqzy.services.BuildStaionService;
import com.lidroid.xutils.util.LogUtils;

import org.xutils.DbManager;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.ccqzy.QzyApplication.TURNOVER;
import static com.example.ccqzy.QzyApplication.cimcs;
import static com.example.ccqzy.QzyApplication.className;
import static com.example.ccqzy.QzyApplication.inputStream;
import static com.example.ccqzy.QzyApplication.isConnect;
import static com.example.ccqzy.QzyApplication.isQuanZY;
import static com.example.ccqzy.QzyApplication.outputStream;
import static com.example.ccqzy.QzyApplication.qzyPoint;
import static com.example.ccqzy.QzyApplication.rotMatrix;
import static com.example.ccqzy.QzyApplication.splitService;
import static java.lang.Math.PI;

public class BuildStationActivity extends Activity implements View.OnClickListener {

    /**
     * tvMeasure       测量
     * tvAddPoint      加点
     * tvCanclePoint   删点
     * tvResult        计算
     * tvIsconnected   蓝牙连接状态
     * rlResult        显示结果的区域
     * tvX             坐标系的x
     * tvY             坐标系的y
     * tvZ             坐标系的z
     * cimcs           坐标系对象
     */
    private TextView tvMeasure;
    private TextView tvAddPoint;
    private TextView tvCanclePoint;
    private TextView tvResult;

    private TextView tvImport;
    private TextView tvIsconnected;
    private ListView lvStation;

    private RelativeLayout rlResult;
    private TextView tvX;
    private TextView tvY;
    private TextView tvZ;

    //计算的工具类
    FormulaTools formulaTools;
    //单条目的id
    public String itemId;

    //一面点,二面点
    QzyApplication.CPDObvValue point1 = null;
    QzyApplication.CPDObvValue point2 = null;
    //平均点
    QzyApplication.CPDObvValue averagePoint;

    //直接从全站仪上读取的坐标点
    QzyApplication.CImsPointEx qzyPoint1 = null;
    QzyApplication.CImsPointEx qzyPoint2 = null;
    QzyApplication.CImsPointEx qzyPT;

    OrientPointAdapter opAdapter;
    AdapterView.OnItemClickListener mListOnItemClick;

    //测量数据存储集合
    String readDate;
    String[] split;

    //建站点列表数据库
    DbManager dbStation;
    //用户信息数据库
    DbManager dbUser;
    public List<StationPoint> allPoint = new ArrayList<>();

    //点击按钮的第一次时间
    private long firstTime = 0;
    //判断服务是否已经解绑
    private boolean isBound = false;

    public static BuildStationActivity instance;

    public BuildStationActivity() {
        instance = BuildStationActivity.this;
    }

    int order = 0;
    //接收全站仪返回的信息
    public Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //全站仪红色按钮点击事件
                    if (splitService != null && isQuanZY && (splitService.length == 6 || splitService.length == 5)) {
                        qzyPT = new QzyApplication.CImsPointEx();
                        if (qzyPoint1 == null) {
                            qzyPoint1 = new QzyApplication.CImsPointEx();
                            qzyPoint1.X = getFourDouble(splitService[2]) * 1000;
                            qzyPoint1.Y = -(getFourDouble(splitService[1]) * 1000);
                            qzyPoint1.Z = getFourDouble(splitService[3]) * 1000;

                            String averageX = getFourPoint(String.valueOf(qzyPoint1.X));
                            String averageY = getFourPoint(String.valueOf(qzyPoint1.Y));
                            String averageH = getFourPoint(String.valueOf(qzyPoint1.Z));
                            //更新坐标
                            update(itemId, averageX, averageY, averageH);
                            LogUtils.d("全站仪一面坐标=x" + averageX + "///y=" + averageY + "///z=" + averageH);
                        } else {
                            qzyPoint2 = new QzyApplication.CImsPointEx();
                            qzyPoint2.X = getFourDouble(splitService[2]) * 1000;
                            qzyPoint2.Y = -(getFourDouble(splitService[1]) * 1000);
                            qzyPoint2.Z = getFourDouble(splitService[3]) * 1000;

                            String averageX = getFourPoint(String.valueOf(qzyPoint2.X));
                            String averageY = getFourPoint(String.valueOf(qzyPoint2.Y));
                            String averageH = getFourPoint(String.valueOf(qzyPoint2.Z));
                            //更新坐标
                            update(itemId, averageX, averageY, averageH);
                            LogUtils.d("全站仪二面坐标=x" + averageX + "///y=" + averageY + "///z=" + averageH);
                        }

                        if (qzyPoint1 != null && qzyPoint2 != null) {
                            qzyPT.X = (qzyPoint1.X + qzyPoint2.X) / 2;
                            qzyPT.Y = (qzyPoint1.Y + qzyPoint2.Y) / 2;
                            qzyPT.Z = (qzyPoint1.Z + qzyPoint2.Z) / 2;

                            String averageX = getFourPoint(String.valueOf(qzyPT.X));
                            String averageY = getFourPoint(String.valueOf(qzyPT.Y));
                            String averageH = getFourPoint(String.valueOf(qzyPT.Z));

                            LogUtils.d("000000全站仪坐标pointx=x" + qzyPT.X + "///y=" + qzyPT.Y + "///z=" + qzyPT.Z);

                            update(itemId, averageX, averageY, averageH);
                            qzyPoint1 = null;
                            qzyPoint2 = null;
                            qzyPT = null;
                            ChangChunActivity.instance.showTip("添加数据成功!");
                            itemId = allPoint.get(1).getId();
                        } else {
                            showBackDialog();
                        }

                    } else {
                        splitService = null;
                        LogUtils.d("红色按钮没有数据");
                    }

                    break;
                case 1:
                    //界面按钮点击事件
                    if (split != null && split.length == 7) {
                        //第一次测量返回的值 double类型,弧度坐标
                        averagePoint = new QzyApplication.CPDObvValue();
                        if (getFourDouble(split[4]) < PI) {
                            point1 = new QzyApplication.CPDObvValue();//第一面
                            point1.m_dHzAngle = getFourDouble(split[3]);//水平角
                            point1.m_dVAngle = getFourDouble(split[4]);//垂直角
                            point1.m_dSlopeDist = getFourDouble(split[5]) * 1000;//斜距(米)
                            LogUtils.d("0000001面值添加成功");

                            //一面测量的点添加到数据库
                            QzyApplication.CImsPointEx point = new QzyApplication.CImsPointEx();
                            point = getPointCoordinate(point1, point);
                            String averageX = getFourPoint(String.valueOf(point.X));
                            String averageY = getFourPoint(String.valueOf(point.Y));
                            String averageH = getFourPoint(String.valueOf(point.Z));
                            //更新坐标
                            update(itemId, averageX, averageY, averageH);
                            LogUtils.d("00000一面坐标=x" + averageX + "///y=" + averageY + "///z=" + averageH);

                        } else {
                            point2 = new QzyApplication.CPDObvValue();//翻面
                            point2.m_dHzAngle = getFourDouble(split[3]);//水平角
                            point2.m_dVAngle = getFourDouble(split[4]);//垂直角
                            point2.m_dSlopeDist = getFourDouble(split[5]) * 1000;//斜距(米)
                            LogUtils.d("0000000000002面值添加成功");
                            LogUtils.d("point2=" + point2.m_dHzAngle + "y=" + point2.m_dVAngle + "ds=" + point2.m_dSlopeDist);

                            //二面测量的点添加到数据库
                            QzyApplication.CImsPointEx point = new QzyApplication.CImsPointEx();
                            point = getPointCoordinate(point2, point);

                            String averageX = getFourPoint(String.valueOf(point.X));
                            String averageY = getFourPoint(String.valueOf(point.Y));
                            String averageH = getFourPoint(String.valueOf(point.Z));
                            update(itemId, averageX, averageY, averageH);
                            LogUtils.d("000000000二面坐标=x" + averageX + "///y=" + averageY + "///z=" + averageH);
                            point2.m_dHzAngle = getFourDouble(split[3]);//水平角
                            point2.m_dVAngle = getFourDouble(split[4]);//垂直角
                            point2.m_dSlopeDist = getFourDouble(split[5]) * 1000;//斜距(米)
                            LogUtils.d("point2=" + point2.m_dHzAngle + "y=" + point2.m_dVAngle + "ds=" + point2.m_dSlopeDist);

                        }
                        //判断是否翻面(粗略判断)
                        if (point1 != null && point2 != null) {
                            LogUtils.d("point1=" + point1.m_dHzAngle + "y=" + point1.m_dVAngle + "ds=" + point1.m_dSlopeDist);
                            LogUtils.d("point2=" + point2.m_dHzAngle + "y=" + point2.m_dVAngle + "ds=" + point2.m_dSlopeDist);
                            averagePoint = formulaTools.averagePoint(point1, point2, averagePoint);
                            QzyApplication.CImsPointEx point = new QzyApplication.CImsPointEx();
                            point = getPointCoordinate(averagePoint, point);

                            String averageX = getFourPoint(String.valueOf(point.X));
                            String averageY = getFourPoint(String.valueOf(point.Y));
                            String averageH = getFourPoint(String.valueOf(point.Z));

                            LogUtils.d("000000全站仪坐标pointx=x" + point.X + "///y=" + point.Y + "///z=" + point.Z);

                            update(itemId, averageX, averageY, averageH);
                            point1 = null;
                            point2 = null;
                            averagePoint = null;
                            LogUtils.d("000point1" + point1 + "///point2" + point2);
                            ChangChunActivity.instance.showTip("添加数据成功!");
                            isQuanZY = true;
                            splitService = null;
                            split = null;
                        } else {
                            showBackDialog();
                        }
                    } else {
                        LogUtils.d("split[]里没有数据!!");
                    }
                    break;
                case 2:

                    break;
            }
            return false;
        }
    });

    String bluetoothStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_station);

        //标题栏透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //建站的数据库
        dbStation = x.getDb(((QzyApplication) getApplication()).getStationConfig());
        dbUser = x.getDb(((QzyApplication) getApplication()).getCimcsConfig());
        formulaTools = new FormulaTools();
        className = "BuildStationActivity";

        //初始化标题头
        initTitle();
        //初始化控件
        initView();

        ///////////////////////////
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(mReceiver, filter);
        ///////////////////////////

        //点击列表每个条目时的事件响应
        mListOnItemClick = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                opAdapter.setSelectItem(arg2);
                opAdapter.notifyDataSetChanged();
                itemId = allPoint.get(arg2).getId();
                LogUtils.d("选中的id=" + itemId);
            }

        };

        queryPoint();

        if (allPoint == null) {
            try {
                insertPointName("0001", "orient1");
                insertPointName("0002", "orient2");
                itemId = allPoint.get(0).getId();
            } catch (DbException e) {
                e.printStackTrace();
            }
        } else {
            LogUtils.d("orient1和orient2已经存在");
            itemId = allPoint.get(0).getId();
        }

        LogUtils.d("选中的id=" + itemId);


    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(BuildStationActivity.this, BuildStaionService.class);
        isBound = bindService(intent, connection, Context.BIND_AUTO_CREATE);

        if (isConnect) {
            tvIsconnected.setText("蓝牙已连接......");
            tvIsconnected.setTextColor(Color.parseColor("#FF08F710"));

            content.setText("蓝牙已连接");
            content.setTextColor(Color.parseColor("#FF08F710"));
        } else {
            tvIsconnected.setText("蓝牙未连接......");
            tvIsconnected.setTextColor(Color.parseColor("#FFF70808"));

            content.setText("蓝牙未连接");
            content.setTextColor(Color.parseColor("#f70808"));
        }


    }

    TextView content;

    /*
   * 初始化标题头
   * */
    private void initTitle() {
        TextView back = findView(R.id.tv_title_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuildStationActivity.this.finish();
            }
        });

        TextView title = findView(R.id.tv_title_name);
        title.setText("建立测站");

        content = findView(R.id.tv_bluetooth_iscontent);
        content.setVisibility(View.VISIBLE);
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到蓝牙连接界面
                Intent intent = new Intent(BuildStationActivity.this, BluetoothActivity.class);
                startActivity(intent);
            }
        });
    }

    /*
    * 初始化view
    * */
    private void initView() {
        tvMeasure = findView(R.id.tv_measure_point);
        tvAddPoint = findView(R.id.tv_addpoint);
        tvCanclePoint = findView(R.id.tv_cancle_point);
        tvResult = findView(R.id.tv_measure_result);
        tvImport = findView(R.id.tv_import);

        rlResult = findView(R.id.rl_reback_result);
        tvX = findView(R.id.tv_direction_value);
        tvY = findView(R.id.tv_direction_errorX);
        tvZ = findView(R.id.tv_direction_errorY);
        lvStation = findView(R.id.lv_station_point);

        tvIsconnected = findView(R.id.tv_isconnected);

        tvImport.setOnClickListener(this);
        tvMeasure.setOnClickListener(this);
        tvAddPoint.setOnClickListener(this);
        tvCanclePoint.setOnClickListener(this);
        tvResult.setOnClickListener(this);


    }

    /*
    * 得到orient点列表
    * */
    public void getListDates(List<StationPoint> list) {
        opAdapter = new OrientPointAdapter(list, this);
        lvStation.setAdapter(opAdapter);
        lvStation.setOnItemClickListener(mListOnItemClick);
        opAdapter.notifyDataSetChanged();
    }

    //点击事件
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_import:

                if (cimcs.X == 0 && cimcs.Y == 0 && cimcs.Z == 0) {
                    ChangChunActivity.instance.showTip("请先建立测站");
                } else {
                    intent = new Intent(BuildStationActivity.this, DateMeasureActivity.class);
                    intent.putExtra("type", "");
                    startActivity(intent);
                    BuildStationActivity.this.finish();
                }
                break;
            case R.id.tv_measure_point:
                long nowTime = System.currentTimeMillis();
                if (nowTime - firstTime > 3000) {
                    //点击翻面
                    sendMessage(TURNOVER);
                    firstTime = nowTime;
                } else {
                    ChangChunActivity.instance.showTip("请勿连续点击!");
                }
                break;
            case R.id.tv_measure_result:

                //如果数据有数据为0,不能计算
                LogUtils.d("order====" + order);
                if (allPoint.get(0).getX().equals("0.0") || allPoint.get(1).getX().equals("0.0") || order < 6) {
                    ChangChunActivity.instance.showTip("orient1或orient2还没有测量完毕");
                } else {
                    showMeasureDialog();
                }

                break;

            default:
                break;
        }

    }

    /*
    *  求建站坐标系坐标,旋转角,缩放比例
    * */
    private void measureResult(List<StationPoint> mpoint) {

        //第一个点坐标x,y,z)
        QzyApplication.CImsPointEx cipex = new QzyApplication.CImsPointEx();
        //轴点坐标x,y,z)
        QzyApplication.CImsPointEx cipex1 = new QzyApplication.CImsPointEx();
        //orient2点坐标(x,y,z)
        QzyApplication.CImsPointEx cipex3 = new QzyApplication.CImsPointEx();

        if (mpoint.size() == 2) {
            //orient1点坐标
            cipex.X = Double.parseDouble(mpoint.get(0).getX());
            cipex.Y = Double.parseDouble(mpoint.get(0).getY());
            cipex.Z = Double.parseDouble(mpoint.get(0).getH());

            //orient2点坐标
            cipex3.X = Double.parseDouble(mpoint.get(1).getX());
            cipex3.Y = Double.parseDouble(mpoint.get(1).getY());
            cipex3.Z = Double.parseDouble(mpoint.get(1).getH());

            //轴点坐标
            cipex1.X = cipex3.X;
            cipex1.Y = cipex3.Y;
            cipex1.Z = cipex3.Z + 1000;

            //全站仪上的原点坐标
            qzyPoint.X = 0;
            qzyPoint.Y = 0;
            qzyPoint.Z = 0;

            //初始化旋转矩阵
            rotMatrix.A1 = 0;
            rotMatrix.A2 = 0;
            rotMatrix.A3 = 0;
            rotMatrix.B1 = 0;
            rotMatrix.B2 = 0;
            rotMatrix.B3 = 0;
            rotMatrix.C1 = 0;
            rotMatrix.C2 = 0;
            rotMatrix.C3 = 0;

            //车体坐标系的平移量和旋转角
            cimcs = formulaTools.CalCsParam(cimcs, cipex3, cipex1, cipex);
            LogUtils.d("建站坐标系cimcs=" + cimcs.X + cimcs.Y + "zuanjiao=" + cimcs.RotZ);
            try {
                Selector<CimcsBean> selector = dbUser.selector(CimcsBean.class);
                List<CimcsBean> userAll = selector.findAll();
                if (userAll == null) {
                    insertPointName();
                } else {
                    updateCimcs();
                    LogUtils.d("新车体坐标系更新");
                }

            } catch (DbException e) {
                e.printStackTrace();
            }

            //旋转矩阵
            formulaTools.CalRotMatrix(rotMatrix, cimcs);

            //初始化旋转矩阵
            rotMatrix.A1 = 0;
            rotMatrix.A2 = 0;
            rotMatrix.A3 = 0;
            rotMatrix.B1 = 0;
            rotMatrix.B2 = 0;
            rotMatrix.B3 = 0;
            rotMatrix.C1 = 0;
            rotMatrix.C2 = 0;
            rotMatrix.C3 = 0;

            // //全站仪在车体坐标系上的坐标
            qzyPoint = formulaTools.TranslateMeaPoint(cimcs, rotMatrix, qzyPoint);
            LogUtils.d("全站仪原点在车体坐标系上的坐标=" + qzyPoint.X + "//" + qzyPoint.Y + "//" + qzyPoint.Z);
            if (qzyPoint.X != 0) {
                rlResult.setVisibility(View.VISIBLE);
                tvX.setText("X ; " + getFourPoint(qzyPoint.X));
                tvY.setText("Y ; " + getFourPoint(qzyPoint.Y));
                tvZ.setText("Z ; " + getFourPoint(qzyPoint.Z));
            } else {
                LogUtils.d("全站仪原点在车体坐标系上的坐标=" + qzyPoint.X + "//" + qzyPoint.Y + "//" + qzyPoint.Z);
            }
            //改变原来的cimcs值

        } else {
            ChangChunActivity.instance.showTip("无效的计算!");
        }
    }

    /*
    * 全站仪返回的水平角,垂直角和斜距算出点的坐标值
    * */
    private QzyApplication.CImsPointEx getPointCoordinate(QzyApplication.CPDObvValue testPoint, QzyApplication.CImsPointEx outPoint) {
        if (testPoint.m_dVAngle < PI) {
            return formulaTools.coordinateFromHzVD(1, testPoint, outPoint);
        } else {
            return formulaTools.coordinateFromHzVD(2, testPoint, outPoint);
        }
    }

    /*
    * 获取建站成功后坐标系的旋转矩阵
    * */
    private QzyApplication.CImsRotMatrix getRotMatrix(QzyApplication.CImsRotMatrix rotMatrix, QzyApplication.CImCS rotAngle) {
        formulaTools.CalRotMatrix(rotMatrix, rotAngle);
        return rotMatrix;
    }

    Timer timer = new Timer();

    /*
    * 与全站仪进行数据交换
    * */
    int num = 0;

    public void sendMessage(final String mesg) {
        isQuanZY = false;
        splitService = null;
        split = null;
        write(mesg);
        LogUtils.d("messages==" + mesg);

        // 实例化Timer类
        num = 0;
        timer.schedule(new TimerTask() {
            public void run() {
                LogUtils.d("方法执行了");
                readDates();
            }
        }, 2000);// 这里毫秒

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
                ChangChunActivity.instance.showTip("通讯异常!,请重新连接蓝牙通讯");
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
        int con = 0;
        try {
            if (inputStream != null) {
                LogUtils.d("inputstrem的数量" + inputStream.available());
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
                    con++;
                    LogUtils.d("byte的长度为;" + byteRead.length);
                    if (con == 13) {
                        LogUtils.d("空循环跳出");
                        //ChangChunActivity.instance.showTip("请检测是否连接仪器");
                        break;
                    }
                }

                //byte[] byteRead = new byte[inputStream.available()];
                if (byteRead.length > 0) {
                    inputStream.read(byteRead, 0, byteRead.length);
                    readDate = new String(byteRead, "UTF-8");
                    split = readDate.split("[,]");
                    if (split.length == 7) {
                        LogUtils.d("所有数据==" + split.length + "///" + split[0] + split[1] + split[2] + "//" + getFourPoint(split[3]) + "//"
                                + getFourPoint(split[4]) + "//" + getFourPoint(split[5]));
                        handler.sendEmptyMessage(1);

                    } else {
                        LogUtils.d("所有数据==" + split.length + "///" + split[0]);
                        split = null;
                        isQuanZY = true;
                        splitService = null;
                        LogUtils.d(className + "split已至空");
                    }
                } else {
                    // 实例化Timer类
                    if (num < 5) {
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
                //如果通讯通道为空,提示为空
                ChangChunActivity.instance.showTip("通讯连接失败!,请重新连接蓝牙通讯");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 处理返回的坐标数据保留小数点后四位,四舍五入
     */
    private String getFourPoint(String point) {
        double value = Double.parseDouble(point);
        String result = String.format("%.3f", value);
        LogUtils.d("返回的结果" + result);
        return result;
    }

    /*
   *  处理返回的坐标数据保留小数点后四位,四舍五入
   * */
    private String getFourPoint(double point) {
        String result = String.format("%.3f", point);
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


    //从数据库中获取数据
    public void queryPoint() {

        try {
            Selector<StationPoint> selector = dbStation.selector(StationPoint.class);
            allPoint = selector.findAll();
            if (allPoint != null) {
                LogUtils.d("详情的建站坐标点数据库dbStation=" + allPoint + allPoint.size());
                getListDates(allPoint);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    //单条目删除添加的点
    public void deleteItemById(String id) {
        //通过自己构建条件来删除
        try {
            dbStation.delete(StationPoint.class, WhereBuilder.b("id", "=", id));
            LogUtils.d("删除成功,刷新成功");
            //参数解析 第一个参数是列名 第二个参数是条件= != > <等等条件，第三个参数为传递的值
            //如果条件参数不止一个的话，我们还可以使用.and("id", "=", id)方法
            //同理还有.or("id", "=", id)方法
            queryPoint();
        } catch (DbException e) {
        }
    }

    //更改某个数据
    public void update(String id, String x, String y, String h) {
        try {
            LogUtils.d("运行到这里了");
            StationPoint user = new StationPoint();
            user.setId(id);
            user.setX(x);
            user.setY(y);
            user.setH(h);
            dbStation.update(user, "x", "y", "h");
            LogUtils.d("dbStation数据更新成功");
            order++;
            //更新列表
            queryPoint();
            //蜂鸣
            ChangChunActivity.instance.getVoice();

        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    //更新新的坐标系
    public void updateCimcs() {
        try {
            CimcsBean user = new CimcsBean();
            user.setId("11");
            user.setX(cimcs.X);
            user.setY(cimcs.Y);
            user.setZ(cimcs.Z);
            user.setRotx(cimcs.RotX);
            user.setRoty(cimcs.RotY);
            user.setRotz(cimcs.RotZ);
            user.setScale(cimcs.Scale);
            dbUser.update(user, "x", "y", "z", "rotx", "roty", "rotz", "scale");
            LogUtils.d("更新cimcs数据成功");

        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    //点数据添加
    private void insertPointName() throws DbException {
        CimcsBean cimcsBean = null;
        cimcsBean = new CimcsBean("11", cimcs.X, cimcs.Y, cimcs.Z, cimcs.RotX, cimcs.RotY, cimcs.RotZ, cimcs.Scale);

        try {
            LogUtils.d("这里出现的问题" + cimcsBean.toString());
            dbUser.save(cimcsBean);
            LogUtils.d("添加数据到cimcs数据成功" + dbUser.toString());
        } catch (DbException e) {

        }
    }

    //点数据添加
    public void insertPointName(String id1, String orient) throws DbException {

        StationPoint user = null;
        user = new StationPoint(id1, "01", orient, "0.0", "0.0", "0.0", "0", "", "or1");
        LogUtils.d("保存数据的id=;" + user.getId());

        try {
            LogUtils.d("这里出现的问题" + user.toString());
            dbStation.save(user);
            LogUtils.d("添加数据到dbManagerTeast成功" + dbStation.toString());
            BuildStationActivity.instance.queryPoint();
        } catch (DbException e) {

        }
    }

    /*
    * 自定义初始化控件方法
    * */
    public <T> T findView(int view) {
        return (T) super.findViewById(view);
    }


    //弹出框,确认是否离开此界面
    public void showBackDialog() {
        final AlertDialogToast dialog = new AlertDialogToast(BuildStationActivity.this);
        dialog.setTitle("请反面,再测量一次");
        dialog.setPositiveButton("翻面", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(TURNOVER);
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

    //弹出框,确认orient1和orient2点都已经测量完毕
    public void showMeasureDialog() {
        final AlertDialogToast dialog = new AlertDialogToast(BuildStationActivity.this);
        dialog.setTitle("确认orient1和orient2点都已经测量完毕");
        dialog.setPositiveButton("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                measureResult(allPoint);
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
            // TODO Auto-generated method stub
            //onServiceDisconnected()方法在正常情况下是不被调用的，它的调用时机是当Service服务被异外销毁时     ，例如内存的资源不足时
        }

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder binder) {
            // TODO Auto-generated method stub
            BuildStaionService.MyBuildService mybinder = (BuildStaionService.MyBuildService) binder;
            BuildStaionService myservice = mybinder.getBuildS();  //获得该服务
            //在这里获取有关服务的各种信息包括状态等等
        }
    };

    ////////////////////注册监听蓝牙状态
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_DISCONNECTED:
                            content.setText("蓝牙未连接");
                            content.setTextColor(Color.parseColor("#f70808"));
                            tvIsconnected.setText("蓝牙未连接......");
                            tvIsconnected.setTextColor(Color.parseColor("#FFF70808"));
                            isConnect = false;
                            LogUtils.d("蓝牙已经断开了=============================");
                            break;
                        case BluetoothAdapter.STATE_CONNECTED:
                            LogUtils.d("蓝牙已经连接上了=============================");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            bluetoothStatus = "on";
                            LogUtils.d("TAG" + "STATE_ON");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            bluetoothStatus = "off";
                            content.setText("蓝牙未连接");
                            content.setTextColor(Color.parseColor("#f70808"));

                            tvIsconnected.setText("蓝牙未连接......");
                            tvIsconnected.setTextColor(Color.parseColor("#FFF70808"));
                            isConnect = false;
                            LogUtils.d("TAG" + "STATE_OFF");
                            break;
                        case BluetoothAdapter.STATE_DISCONNECTING:
                            LogUtils.d("SLDJFLASJDFLJAJF///DISCONNECTION");
                            break;
                        case BluetoothAdapter.ERROR:
                            LogUtils.d("连接错误//error");
                            break;
                        case BluetoothAdapter.STATE_CONNECTING:
                            LogUtils.d("连接信息//STATE_CONNECTING");
                            break;
                        case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                            LogUtils.d("连接信息//SCAN_MODE_CONNECTABLE_DISCOVERABLE");
                            break;
                    }
                    break;
            }
        }
    };

    ////////////////////////////////////


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
        LogUtils.d("服务接触绑定");
        order = 0;
        try {
            dbStation.dropDb();
            LogUtils.d("dbstation数据库被清空");
        } catch (DbException e) {
            e.printStackTrace();
        }

        unregisterReceiver(mReceiver);
    }
}
