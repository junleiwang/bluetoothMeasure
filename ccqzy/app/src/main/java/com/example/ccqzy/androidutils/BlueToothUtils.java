package com.example.ccqzy.androidutils;

/**
 * Created by Administrator on 2018/1/18.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.lidroid.xutils.util.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.example.ccqzy.QzyApplication.inputStream;
import static com.example.ccqzy.QzyApplication.isConnect;
import static com.example.ccqzy.QzyApplication.mSocket;
import static com.example.ccqzy.QzyApplication.outputStream;

/**
 * Created by shaolin on 5/23/16.
 */
public class BlueToothUtils {
    private static final String TAG = "BlueToothUtils";
    private Context mContext;
    public static BlueToothUtils sInstance;
    private BluetoothAdapter mBA;
    // UUID.randomUUID()随机获取UUID
    private final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    // 连接对象的名称
    private final String NAME = "TS883145";

    // 这里本身即是服务端也是客户端，需要如下类
    public BluetoothDevice mOldDevice;
    public BluetoothDevice mCurDevice;
    // 输出流_客户端需要往服务端输出
    private OutputStream os;

    //线程类的实例
    private AcceptThread ac;

    public static synchronized BlueToothUtils getInstance() {
        if (sInstance == null) {
            sInstance = new BlueToothUtils();
        }
        return sInstance;
    }

