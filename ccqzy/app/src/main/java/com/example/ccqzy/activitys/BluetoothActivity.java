package com.example.ccqzy.activitys;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ccqzy.ChangChunActivity;
import com.example.ccqzy.R;
import com.example.ccqzy.adapters.BAdapter;
import com.example.ccqzy.androidutils.BlueToothUtils;
import com.lidroid.xutils.util.LogUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.ccqzy.QzyApplication.MEASURE;
import static com.example.ccqzy.QzyApplication.TURNOVER;
import static com.example.ccqzy.QzyApplication.inputStream;
import static com.example.ccqzy.QzyApplication.isConnect;
import static com.example.ccqzy.QzyApplication.outputStream;

public class BluetoothActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "BlueToothActivity";
    private TextView btnOpen; //打开蓝牙
    private TextView btnClose; //关闭蓝牙
    private TextView btnSearch;//搜索蓝牙
    private TextView btnSend; //测角测面命令
    private TextView btnTurnOver;//翻面命令
    private TextView btnMeasureAngle;//旋转命令
    private TextView tvGetDate;
    private TextView tvIsconnected;//蓝牙连接状态
    private ListView lvSearch;//搜索到的蓝牙列表
    private ListView lvPair;//已配对列表

    BlueToothUtils blueUtil;//蓝牙工具类
    BAdapter adapterPair;
    BAdapter adapterSearch;

    List<BluetoothDevice> listSearch = new ArrayList<>();
    Set<BluetoothDevice> setSearch = new HashSet<>();

    String order4 = "%R1Q,9027:20,20,0,0,0\r";//旋转角度

    String readDate;//从全站仪返回的数据



    //BluetoothServiceConnect bluetoothServiceConnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        //设置导航栏透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        blueUtil = new BlueToothUtils();

        initTitle();

        initView();

        //通过广播的方式进行蓝牙搜索
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter2);

        //已配对设备列表
        getPairDate(blueUtil.getBondedDevices());

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isConnect){
            tvIsconnected.setText("蓝牙已连接......");
            tvIsconnected.setTextColor(Color.parseColor("#FF08F710"));
        }else {
            tvIsconnected.setText("蓝牙未连接......");
            tvIsconnected.setTextColor(Color.parseColor("#FFF70808"));
        }
    }

    //初始化标题头
    private void initTitle() {

        TextView back = (TextView) findViewById(R.id.tv_title_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothActivity.this.finish();
            }
        });

        TextView title = (TextView) findViewById(R.id.tv_title_name);
        title.setText("蓝牙连接");
    }

    /*
   * 初始化view控件
   * */
    private void initView() {
        btnOpen = (TextView) findViewById(R.id.btn_open);
        btnClose = (TextView) findViewById(R.id.btn_close);
        btnSearch = (TextView) findViewById(R.id.btn_search);
        btnSend = (TextView) findViewById(R.id.btn_send);
        btnMeasureAngle = (TextView) findViewById(R.id.btn_measure_angle);
        btnTurnOver = (TextView) findViewById(R.id.btn_turn_over);
        tvGetDate = (TextView) findViewById(R.id.tv_back_date);

        tvIsconnected = (TextView) findViewById(R.id.tv_isconnected);

        btnOpen.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btnMeasureAngle.setOnClickListener(this);
        btnTurnOver.setOnClickListener(this);
        tvGetDate.setOnClickListener(this);

        lvSearch = (ListView) findViewById(R.id.lv_search);
        // 点击单条目进行配对
        lvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 点击单条目进行配对
                blueUtil.stopSearchDevices();
                try {
                    blueUtil.createBond(listSearch.get(position).getClass(), listSearch.get(position));
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    getSearchDate(listSearch);
                    getPairDate(blueUtil.getBondedDevices());
                }

            }
        });

        lvPair = (ListView) findViewById(R.id.lv_pair);
        if (blueUtil.getBondedDevices() == null) {
            Toast.makeText(this, "没有已配对的蓝牙设备", Toast.LENGTH_SHORT).show();
        } else {
            getPairDate(blueUtil.getBondedDevices());
        }

        //点击单条目进行蓝牙连接
        lvPair.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                blueUtil.stopSearchDevices();
                if (blueUtil.isEnabled()) {
                    try {
                        blueUtil.connect(blueUtil.getBondedDevices().get(position));
                        LogUtils.d("蓝牙连接成功");

                        if (isConnect) {
                            tvIsconnected.setText("蓝牙已连接......");
                            tvIsconnected.setTextColor(Color.parseColor("#FF08F710"));
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    ChangChunActivity.instance.showTip("请先打开蓝牙,在进行连接");
                    //Toast.makeText(BluetoothActivity.this, "请先打开蓝牙,在进行连接", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /*
    * 更新搜索到的蓝牙设备列表
    * */
    public void getSearchDate(List<BluetoothDevice> list) {
        adapterSearch = new BAdapter(list, BluetoothActivity.this);
        lvSearch.setAdapter(adapterSearch);
        adapterSearch.notifyDataSetChanged();
    }

    /*
    * 更新已配对的蓝牙设备列表
    * */
    public void getPairDate(List<BluetoothDevice> list) {
        adapterPair = new BAdapter(list, BluetoothActivity.this);
        lvPair.setAdapter(adapterPair);
        lvPair.refreshDrawableState();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open:
                blueUtil.openBlueTooth();//打开蓝牙
                break;
            case R.id.btn_close:
                blueUtil.closeBlueTooth();//关闭蓝牙
                isConnect = false;
                tvIsconnected.setText("蓝牙未连接......");
                tvIsconnected.setTextColor(Color.parseColor("#FFF70808"));
                break;
            case R.id.btn_search:
                searchBluetooth();
                getPairDate(blueUtil.getBondedDevices());
                break;
            case R.id.btn_send:
                //readDates();
                sendMessage(MEASURE);
                break;
            case R.id.btn_measure_angle:
               // readDates();
                sendMessage(order4);
                break;
            case R.id.btn_turn_over:
                sendMessage(TURNOVER);
                break;
            case R.id.tv_back_date:
                readDates();
                break;
            default:
                break;
        }

    }


    /*
    * 搜索蓝牙设备
    * */
    private void searchBluetooth() {

        if (blueUtil.isEnabled()) {
            Toast.makeText(BluetoothActivity.this, "正在搜索中。。。", Toast.LENGTH_SHORT).show();
            blueUtil.searchDevices();
        } else {
            ChangChunActivity.instance.showTip("请打开蓝牙,再进行所搜");
            //Toast.makeText(this, "请打开蓝牙，再进行搜索", Toast.LENGTH_SHORT).show();
        }

    }

    //定义广播接收
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            Log.d(TAG, action);
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {    //显示已配对设备

                } else if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    setSearch.add(device);

                }

            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {

                listSearch.clear();
                if (setSearch.size() > 0) {
                    for (BluetoothDevice deviceSearch : setSearch) {
                        listSearch.add(deviceSearch);
                    }
                }
                getSearchDate(listSearch);

                //Toast.makeText(context, "搜索完成...", Toast.LENGTH_SHORT).show();
            }

        }


    };
    /*
    * 发送信息
    * */
    int num = 0;
    Timer timer = new Timer();
    private void sendMessage(final String mesg) {
        write(mesg);
        LogUtils.d("messages==" + mesg);
        // 实例化Timer类

        num = 0;
        timer.schedule(new TimerTask() {
            public void run() {
                LogUtils.d("方法执行了");
                readDates();
            }
        }, 1000);// 这里百毫秒

    }

    ;

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
                Log.d(TAG, "os为空");
            }
            Log.e(TAG, "write:" + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    * 读取全站仪返回的信息
    * */
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
               // byte[] byteRead = new byte[inputStream.available()];
                if (byteRead.length > 0) {
                    inputStream.read(byteRead, 0, byteRead.length);
                    readDate = new String(byteRead, "UTF-8");
                    String[] split = readDate.split("[,]");
                    if (split.length==7) {
                        LogUtils.d("所有数据==" + split.length + "///" + split[0]+split[1]+split[2] + "//垂直角(弧度)=" +getFourPoint(split[3])+ "//水平角(弧度)="
                                +getFourPoint(split[4])+ "//斜距(米)=" +getSixPoint(split[5])+"//"+getSixPoint(split[6]));
//
                    }else if(split.length ==9){
                        LogUtils.d("所有数据==" + split.length + "///" + split[0]+split[1]+split[2] + "/9/垂直角(弧度)=" +getFourPoint(split[5])+ "//9水平角(弧度)="
                                +getFourPoint(split[6])+ "//9斜距(米)=" +getSixPoint(split[7]));
                    }else if(split.length ==11){
                        LogUtils.d("所有数据==" + split.length + "///" + split[0]+split[1]+split[2] + "//11垂直角(弧度)=" +getFourPoint(split[7])+ "//11水平角(弧度)="
                                +getFourPoint(split[8])+ "/11斜距(米)=" +getSixPoint(split[9]));
                    }else {
                        LogUtils.d("所有数据==" + split.length + "///" + split[0]);
                    }
                }else {
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
            }else {
                ChangChunActivity.instance.showTip("仪器连接异常");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
    *  处理返回的坐标数据保留小数点后四位,四舍五入
    * */
    private String getFourPoint(String point){
        double value = Double.parseDouble(point);
        String result = String.format("%.4f",value);
        LogUtils.d("返回的结果"+result);
        return result;
    }

       /*
    *  处理返回的坐标数据保留小数点后六位,四舍五入
    * */
    private String getSixPoint(String point){
        double value = Double.parseDouble(point);
        String result = String.format("%.6f",value);
        LogUtils.d("返回的结果"+result);
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
