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
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ccqzy.ChangChunActivity;
import com.example.ccqzy.QzyApplication;
import com.example.ccqzy.R;
import com.example.ccqzy.adapters.MeasurAdapter;
import com.example.ccqzy.androidutils.AlertDialogToast;
import com.example.ccqzy.androidutils.FormulaTools;
import com.example.ccqzy.beans.CarbodyCalculatedDatas;
import com.example.ccqzy.beans.CarbodyMeasureInfos;
import com.example.ccqzy.beans.CarbodyMeasuredDatas;
import com.example.ccqzy.beans.MeasurePoint;
import com.example.ccqzy.beans.ModleDatas;
import com.example.ccqzy.services.MeasureService;
import com.example.ccqzy.services.MessageService;
import com.lidroid.xutils.util.LogUtils;

import org.xutils.DbManager;
import org.xutils.db.Selector;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static com.example.ccqzy.QzyApplication.MEASURE;
import static com.example.ccqzy.QzyApplication.cimcs;
import static com.example.ccqzy.QzyApplication.className;
import static com.example.ccqzy.QzyApplication.fistLogin;
import static com.example.ccqzy.QzyApplication.getRotAng;
import static com.example.ccqzy.QzyApplication.inputStream;
import static com.example.ccqzy.QzyApplication.isConnect;
import static com.example.ccqzy.QzyApplication.isOnClick;
import static com.example.ccqzy.QzyApplication.isQuanZY;
import static com.example.ccqzy.QzyApplication.outputStream;
import static com.example.ccqzy.QzyApplication.qzyPoint;
import static com.example.ccqzy.QzyApplication.rotMatrix;
import static com.example.ccqzy.QzyApplication.splitService;
import static java.lang.Math.PI;
import static java.lang.Math.abs;

public class MeasureActivity extends Activity {

    //定义各个控件
    private TextView tvPointX;
    private TextView tvPointY;
    private TextView tvPointH;//点坐标
    private TextView tvMeasure;//测量按钮
    private TextView tvUpdate;//翻面按钮

    private TextView content;

    //接收仪器返回的坐标值弧度值
    QzyApplication.CPDObvValue point1;

    //全站仪红色按钮
    QzyApplication.CImsPointEx pointQuan;
    //全站仪上的坐标值
    QzyApplication.CImsPointEx pointx;

    String averageX;
    String averageY;
    String averageH;