    public BlueToothUtils() {
        mBA = BluetoothAdapter.getDefaultAdapter();
        ac = new AcceptThread();
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public BluetoothAdapter getBA() {
        return mBA;
    }

    public AcceptThread getAc() {
        return ac;
    }

    public BluetoothDevice getCurDevice() {
        return mCurDevice;
    }

    /**
     * 判断是否打开蓝牙
     *
     * @return
     */
    public boolean isEnabled() {
        if (mBA.isEnabled()) {
            return true;
        }


        return false;
    }

    /**
     * 停止搜索
     */
    public void stopSearchDevices() {
            mBA.cancelDiscovery();
        LogUtils.d("停止蓝牙搜索,进行蓝牙连接");
    }

    /**
     * 搜索设备
     */
    public void searchDevices() {
        // 判断是否在搜索,如果在搜索，就取消搜索
        if (mBA.isDiscovering()) {
            mBA.cancelDiscovery();
        }
        // 开始搜索
        mBA.startDiscovery();
        Log.e(TAG, "正在搜索...");
    }

    /**
     * 获取已经配对的设备
     *
     * @return
     */
    public List<BluetoothDevice> getBondedDevices() {
        List<BluetoothDevice> devices = new ArrayList<>();
        Set<BluetoothDevice> pairedDevices = mBA.getBondedDevices();
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
     * 与设备配对
     *
     * @param device
     */
    public void createBond(BluetoothDevice device) {
        try {
            Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
            createBondMethod.invoke(device);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 与设备解除配对
     *
     * @param device
     */
    public void removeBond(BluetoothDevice device) {
        try {
            Method removeBondMethod = device.getClass().getMethod("removeBond");
            removeBondMethod.invoke(device);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param device
     * @param str  设置PIN码
     * @return
     */
    public boolean setPin(BluetoothDevice device, String str) {
        try {
            Method removeBondMethod = device.getClass().getDeclaredMethod("setPin",
                    new Class[]{byte[].class});
            Boolean returnValue = (Boolean) removeBondMethod.invoke(device,
                    new Object[]{str.getBytes()});
            Log.e("returnValue", "" + returnValue);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 取消用户输入
     */
    public boolean cancelPairingUserInput(BluetoothDevice device) {
        Boolean returnValue = false;
        try {
            Method createBondMethod = device.getClass().getMethod("cancelPairingUserInput");
            returnValue = (Boolean) createBondMethod.invoke(device);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        // cancelBondProcess()
        return returnValue.booleanValue();
    }

    /**
     * 取消配对
     */
    public boolean cancelBondProcess(BluetoothDevice device) {
        Boolean returnValue = null;
        try {
            Method createBondMethod = device.getClass().getMethod("cancelBondProcess");
            returnValue = (Boolean) createBondMethod.invoke(device);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return returnValue.booleanValue();
    }

    /**
     * @param strAddr
     * @param strPsw
     * @return
     */
    public boolean pair(String strAddr, String strPsw) {
        boolean result = false;
        mBA.cancelDiscovery();

        if (!mBA.isEnabled()) {
            mBA.enable();
        }

        if (!BluetoothAdapter.checkBluetoothAddress(strAddr)) { // 检查蓝牙地址是否有效
            Log.d("mylog", "devAdd un effient!");
        }

        BluetoothDevice device = mBA.getRemoteDevice(strAddr);
        if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
            Log.d("mylog", "NOT BOND_BONDED");
            try {
                setPin(device, strPsw); // 手机和蓝牙采集器配对
                createBond(device);
                result = true;
            } catch (Exception e) {
                Log.d("mylog", "setPiN failed!");
                e.printStackTrace();
            } //

        } else {
            Log.d("mylog", "HAS BOND_BONDED");
            try {
                createBond(device);
                setPin(device, strPsw); // 手机和蓝牙采集器配对
                createBond(device);
                result = true;
            } catch (Exception e) {
                Log.d("mylog", "setPiN failed!");
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 获取device.getClass()这个类中的所有Method
     *
     * @param clsShow
     */
    public void printAllInform(Class clsShow) {
        try {
            // 取得所有方法
            Method[] hideMethod = clsShow.getMethods();
            int i = 0;
            for (; i < hideMethod.length; i++) {
                Log.e("method name", hideMethod[i].getName() + ";and the i is:" + i);
            }
            // 取得所有常量
            Field[] allFields = clsShow.getFields();
            for (i = 0; i < allFields.length; i++) {
                Log.e("Field name", allFields[i].getName());
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开蓝牙
     */
    public void openBlueTooth() {
        if (!mBA.isEnabled()) {
            // 弹出对话框提示用户是后打开
                /*Intent intent = new Intent(BAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, 1);*/
            // 不做提示，强行打开
            mBA.enable();
            showToast("打开蓝牙");
        } else {
            showToast("蓝牙已打开");
        }
    }

    /**
     * 关闭蓝牙
     */
    public void closeBlueTooth() {
        if (mBA.isEnabled()) {
            mBA.disable();
            showToast("关闭蓝牙");
        }else {
            showToast("蓝牙已关闭");
        }
    }

    /**
     * 弹出Toast窗口
     *
     * @param message
     */
    private void showToast(String message) {
        if (mContext != null) {
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        } else {
            Log.e(TAG, "message:" + message);
        }
    }


    /**
     * 与设备配对 参考源码：platform/packages/apps/Settings.git
     * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java
     */
    public boolean createBond(Class btClass, BluetoothDevice btDevice)
            throws Exception
    {
        Method createBondMethod = btClass.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    /*
* 创建连接
* */
    public void connect(final BluetoothDevice device) throws IOException {
        mBA.cancelDiscovery();
        try {
            final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
            UUID uuid = UUID.fromString(SPP_UUID);
            mSocket = device.createRfcommSocketToServiceRecord(uuid);
            mSocket.connect();
            isConnect = mSocket.isConnected();//判断蓝牙是否连接
            LogUtils.d("bluetoothserviceconnect 是否创建连接成功"+isConnect);

            //得到连接的输入输出流
            outputStream = mSocket.getOutputStream();
            inputStream = mSocket.getInputStream();

        } catch (Exception e) {
            e.printStackTrace();
        }

      /*  try {
            final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
            UUID uuid = UUID.fromString(SPP_UUID);
            mSocket = device.createRfcommSocketToServiceRecord(uuid);
            mSocket.connect();

            //blueUtil.mSocket.isConnect = true;
            Toast.makeText(mContext, "连接成功！", Toast.LENGTH_LONG).show();
//            os = blueUtil.mSocket.getOutputStream();
//            is = blueUtil.mSocket.getInputStream();
            //开启服务
//            ctx.startService(new Intent(InstrumentSet.this,BackgroundService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
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

    // 服务端，需要监听客户端的线程类
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            showToast(String.valueOf(msg.obj));
            Log.e(TAG, "服务端:" + msg.obj);
            super.handleMessage(msg);
        }
    };


    // 线程服务类
    public class AcceptThread extends Thread {
        private BluetoothServerSocket serverSocket;
        private BluetoothSocket socket;
        // 输入 输出流
        private OutputStream os;
        private InputStream is;

        public AcceptThread() {
            try {
                serverSocket = mBA.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            // 截获客户端的蓝牙消息
            try {
                socket = serverSocket.accept(); // 如果阻塞了，就会一直停留在这里
                is = socket.getInputStream();
                os = socket.getOutputStream();
                while (true) {
                    synchronized (this) {
                        byte[] tt = new byte[is.available()];
                        if (tt.length > 0) {
                            is.read(tt, 0, tt.length);
                            Message msg = new Message();
                            msg.obj = new String(tt, "UTF-8");
                            Log.e(TAG, "客户端:" + msg.obj);
                            handler.sendMessage(msg);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}