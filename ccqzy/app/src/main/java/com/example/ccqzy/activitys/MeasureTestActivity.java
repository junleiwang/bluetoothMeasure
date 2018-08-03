package com.example.ccqzy.activitys;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import static com.example.ccqzy.QzyApplication.getRotAng;
import static com.example.ccqzy.QzyApplication.inputStream;
import static com.example.ccqzy.QzyApplication.isQuanZY;
import static com.example.ccqzy.QzyApplication.outputStream;
import static com.example.ccqzy.QzyApplication.rotMatrix;
import static com.example.ccqzy.QzyApplication.splitService;
import static java.lang.Math.PI;
import static java.lang.Math.abs;

public class MeasureTestActivity extends Activity {

    //定义各个控件
    private TextView tvPointX;
    private TextView tvPointY;
    private TextView tvPointH;//点坐标
    private TextView tvMeasure;//测量按钮
    private TextView tvUpdate;//翻面按钮

    //接收仪器返回的坐标值弧度值
    QzyApplication.CPDObvValue point1;
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
                    //测量返回的值 double类型,弧度坐标
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

                    //返回时判断模板点是否全部测量完毕
                    getCheckStates(2);
                    break;
            }
            return false;
        }
    });

    //数据集合
    List<MeasurePoint> listMeasure = new ArrayList<>();
    //接收从数据库里获取的点坐标
    List<CarbodyCalculatedDatas> allPoint = null;
    private ListView lvMeasure;
    MeasurAdapter adapter;

    //传递过来的uuid
    String itemUuid;

    //每个测点的id
    String itemId;

    //每个测点的order
    int itemOrder;

    AdapterView.OnItemClickListener mLeftListOnItemClick;

    //模板点的数据库
    DbManager dbManagerPoint;
    //全站仪原始坐标数据
    DbManager dbMeasureDatas;
    //.dat文件数据库
    DbManager dbFileDatas;

    //原点坐标(水平角,垂直角,斜距)
    QzyApplication.CPDObvValue point;

    FormulaTools formulaTools;

    //接收旋转角度坐标
    QzyApplication.CPDObvValue rotValue = new QzyApplication.CPDObvValue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_test);

        //标题栏透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        dbManagerPoint = x.getDb(((QzyApplication) getApplication()).getCarbodyCalculatedDatasConfig());
        dbMeasureDatas = x.getDb(((QzyApplication) getApplication()).getRadianConfig());
        dbFileDatas = x.getDb(((QzyApplication) getApplication()).getCarbodyMeasureConfig());
        itemUuid = getIntent().getStringExtra("itemId");

        initTitle();
        initView();

        formulaTools = new FormulaTools();
        point = new QzyApplication.CPDObvValue();

        //点击列表每个条目时的事件响应
        mLeftListOnItemClick = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                adapter.setSelectItem(arg2);
                adapter.notifyDataSetInvalidated();
                tvPointX.setText(listMeasure.get(arg2).getPointX());
                tvPointY.setText(listMeasure.get(arg2).getPointY());
                tvPointH.setText(listMeasure.get(arg2).getPointH());
                //根据每个条目的id 改变其数据
                for (int i = 0; i < allPoint.size(); i++) {
                    if (allPoint.get(i).getMeasurePointName().equals(listMeasure.get(arg2).getPointName())
                            && itemUuid.equals(allPoint.get(i).getMeasureInfoId())) {
                        //这里的问题
                        itemId = allPoint.get(i).getId();
                        itemOrder = allPoint.get(i).getSortOrder();
                    }
                }
                //currentRot();
            }

        };

        //读取数据库的测量点数据
        queryPoint();

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
            }
        });

        TextView title = findView(R.id.tv_title_name);
        title.setText("学习测量");
    }

    /*
    * 初始化view
    * */
    private void initView() {
        lvMeasure = findView(R.id.lv_dat_info);
        tvPointX = findView(R.id.tv_pointX);
        tvPointY = findView(R.id.tv_pointY);
        tvPointH = findView(R.id.tv_pointH);

        tvMeasure = findView(R.id.tv_measure_btn);
        tvMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击进行测量并返回点的坐标
                if (itemId == null) {
                    ChangChunActivity.instance.showTip("请先选择要测量的点");
                } else {
                    sendMessage(MEASURE);
                }
            }
        });
        //点击终止按钮,测量按钮是否可用?
        tvUpdate = findView(R.id.tv_update_btn);
        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击终止按钮,测量按钮是否可用?
                updateFlog(itemUuid);
                //测量按钮不可用
                tvMeasure.setOnClickListener(null);
            }
        });
        lvMeasure.setBackgroundColor(Color.parseColor("#abcfff"));

        lvMeasure.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tvPointX.setText(listMeasure.get(position).getPointX());
                tvPointY.setText(listMeasure.get(position).getPointY());
                tvPointH.setText(listMeasure.get(position).getPointH());
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
        adapter.notifyDataSetChanged();


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
        isQuanZY = false;
        splitService =null;
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
        }, 1000);// 这里百毫秒

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
                            Thread.sleep(300);
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
                    }else {
                        LogUtils.d("所有数据==" + split.length + "///" + split[0]);
                        isQuanZY = true;
                        splitService =null;
                        split = null;
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


    //从数据库中获取数据
    public void queryPoint() {
        try {
            Selector<CarbodyCalculatedDatas> selector = dbManagerPoint.selector(CarbodyCalculatedDatas.class);
            allPoint = selector.findAll();
            if (allPoint != null) {
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

    //当前点的旋转角度
    public void currentRot() {
        QzyApplication.CImsPointEx rotPoint = new QzyApplication.CImsPointEx();
        //从当前的集合进行判定
        for (int j = 0; j < listMeasure.size(); j++) {
            if (listMeasure.get(j).getSortOrder() == itemOrder) {
                rotPoint.X = Double.parseDouble(listMeasure.get(j).getPointX());
                rotPoint.Y = Double.parseDouble(listMeasure.get(j).getPointY());
                rotPoint.Z = Double.parseDouble(listMeasure.get(j).getPointH());
                itemId = listMeasure.get(j).getPointId();
                LogUtils.d("itemid=" + itemId);

                //设置下一点显示
                adapter.setSelectItem(j);
                adapter.notifyDataSetInvalidated();
                tvPointX.setText(listMeasure.get(j).getPointX());
                tvPointY.setText(listMeasure.get(j).getPointY());
                tvPointH.setText(listMeasure.get(j).getPointH());
            }
        }
        //得到全站仪的旋转角
        rotValue = formulaTools.calculate_ts_angle(rotPoint, rotValue);
        if (rotValue.m_dHzAngle != 0) {
            sendMessage(getRotAng(rotValue.m_dHzAngle, rotValue.m_dVAngle));
            LogUtils.d("旋转角度水平角=" + rotValue.m_dHzAngle + "垂直角=" + rotValue.m_dVAngle);
        } else {
            LogUtils.d("没有旋转角度");
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
            }
        }

        if (i == 1) {
            if (!isFinished) {
                showBackDialog();
            } else {
                updateFlog(itemUuid);
                MeasureTestActivity.this.finish();
            }
        } else if (i == 2) {
            if (isFinished) {
                updateFlog(itemUuid);
            }
        }
    }

    //弹出框,确认是否离开此界面
    public void showBackDialog() {
        final AlertDialogToast dialog = new AlertDialogToast(MeasureTestActivity.this);
        dialog.setTitle("还有未测量点,确认离开？");
        dialog.setPositiveButton("离开", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFlog(itemUuid);
                MeasureTestActivity.this.finish();
            }
        });


        dialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }
}