    //全站仪返回的数据处理方法
    public Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //全站仪红色按钮点击自动接收数据并显示在列表上
                    if (splitService != null && isQuanZY && (splitService.length == 6 || splitService.length == 5)) {
                        // split = null;//将split值为null
                        pointQuan = new QzyApplication.CImsPointEx();
                        pointQuan.X = getFourDouble(splitService[2]) * 1000;
                        pointQuan.Y = -(getFourDouble(splitService[1]) * 1000);
                        pointQuan.Z = getFourDouble(splitService[3]) * 1000;

                        if (abs(cimcs.X) > 0 || abs(cimcs.Y) > 0 || abs(cimcs.Z) > 0) {
                            rotMatrix.A1 = 0;
                            rotMatrix.A2 = 0;
                            rotMatrix.A3 = 0;
                            rotMatrix.B1 = 0;
                            rotMatrix.B2 = 0;
                            rotMatrix.B3 = 0;
                            rotMatrix.C1 = 0;
                            rotMatrix.C2 = 0;
                            rotMatrix.C3 = 0;
                            LogUtils.d("POINTQuan的坐标是 x==" + pointQuan.X + "y==" + pointQuan.Y + "z==" + pointQuan.Z);
                            pointQuan = formulaTools.TranslateMeaPoint(cimcs, rotMatrix, pointQuan);
                        } else {
                            ChangChunActivity.instance.showTip("cims为空,旋转矩阵为空");
                        }

                        averageX = getFourPoint(String.valueOf(pointQuan.X));
                        averageY = getFourPoint(String.valueOf(pointQuan.Y));
                        averageH = getFourPoint(String.valueOf(pointQuan.Z));

                        update(itemId, averageX, averageY, averageH);

                        //得到全站仪的旋转角
                        rotValueQuan = formulaTools.calculate_ts_angle(pointQuan, rotValueQuan);
                        //得到id是itemId的模板点的信息,添加到全站仪原始数据库中
                        for (int i = 0; i < allPoint.size(); i++) {
                            if (allPoint.get(i).getId().equals(itemId)) {
                                try {
                                    insertRadianPoint(allPoint.get(i), String.valueOf(0.0),
                                            String.valueOf(0.0), String.valueOf(0.0));
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        getCheckStates(2);

                        //自动旋转到下一点
                        automaticRot();
                    } else {
                        splitService = null;
                        LogUtils.d("红色按钮没有数据");
                    }
                    break;
                case 1:
                    //全站仪上的按钮
                    if (split != null && split.length == 7) {
                        //测量返回的值 double类型,弧度坐标,手动测量返回数据读取数据
                        point1 = new QzyApplication.CPDObvValue();//第一面
                        point1.m_dHzAngle = getFourDouble(split[3]);//水平角
                        point1.m_dVAngle = getFourDouble(split[4]);//垂直角
                        point1.m_dSlopeDist = getFourDouble(split[5]) * 1000;//斜距(米)

                        //定义全站仪上的坐标点(x,y,z)
                        pointx = new QzyApplication.CImsPointEx();
                        pointx = getPointCoordinate(point1, pointx);
                        LogUtils.d("全站仪坐标pointx=x" + pointx.X + "///y=" + pointx.Y + "///z=" + pointx.Z);

                        if (abs(cimcs.X) > 0 || abs(cimcs.Y) > 0 || abs(cimcs.Z) > 0) {
                            rotMatrix.A1 = 0;
                            rotMatrix.A2 = 0;
                            rotMatrix.A3 = 0;
                            rotMatrix.B1 = 0;
                            rotMatrix.B2 = 0;
                            rotMatrix.B3 = 0;
                            rotMatrix.C1 = 0;
                            rotMatrix.C2 = 0;
                            rotMatrix.C3 = 0;

                            pointx = formulaTools.TranslateMeaPoint(cimcs, rotMatrix, pointx);
                        } else {
                            ChangChunActivity.instance.showTip("cims为空,旋转矩阵为空");
                        }
                        // 正反两面测量的均值
                        averageX = getFourPoint(String.valueOf(pointx.Y));
                        averageY = getFourPoint(String.valueOf(pointx.X));
                        averageH = getFourPoint(String.valueOf(pointx.Z));

                        update(itemId, averageY, averageX, averageH);

                        //得到id是itemId的模板点的信息,添加到全站仪原始数据库中
                        for (int i = 0; i < allPoint.size(); i++) {
                            if (allPoint.get(i).getId().equals(itemId)) {
                                try {
                                    insertRadianPoint(allPoint.get(i), String.valueOf(point1.m_dHzAngle),
                                            String.valueOf(point1.m_dVAngle), String.valueOf(point1.m_dSlopeDist));
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        getCheckStates(2);

                        //自动旋转到下一点
                        automaticRot();
                        isQuanZY = true;
                        splitService = null;
                        split = null;
                    } else {
                        LogUtils.d("split为空");
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    //数据集合
    List<MeasurePoint> listMeasure = new ArrayList<>();
    //模板点集合
    List<MeasurePoint> modleList = new ArrayList<>();
    //接收从数据库里获取的点坐标
    List<CarbodyCalculatedDatas> allPoint = null;
    //接收模板数据库里获取的点坐标
    List<ModleDatas> modleDatasList = null;

    private ListView lvMeasure;
    MeasurAdapter adapter;

    //传递过来的uuid和标示
    String itemUuid;
    //是否是未测量完的数据
    String isTrue;

    //每个测点的id
    String itemId;

    //每个测点的下标
    int positionItem;

    //每个测点的order
    int itemOrder;

    AdapterView.OnItemClickListener mLeftListOnItemClick;

    //模板点的数据库
    DbManager dbModle;
    //模板点测量的数据库
    DbManager dbManagerPoint;
    //全站仪原始坐标数据
    DbManager dbMeasureDatas;
    //.dat文件数据库
    DbManager dbFileDatas;

    //平移量旋转角数据库
    DbManager dbCimcs;

    //原点坐标(水平角,垂直角,斜距)
    QzyApplication.CPDObvValue point;

    FormulaTools formulaTools;

    //接收旋转角度坐标
    QzyApplication.CPDObvValue rotValue = new QzyApplication.CPDObvValue();
    QzyApplication.CPDObvValue rotValueQuan = new QzyApplication.CPDObvValue();

    public static MeasureActivity instance;

    public MeasureActivity() {
        instance = MeasureActivity.this;
    }

    MediaPlayer mp = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);
        //标题栏透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        dbModle = x.getDb(((QzyApplication) getApplication()).getModleConfig());
        dbManagerPoint = x.getDb(((QzyApplication) getApplication()).getCarbodyCalculatedDatasConfig());
        dbMeasureDatas = x.getDb(((QzyApplication) getApplication()).getRadianConfig());
        dbFileDatas = x.getDb(((QzyApplication) getApplication()).getCarbodyMeasureConfig());
        dbCimcs = x.getDb(((QzyApplication) getApplication()).getCimcsConfig());
        itemUuid = getIntent().getStringExtra("itemId");
        isTrue = getIntent().getStringExtra("isTrue");

        className = "MeasureActivity";

        mp = MediaPlayer.create(this, R.raw.beep1);
        initTitle();
        initView();

        ///////////////////////////
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(mReceiver, filter);
        ///////////////////////////

        formulaTools = new FormulaTools();
        point = new QzyApplication.CPDObvValue();

        //点击列表每个条目时的事件响应
        mLeftListOnItemClick = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                adapter.setSelectItem(arg2);
                adapter.notifyDataSetInvalidated();
                tvPointX.setText(modleList.get(arg2).getPointX());
                tvPointY.setText(modleList.get(arg2).getPointY());
                tvPointH.setText(modleList.get(arg2).getPointH());
                LogUtils.d("模版12--------数据单条目视" + modleList.get(arg2).getPointX());
                LogUtils.d("测量12--------数据单条目视" + listMeasure.get(arg2).getPointX());
                //根据每个条目的id 改变其数据
                for (int i = 0; i < allPoint.size(); i++) {
                    if (allPoint.get(i).getMeasurePointName().equals(listMeasure.get(arg2).getPointName())
                            && itemUuid.equals(allPoint.get(i).getMeasureInfoId())) {
                        //这里的问题
                        positionItem = arg2;
                        itemId = allPoint.get(i).getId();
                        itemOrder = allPoint.get(i).getSortOrder();

                    }
                }

                //指定到当前点
                currentRot();
            }

        };

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
        //全站仪上的原点坐标
        qzyPoint.X = 0;
        qzyPoint.Y = 0;
        qzyPoint.Z = 0;
        qzyPoint = formulaTools.TranslateMeaPoint(cimcs, rotMatrix, qzyPoint);

        //读取数据库的测量点数据
        queryPoint();
        queryModlePoint();

        //点击添加模块进来后自动旋转到orient2点
        automaticRot();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.d("onResume运行了");
        Intent intent1 = new Intent(MeasureActivity.this, MeasureService.class);
        isBound = bindService(intent1, connection1, Context.BIND_AUTO_CREATE);

        if (isConnect) {
            content.setText("蓝牙已连接");
            content.setTextColor(Color.parseColor("#FF08F710"));
        } else {
            content.setText("蓝牙未连接");
            content.setTextColor(Color.parseColor("#f70808"));
        }
    }

    /*
       * 初始化标题头
       * */
    private void initTitle() {
        TextView back = findView(R.id.tv_title_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCheckStates(1);
                isQuanZY = false;
            }
        });

        TextView title = findView(R.id.tv_title_name);
        title.setText("测量");

        content = findView(R.id.tv_bluetooth_iscontent);
        content.setVisibility(View.VISIBLE);
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MeasureActivity.this, BluetoothActivity.class);
                startActivity(intent);
            }
        });
    }

    /*
    * 初始化view
    * */

    private long firstTime = 0;
    private void initView() {
        lvMeasure = findView(R.id.lv_dat_info);
        tvPointX = findView(R.id.tv_pointX);
        tvPointY = findView(R.id.tv_pointY);
        tvPointH = findView(R.id.tv_pointH);

        tvMeasure = findView(R.id.tv_measure_btn);
        tvMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //判断测量键是否可用
                if (isOnClick) {
                    LogUtils.d("点击后的状态旋转完成后测量键的状态"+isOnClick);
                    isOnClick = false;
                    //点击进行测量并返回点的坐标
                    if (itemId == null) {
                        ChangChunActivity.instance.showTip("请先选择要测量的点");
                        isQuanZY = true;
                    } else {
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - firstTime > 500) {
                            sendMessage(MEASURE);
                            firstTime = currentTime;

                        } else {
                            ChangChunActivity.instance.showTip("1111111111111111正在测量操作,请勿连续点击");
                        }
                    }
                }else {
                    ChangChunActivity.instance.showTip("正在测量操作,请勿连续点击");
                }
            }
        });
        //终止键
        tvUpdate = findView(R.id.tv_update_btn);
        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击终止按钮,测量按钮是否可用?
                tvMeasure.setOnClickListener(null);
                updateFlog(itemUuid);
                if (isBound) {
                    unbindService(connection1);
                    isBound = false;
                    fistLogin = false;
                }
                try {
                    dbCimcs.dropDb();
                    LogUtils.d("dbCimcs数据库清除成功!!!");
                } catch (DbException e) {
                    e.printStackTrace();
                }

            }
        });
        lvMeasure.setBackgroundColor(Color.parseColor("#abcfff"));

        lvMeasure.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tvPointX.setText(modleList.get(position).getPointX());
                tvPointY.setText(modleList.get(position).getPointY());
                tvPointH.setText(modleList.get(position).getPointH());
                LogUtils.d("模板数据单条目视" + modleList.get(position).getPointX());
                LogUtils.d("测量数据单条目视" + listMeasure.get(position).getPointX());

                //根据每个条目的id 改变其数据
                for (int i = 0; i < allPoint.size(); i++) {
                    if (allPoint.get(i).getMeasurePointName().equals(listMeasure.get(position).getPointName())
                            && itemUuid.equals(allPoint.get(i).getMeasureInfoId())) {
                        //这里的问题
                        itemId = allPoint.get(i).getId();
                        itemOrder = allPoint.get(i).getSortOrder();
                        positionItem = position;

                    }
                }
            }
        });
    }

    /*
 * 将添加的模板文件的内容展示出来的列表
 * */
    private void getDates(List<MeasurePoint> list) {
        adapter = new MeasurAdapter(list, this);
        lvMeasure.setDividerHeight(0);
        lvMeasure.setAdapter(adapter);
        lvMeasure.setOnItemClickListener(mLeftListOnItemClick);
        //数据更新后跳转到第positionItem处
        LogUtils.d("positionitem的值为" + positionItem);
        lvMeasure.post(new Runnable() {
            @Override
            public void run() {
                lvMeasure.smoothScrollToPosition(positionItem);
                LogUtils.d("测试跳转执行了");
            }
        });

        // adapter.notifyDataSetChanged();

    }

    /*
    * 全站仪返回的水平角,垂直角和斜距算出点的坐标值
    * */
    private QzyApplication.CImsPointEx getPointCoordinate(QzyApplication.CPDObvValue testPoint, QzyApplication.CImsPointEx outPoint) {
        //  return formulaTools.coordinateFromHzVD(1, testPoint, outPoint);
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

    /*
* 发送信息
* */
    Timer timer = new Timer();
    int num = 0;

    private void sendMessage(final String mesg) {

        //修改部分
        isQuanZY = false;
        splitService = null;
        split = null;

        write(mesg);
        LogUtils.d("messages==" + mesg);

        num = 0;
        // 实例化Timer类
        timer.schedule(new TimerTask() {
            public void run() {
                LogUtils.d("方法执行了");
                readDates();
            }
        }, 3000);// 这里百毫秒

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
    private StringBuilder jsonBuilder = new StringBuilder();
    private BlueWaitingTimer timer1 = new BlueWaitingTimer(500, 500);

    private class BlueWaitingTimer extends CountDownTimer {


        public BlueWaitingTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            //一段数据接收完毕，开始使用数据，使用后清空StringBuilder
            jsonBuilder.delete(0, jsonBuilder.length());
        }
    }


    String readDate;
    String[] split;

    public void readDates() {
        try {
            if (inputStream != null) {
                byte[] buffer = new byte[1024];
                int count;
                if (inputStream.available() >10) {
                    count = inputStream.read(buffer);
                    timer1.cancel();
                    jsonBuilder.append(new String(buffer, 0, count, "UTF-8"));
                    timer1.start();

                    LogUtils.d("jsonbuilder的数值为" + jsonBuilder.toString());
                    split = jsonBuilder.toString().split("[,]");
                    if (split.length == 7) {
                        LogUtils.d("所有数据==" + split.length + "///" + split[0] + split[1] + split[2] + "//" + getFourPoint(split[3]) + "//"
                                + getFourPoint(split[4]) + "//" + getFourPoint(split[5]));
                        handler.sendEmptyMessage(1);
                    } else if (split.length == 3) {
                        LogUtils.d("获取的数据长度为==" + split.length + "//" + split[0]);
                        isOnClick = true;
                        LogUtils.d("旋转完成后测量键的状态"+isOnClick);
                        split = null;
                        isQuanZY = true;
                        splitService = null;
                        LogUtils.d("split已至空");
                    } else if(split.length>0&&split.length<3) {
                        LogUtils.d("获取的数据长度为==" + split.length + "//" + split[0]);
                        split = null;
                        splitService = null;
                        isQuanZY = true;//是否使用
                        LogUtils.d("split已至空");
                        //信息致空后,直接再次测量
                        sendMessage(MEASURE);
                       // ChangChunActivity.instance.showTip("测量数据异常,请再次测量");
                    }else {
                        split = null;
                        splitService = null;
                        LogUtils.d("split已至空");
                        //信息致空后,直接再次测量
                        sendMessage(MEASURE);
                    }
                }
                //这个地方该不该标示
                //isQuanZY = true;

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

    //从模板数据库中获取数据
    public void queryModlePoint() {
        try {
            Selector<ModleDatas> selector = dbModle.selector(ModleDatas.class);
            modleDatasList = selector.findAll();
            if (modleDatasList == null) {
                LogUtils.d("数据库为空!!!!!");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        if (modleDatasList != null) {
            modleList.clear();
            for (ModleDatas user : modleDatasList) {
                //添加到列表中
                if (user.getMeasureInfoId().equals(itemUuid)) {
                    MeasurePoint measurePoint = new MeasurePoint();
                    measurePoint.setPointName(user.getMeasurePointName());
                    measurePoint.setPointX(user.getX());
                    measurePoint.setPointY(user.getY());
                    measurePoint.setPointH(user.getH());
                    measurePoint.setIsOver(String.valueOf(user.isIsOver()));
                    measurePoint.setSortOrder(user.getSortOrder());
                    measurePoint.setPointId(user.getId());
                    modleList.add(measurePoint);
                } else {
                    LogUtils.d("没有数据");
                }
            }
            LogUtils.d("modlelist模板数据库=" + modleList.toString());

        } else {
            LogUtils.d("没有数据");
        }
    }

    //从数据库中获取数据
    public void queryPoint() {
        try {
            Selector<CarbodyCalculatedDatas> selector = dbManagerPoint.selector(CarbodyCalculatedDatas.class);
            allPoint = selector.findAll();
            if (allPoint == null) {
                LogUtils.d("数据库为空!!!!!");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        if (allPoint != null) {
            listMeasure.clear();
            for (CarbodyCalculatedDatas user : allPoint) {
                //添加到列表中
                if (user.getMeasureInfoId().equals(itemUuid)) {
                    MeasurePoint measurePoint = new MeasurePoint();
                    measurePoint.setPointName(user.getMeasurePointName());
                    measurePoint.setPointX(user.getX());
                    measurePoint.setPointY(user.getY());
                    measurePoint.setPointH(user.getH());
                    measurePoint.setIsOver(String.valueOf(user.isIsOver()));
                    measurePoint.setSortOrder(user.getSortOrder());
                    measurePoint.setPointId(user.getId());
                    listMeasure.add(measurePoint);
                } else {
                    LogUtils.d("没有数据");
                }
                //getDates(listMeasure);
            }
            LogUtils.d("listMeasure=" + listMeasure.toString());
            getDates(listMeasure);
            LogUtils.d("判断所有数据是否测量完成");
            getCheckStates(3);

        } else {
            LogUtils.d("没有数据");
        }
    }

    //更改某个数据,当点击正常或异常按钮时将isCheck 的状态改为yes
    public void update(String id, String x, String y, String h) {
        try {
            CarbodyCalculatedDatas user = new CarbodyCalculatedDatas();
            user.setId(id);
            user.setX(x);
            user.setY(y);
            user.setH(h);
            user.setCreationTime(getTime(System.currentTimeMillis()));
            user.setIsOver(true);
            dbManagerPoint.update(user, "X", "Y", "H", "CreationTime", "isOver");
            LogUtils.d("模板数据更新成功");

            //更新列表//应该更新单条目
            queryPoint();
            //蜂鸣
            getVoice();
            //震动
            //getShock();

        } catch (DbException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //更新.dat文件是否结束的数据标示
    public void updateFlog(String id) {
        try {
            CarbodyMeasureInfos user = new CarbodyMeasureInfos();
            user.setId(id);
            user.setFinished(true);
            dbFileDatas.update(user, "finished");
            LogUtils.d("模板数据更新成功finished=" + user.isFinished() + "//id==" + user.getId());

            //更新列表//应该更新单条目
            queryPoint();

        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    //自动计算下一点的旋转角度
    public void automaticRot() {
        int posion = 0;
        QzyApplication.CImsPointEx rotPoint = new QzyApplication.CImsPointEx();
        //从当前的集合进行判定
        if (isTrue.equals("false")) {
            for (int j = 0; j < listMeasure.size(); j++) {
                if (listMeasure.get(j).getSortOrder() == itemOrder + 1) {
                    rotPoint.X = Double.parseDouble(listMeasure.get(j).getPointX());
                    rotPoint.Y = Double.parseDouble(listMeasure.get(j).getPointY());
                    rotPoint.Z = Double.parseDouble(listMeasure.get(j).getPointH());
                    itemId = listMeasure.get(j).getPointId();
                    positionItem = j;
                    LogUtils.d("itemid=" + itemId);

                    //设置下一点显示
                    adapter.setSelectItem(j);
                    adapter.notifyDataSetInvalidated();
                    tvPointX.setText(modleList.get(j).getPointX());
                    tvPointY.setText(modleList.get(j).getPointY());
                    tvPointH.setText(modleList.get(j).getPointH());
                }
            }
        } else {
            for (int i = 0; i < listMeasure.size(); i++) {
                if (listMeasure.get(i).getIsOver().equals("true")) {
                    posion = i + 1;
                }
            }
            if (posion == listMeasure.size()) {
                posion = posion - 1;
            }
            LogUtils.d("测试里面的POSINO===" + posion);
            rotPoint.X = Double.parseDouble(listMeasure.get(posion).getPointX());
            rotPoint.Y = Double.parseDouble(listMeasure.get(posion).getPointY());
            rotPoint.Z = Double.parseDouble(listMeasure.get(posion).getPointH());
            itemId = listMeasure.get(posion).getPointId();
            positionItem = posion;
            LogUtils.d("测试里面的itemid=" + itemId);

            //设置下一点显示
            adapter.setSelectItem(posion);
            adapter.notifyDataSetInvalidated();
            tvPointX.setText(modleList.get(posion).getPointX());
            tvPointY.setText(modleList.get(posion).getPointY());
            tvPointH.setText(modleList.get(posion).getPointH());

            isTrue = "false";
            itemOrder = listMeasure.get(posion).getSortOrder() - 1;

        }


        itemOrder = itemOrder + 1;
        LogUtils.d("///+itemOrder=" + itemOrder);
        //得到全站仪的旋转角
        rotValue = formulaTools.calculate_ts_angle(rotPoint, rotValue);
        if (rotValue.m_dHzAngle != 0) {
            sendMessage(getRotAng(rotValue.m_dHzAngle, rotValue.m_dVAngle));
            LogUtils.d("旋转角度水平角=" + rotValue.m_dHzAngle + "垂直角=" + rotValue.m_dVAngle);
        } else {
            sendMessage(getRotAng(rotValue.m_dHzAngle, rotValue.m_dVAngle));
            LogUtils.d("旋转角度水平角=" + rotValue.m_dHzAngle + "垂直角=" + rotValue.m_dVAngle);
            LogUtils.d("没有旋转角度");
        }
    }

    //当前点的旋转角度
    public void currentRot() {
        QzyApplication.CImsPointEx rotPoint = new QzyApplication.CImsPointEx();
        //从当前的集合进行判定
        for (int j = 0; j < listMeasure.size(); j++) {
            if (listMeasure.get(j).getSortOrder() == itemOrder) {//默认指向listMeasure列表的第一个点
                rotPoint.X = Double.parseDouble(listMeasure.get(j).getPointX());
                rotPoint.Y = Double.parseDouble(listMeasure.get(j).getPointY());
                rotPoint.Z = Double.parseDouble(listMeasure.get(j).getPointH());
                itemId = listMeasure.get(j).getPointId();
                positionItem = j;
                LogUtils.d("itemid=" + itemId + "rotpointx" + rotPoint.X + rotPoint.Y + rotPoint.Z);

                //设置下一点显示
                adapter.setSelectItem(j);
                adapter.notifyDataSetInvalidated();
                tvPointX.setText(modleList.get(j).getPointX());
                tvPointY.setText(modleList.get(j).getPointY());
                tvPointH.setText(modleList.get(j).getPointH());
            }
        }
        //得到全站仪的旋转角
        rotValue = formulaTools.calculate_ts_angle(rotPoint, rotValue);
        if (rotValue.m_dHzAngle != 0 || rotValue.m_dVAngle != 0) {
            sendMessage(getRotAng(rotValue.m_dHzAngle, rotValue.m_dVAngle));
            LogUtils.d("旋转角度水平角=" + rotValue.m_dHzAngle + "垂直角=" + rotValue.m_dVAngle);
        } else {
            LogUtils.d("没有旋转角度");
        }
    }

    //实现手机震动的方法
    private Vibrator vibrator;

    private void getShock() {
         /*
     * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
     * */
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {500, 500, 1000, 1000}; // 停止 开启 停止 开启
        vibrator.vibrate(pattern, -1); //重复两次上面的pattern 如果只想震动一次，index设为-1
    }

    //实现手机蜂鸣的方法
    private void getVoice() {
        mp.start();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //mp.stop();
                mp.pause();
            }
        }, 1000);
    }

    //全站仪原始数据添加
    private void insertRadianPoint(CarbodyCalculatedDatas ccd, String x, String y, String z) throws DbException {

        String id = UUID.randomUUID().toString();
        CarbodyMeasuredDatas user = null;
        user = new CarbodyMeasuredDatas(id, ccd.getMeasureInfoId(), ccd.getMeasurePointId(), ccd.getMeasurePointName(),
                ccd.getSortOrder(), x, y, z, ccd.getCreationTime());
        LogUtils.d("保存全站仪原始数据的时间;" + id);


        try {
            LogUtils.d("这里出现的问题" + user.toString());
            dbMeasureDatas.save(user);
            LogUtils.d("添加数据到dbMeasureDatas成功,保存全站仪原始数据的值" + dbMeasureDatas.toString());
        } catch (DbException e) {

        }
    }

    /*
*  处理返回的坐标数据保留小数点后四位,四舍五入
* */
    private double getFourDouble(String point) {
        double value = Double.parseDouble(point);
        return value;
    }

    //获取时间的转换格式
    public String getTime(long time) throws ParseException {
        Date d = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        String time1 = sdf.format(d);
        return time1;
    }

    //点击返回时调用的方法
    private void getCheckStates(int i) {
        boolean isFinished = true;
        for (MeasurePoint measurePoint : listMeasure) {
            if (measurePoint.getIsOver().equals("false")) {
                isFinished = false;
            } else {
                isFinished = true;
            }
        }

        if (i == 1) {
            if (!isFinished) {
                showBackDialog();
            } else {
                updateFlog(itemUuid);
                try {
                    dbCimcs.dropDb();
                    LogUtils.d("dbcimcs数据库删除成功!!!");
                } catch (DbException e) {
                    e.printStackTrace();
                }
                MeasureActivity.this.finish();
            }
        } else if (i == 2) {
            if (isFinished) {
                updateFlog(itemUuid);
            }
        } else if (i == 3) {
            if (isFinished) {
                ChangChunActivity.instance.showTip("所有测量点都已测完!!!");
            } else {
                LogUtils.d("还有未测量点");
            }
        }
    }

    /**
     * 第三种方法 调用一次getView()方法；Google推荐的做法
     *
     * @param position 要更新的位置
     */
    private void updateItem(int position) {
        /**第一个可见的位置**/
        int firstVisiblePosition = lvMeasure.getFirstVisiblePosition();
        /**最后一个可见的位置**/
        int lastVisiblePosition = lvMeasure.getLastVisiblePosition();

        /**在看见范围内才更新，不可见的滑动后自动会调用getView方法更新**/
        if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
            /**获取指定位置view对象**/
            View view = lvMeasure.getChildAt(position - firstVisiblePosition);
            adapter.getView(position, view, lvMeasure);
            LogUtils.d("单条目数据更新成功");
        }

    }

    //弹出框,确认是否离开此界面
    public void showBackDialog() {
        final AlertDialogToast dialog = new AlertDialogToast(MeasureActivity.this);
        dialog.setTitle("还有未测量点,确认离开？");
        dialog.setPositiveButton("离开", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFlog(itemUuid);

                try {
                    dbCimcs.dropDb();
                    LogUtils.d("dbcimcs数据库被删除成功!!!!");
                } catch (DbException e) {
                    e.printStackTrace();
                }
                if (isBound) {
                    unbindService(connection1);
                    isBound = false;
                }

                MeasureActivity.this.finish();
            }
        });


        dialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    /**
     * back键的控制
     */
    @Override
    public void onBackPressed() {
        LogUtils.d("back键被点击了");
        getCheckStates(1);
        isQuanZY = false;
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
            MessageService.MyBinder mybinder = (MessageService.MyBinder) binder;
            MessageService myservice = mybinder.getService();  //获得该服务
            //在这里获取有关服务的各种信息包括状态等等
        }
    };

    final ServiceConnection connection1 = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // TODO Auto-generated method stub
            //onServiceDisconnected()方法在正常情况下是不被调用的，它的调用时机是当Service服务被异外销毁时     ，例如内存的资源不足时
        }

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder binder) {
            // TODO Auto-generated method stub
            MeasureService.MyBindService myBindService = (MeasureService.MyBindService) binder;
            MeasureService mybs = myBindService.getMeasuerS();//获得该服务
            //在这里获取有关服务的各种信息包括状态等等
            mybs.readDates();
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
                            isConnect = false;
                            break;
                        case BluetoothAdapter.STATE_CONNECTED:
                            LogUtils.d("蓝牙已经连接上了=============================");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            LogUtils.d("TAG" + "STATE_ON");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            content.setText("蓝牙未连接");
                            content.setTextColor(Color.parseColor("#f70808"));
                            isConnect = false;
                            LogUtils.d("TAG" + "STATE_OFF");
                            break;
                    }
                    break;
            }
        }
    };

    ////////////////////////////////////

    private boolean isBound = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(connection1);
            isBound = false;
        }
        unregisterReceiver(mReceiver);
        LogUtils.d("测量界面的服务接触绑定了===============================");
    }
}
