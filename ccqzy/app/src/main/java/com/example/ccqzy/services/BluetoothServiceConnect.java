package com.example.ccqzy.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.lidroid.xutils.util.LogUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.example.ccqzy.QzyApplication.inputStream;
import static com.example.ccqzy.QzyApplication.isConnect;
import static com.example.ccqzy.QzyApplication.outputStream;

/**
 * Created by Administrator on 2018/1/29.
 */

public class BluetoothServiceConnect extends Service {

    Context context;
    private static final String TAG = BluetoothServiceConnect.class.getSimpleName();
    private BluetoothAdapter mBluetoothAdapter;
    // 这里本身即是服务端也是客户端，需要如下类
    public BluetoothSocket mSocket;

    // 输出流_客户端需要往服务端输出
    private OutputStream os;


    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d("处理服务开启后的方法 is oncreat.....");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        readDates();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //处理开启服务后的方法

        LogUtils.d("处理服务开启后的方法。。。。。。。。");
        //connect();


        return START_NOT_STICKY;
    }

    @Nullable



    /**
     * 获取已经配对的设备
     *
     * @return
     */
    public List<BluetoothDevice> getBondedDevices() {
        List<BluetoothDevice> devices = new ArrayList<>();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // 判断是否有配对过的设备
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                devices.add(device);
                Log.e(TAG, "BondedDevice:" + device.getName());
            }
        }
        return devices;
    }

    /**
     * 弹出Toast窗口
     *
     * @param message
     */
    private void showToast(String message) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        } else {
            Log.e(TAG, "message:" + message);
        }
    }

    /*
* 创建连接
* */
    public void connect(final BluetoothDevice device) throws IOException {
      //  mBluetoothAdapter.cancelDiscovery();
        try {
            final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
            UUID uuid = UUID.fromString(SPP_UUID);
            mSocket = device.createRfcommSocketToServiceRecord(uuid);
            mSocket.connect();
            LogUtils.d("bluetoothserviceconnect 创建连接成功。。");
            isConnect = true;

            //得到连接的输入输出流
            outputStream = mSocket.getOutputStream();
            inputStream = mSocket.getInputStream();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 传输数据
     *
     * @param message
     */
    public void write(String message) {
        try {
            if (os != null) {
                os.write(message.getBytes("GBK"));
            }
            Log.e(TAG, "write:" + message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
  * 读取全站仪返回的信息
  * */
    String readDate;
    public void readDates() {
        try {
           // is = inputStream;
            while (inputStream != null) {
                byte[] byteRead = new byte[inputStream.available()];
                if (byteRead.length > 0) {
                    inputStream.read(byteRead, 0, byteRead.length);
                    readDate = new String(byteRead, "UTF-8");
                    String[] split = readDate.split("[,]");
                    if (split.length==7) {
                        LogUtils.d("服务器传递的数据==" + split.length + "///" + split[0]+split[1]+split[2] + "//" +getFourPoint(split[3])+ "//"
                                +getFourPoint(split[4])+ "//" +getFourPoint(split[5]));
//                        tvGetDate.setText("返回头"+split[0]+split[1]+split[2]+"\n"+"坐标X； "+getFourPoint(split[3])+"\n"+
//                                "坐标Y； "+getFourPoint(split[4])+"\n"+"坐标H； "+getFourPoint(split[5]));
                    }else {
                        LogUtils.d("服务器传递的数据==" + split.length + "///" + split[0]);
                    }
//                      tvGetDate.setText("返回头"+split[0]+split[1]+split[2]);
                    Log.d(TAG, "服务器传递的数据；=" + readDate);
                }
            }
            LogUtils.d("输出通道为空,请等候数据....");
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

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
