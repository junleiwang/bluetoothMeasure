package com.example.ccqzy;

import android.app.Application;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.example.ccqzy.androidutils.Preferences;
import com.example.ccqzy.androidutils.PreferencesCookieStore;
import com.lidroid.xutils.util.LogUtils;

import org.xutils.DbManager;
import org.xutils.db.table.TableEntity;
import org.xutils.x;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Administrator on 2018/1/24.
 */

public class QzyApplication extends Application {

    public static final double Metro_ZERO = 0.00000000001;
    public static final double Metro_2PI = 6.283185307179586476925286766559;
    public static final double Metro_05PI = 1.5707963267948966192313216916398;
    public static final double Metro_PI = 3.14159265358979;

    public static final String TURNOVER = "%R1Q,9028:\r";//翻面
    public static final String MEASUERANGLE = "%R1Q,2107:0\r";//测角
    public static final String MEASURE = "%R1Q,17017:2\r";//测距测角
    public static final String ROTANGLE = "%R1Q,9027:20,20,0,0,0\r";//旋转角度

    private static QzyApplication mContext;
    public static Preferences preferences;
    public static PreferencesCookieStore cookieStore;

    // 配置信息
    //服务是否绑定
    public static boolean fistLogin = true;
    //点击全站仪上红键时数据存储的集合
    public static String splitService[];
    //每个activity传递过来的类名
    public static String className;
    //是否继续读取数据的标示
    public static boolean isQuanZY = true;
    //测量界面测量键是否可用,点击按键后为false,旋转完成后为true;
    public static boolean isOnClick = false;

    //蓝牙输入\输出流
    public static InputStream inputStream;
    public static OutputStream outputStream;

    //蓝牙socket
    public static BluetoothSocket mSocket;
    //蓝牙是否连接
    public static boolean isConnect;
    //是否要显示调序 ture显示,false不显示
    public static boolean isOrder = false;

    //新建坐标系的坐标和旋转角
    public static CImCS cimcs;
    //旋转矩阵
    public static CImsRotMatrix rotMatrix;

    //全站仪原点(0,0,0)在车体坐标系下的坐标
    public static CImsPointEx qzyPoint;

    //建立的数据库
    //carbodyMeasureConfig是所有数据的库
    private DbManager.DaoConfig carbodyMeasureConfig;

    //测量各个点的数据库
    private DbManager.DaoConfig carbodyCalculatedDatasConfig;

    //学习测量点的数据库
    private DbManager.DaoConfig measureTestConfig;

    //建站的加点的数据库
    private DbManager.DaoConfig buildStationConfig;

    //建站的点的数据库
    private DbManager.DaoConfig stationConfig;

    //模板坐标点的弧度坐标(dz,dv,s)数据库
    private DbManager.DaoConfig radianConfig;

    //用户名数据库
    private DbManager.DaoConfig userInfo;

    //平移量旋转角缩放比例(车体坐标系)
    private DbManager.DaoConfig cimcsConfig;

    //模板数据库
    private DbManager.DaoConfig modleConfig;

    //模板编辑数据库
    private DbManager.DaoConfig editModleConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        LogUtils.allowD = true;
        LogUtils.customTagPrefix = "xlog";//打印log的标示
        preferences = new Preferences(mContext.getSharedPreferences("qzyq",
                Context.MODE_PRIVATE));
        cookieStore = new PreferencesCookieStore(mContext);
        cookieStore.clear(); // 清除原来的cookie,在登录时清空

        cimcs = new CImCS();
        rotMatrix = new CImsRotMatrix();
        qzyPoint = new CImsPointEx();

        //数据库启动条件
        x.Ext.init(this);
        x.Ext.setDebug(true);
        carbodyMeasureConfig = new DbManager.DaoConfig();
        carbodyCalculatedDatasConfig = new DbManager.DaoConfig();
        measureTestConfig = new DbManager.DaoConfig();
        buildStationConfig = new DbManager.DaoConfig();
        stationConfig = new DbManager.DaoConfig();
        radianConfig = new DbManager.DaoConfig();
        userInfo = new DbManager.DaoConfig();
        cimcsConfig = new DbManager.DaoConfig();
        modleConfig = new DbManager.DaoConfig();
        editModleConfig =  new DbManager.DaoConfig();

        carbodyMeasureConfig.setDbName("carbodyMeasureInfos");
        carbodyCalculatedDatasConfig.setDbName("carbodyCalculatedDatas");
        measureTestConfig.setDbName("measureTest");
        buildStationConfig.setDbName("buildStationPoint");
        stationConfig.setDbName("stationConfig");
        radianConfig.setDbName("radianPoint");
        userInfo.setDbName("userInfo");
        cimcsConfig.setDbName("cimcstable");
        modleConfig.setDbName("modleDatas");
        editModleConfig.setDbName("editModle");

        //carbodyMeasureConfig.setDbDir(File file);
        // 该语句会将数据库文件保存在你想存储的地方
        //如果不设置则默认存储在应用程序目录下/database/dbName.db
        carbodyMeasureConfig.setDbUpgradeListener(new DbManager.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                //用来监听数据库是否升级
                //如果当前数据库版本号比已存在的数据库版本号高则执行此片段
                //用途：软件升级之后在第一次运行时执行一些必要的初始化操作
            }
        });
        carbodyMeasureConfig.setTableCreateListener(new DbManager.TableCreateListener() {
            @Override
            public void onTableCreated(DbManager db, TableEntity table) {
                //用来监听数据表的创建
                //当第一次创建表的时候执行此片段
            }
        });

        //是否允许开启事务
        carbodyMeasureConfig.setAllowTransaction(true);


        carbodyCalculatedDatasConfig.setDbUpgradeListener(new DbManager.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                //用来监听数据库是否升级
                //如果当前数据库版本号比已存在的数据库版本号高则执行此片段
                //用途：软件升级之后在第一次运行时执行一些必要的初始化操作
            }
        });
        carbodyCalculatedDatasConfig.setTableCreateListener(new DbManager.TableCreateListener() {
            @Override
            public void onTableCreated(DbManager db, TableEntity table) {
                //用来监听数据表的创建
                //当第一次创建表的时候执行此片段
            }
        });
        //是否允许开启事务
        carbodyCalculatedDatasConfig.setAllowTransaction(true);


        measureTestConfig.setDbUpgradeListener(new DbManager.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                //用来监听数据库是否升级
                //如果当前数据库版本号比已存在的数据库版本号高则执行此片段
                //用途：软件升级之后在第一次运行时执行一些必要的初始化操作
            }
        });
        measureTestConfig.setTableCreateListener(new DbManager.TableCreateListener() {
            @Override
            public void onTableCreated(DbManager db, TableEntity table) {
                //用来监听数据表的创建
                //当第一次创建表的时候执行此片段
            }
        });
        //是否允许开启事务
        measureTestConfig.setAllowTransaction(true);


        buildStationConfig.setDbUpgradeListener(new DbManager.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                //用来监听数据库是否升级
                //如果当前数据库版本号比已存在的数据库版本号高则执行此片段
                //用途：软件升级之后在第一次运行时执行一些必要的初始化操作
            }
        });
        buildStationConfig.setTableCreateListener(new DbManager.TableCreateListener() {
            @Override
            public void onTableCreated(DbManager db, TableEntity table) {
                //用来监听数据表的创建
                //当第一次创建表的时候执行此片段
            }
        });
        //是否允许开启事务
        buildStationConfig.setAllowTransaction(true);

        stationConfig.setDbUpgradeListener(new DbManager.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                //用来监听数据库是否升级
                //如果当前数据库版本号比已存在的数据库版本号高则执行此片段
                //用途：软件升级之后在第一次运行时执行一些必要的初始化操作
            }
        });
        stationConfig.setTableCreateListener(new DbManager.TableCreateListener() {
            @Override
            public void onTableCreated(DbManager db, TableEntity table) {
                //用来监听数据表的创建
                //当第一次创建表的时候执行此片段
            }
        });

        //是否允许开启事务
        stationConfig.setAllowTransaction(true);

        radianConfig.setDbUpgradeListener(new DbManager.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                //用来监听数据库是否升级
                //如果当前数据库版本号比已存在的数据库版本号高则执行此片段
                //用途：软件升级之后在第一次运行时执行一些必要的初始化操作
            }
        });
        radianConfig.setTableCreateListener(new DbManager.TableCreateListener() {
            @Override
            public void onTableCreated(DbManager db, TableEntity table) {
                //用来监听数据表的创建
                //当第一次创建表的时候执行此片段
            }
        });

        //是否允许开启事务
        radianConfig.setAllowTransaction(true);

        userInfo.setDbUpgradeListener(new DbManager.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                //用来监听数据库是否升级
                //如果当前数据库版本号比已存在的数据库版本号高则执行此片段
                //用途：软件升级之后在第一次运行时执行一些必要的初始化操作
            }
        });
        userInfo.setTableCreateListener(new DbManager.TableCreateListener() {
            @Override
            public void onTableCreated(DbManager db, TableEntity table) {
                //用来监听数据表的创建
                //当第一次创建表的时候执行此片段
            }
        });

        //是否允许开启事务,平移量旋转角缩放比例
        userInfo.setAllowTransaction(true);

        cimcsConfig.setDbUpgradeListener(new DbManager.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                //用来监听数据库是否升级
                //如果当前数据库版本号比已存在的数据库版本号高则执行此片段
                //用途：软件升级之后在第一次运行时执行一些必要的初始化操作
            }
        });
        cimcsConfig.setTableCreateListener(new DbManager.TableCreateListener() {
            @Override
            public void onTableCreated(DbManager db, TableEntity table) {
                //用来监听数据表的创建
                //当第一次创建表的时候执行此片段
            }
        });

        //是否允许开启事务
        cimcsConfig.setAllowTransaction(true);

        //模板数据库
        modleConfig.setDbUpgradeListener(new DbManager.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                //用来监听数据库是否升级
                //如果当前数据库版本号比已存在的数据库版本号高则执行此片段
                //用途：软件升级之后在第一次运行时执行一些必要的初始化操作
            }
        });
        modleConfig.setTableCreateListener(new DbManager.TableCreateListener() {
            @Override
            public void onTableCreated(DbManager db, TableEntity table) {
                //用来监听数据表的创建
                //当第一次创建表的时候执行此片段
            }
        });

        //是否允许开启事务
        modleConfig.setAllowTransaction(true);

        //模板数据库
        editModleConfig.setDbUpgradeListener(new DbManager.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                //用来监听数据库是否升级
                //如果当前数据库版本号比已存在的数据库版本号高则执行此片段
                //用途：软件升级之后在第一次运行时执行一些必要的初始化操作
            }
        });
        editModleConfig.setTableCreateListener(new DbManager.TableCreateListener() {
            @Override
            public void onTableCreated(DbManager db, TableEntity table) {
                //用来监听数据表的创建
                //当第一次创建表的时候执行此片段
            }
        });

        //是否允许开启事务
        editModleConfig.setAllowTransaction(true);

    }


    public static Context getAppContext() {
        return mContext;
    }

    /**
     * 检查当前网络是否可用
     *
     * @return
     * @parammContext
     */

    public DbManager.DaoConfig getCarbodyMeasureConfig() {
        return carbodyMeasureConfig;
    }

    public DbManager.DaoConfig getCarbodyCalculatedDatasConfig() {
        return carbodyCalculatedDatasConfig;
    }

    public DbManager.DaoConfig getMeasureTestConfig() {
        return measureTestConfig;
    }

    public DbManager.DaoConfig getBuildStationConfig() {
        return buildStationConfig;
    }

    public DbManager.DaoConfig getStationConfig() {
        return stationConfig;
    }

    public DbManager.DaoConfig getRadianConfig() {
        return radianConfig;
    }

    public DbManager.DaoConfig getUserInfoConfig() {
        return userInfo;
    }

    public DbManager.DaoConfig getCimcsConfig() {
        return cimcsConfig;
    }

    public DbManager.DaoConfig getModleConfig() {
        return modleConfig;
    }

    public DbManager.DaoConfig getEditModleConfig(){
        return editModleConfig;
    }

    //点坐标
    public static class CPDObvValue {
        public double m_dHzAngle;
        public double m_dVAngle;
        public double m_dSlopeDist;
    }

    //点坐标
    public static class CImsPointEx {
        public double X;
        public double Y;
        public double Z;
    }

    //点坐标
    public static class CImsVector {
        public double I;      //向量X分量
        public double J;       //向量Y分量
        public double K;          //向量Z分量
    }

    //点坐标
    public static class CImsRotAngle {
        public double RotX;            //X方向旋转角度
        public double RotY;             //Y方向旋转角度
        public double RotZ;              //Z方向旋转角度
    }


    /*
    * 坐标系坐标点和旋转角,缩放比例
    * */
    public static class CImCS {
        public double X;
        public double Y;
        public double Z;
        public double RotX;
        public double RotY;
        public double RotZ;
        public double Scale;
    }

    /*
    * 旋转矩阵
    * */
    public static class CImsRotMatrix {
        public double A1;
        public double A2;
        public double A3;
        public double B1;
        public double B2;
        public double B3;
        public double C1;
        public double C2;
        public double C3;
    }

    /*
    * 根据水平角,垂直角和斜距算出点坐标
    * */
    public static class CImsObvValue {
        public double HzAngle;                    //水平角
        public double VAngle;                        //天顶距==垂直角
        public double SlopeDist;                    //斜距(米)
        public int MeaNum;                        //平均测量次数
        public double ResiErrHz;                    //水平角残差
        public double ResiErrV;                    //垂直角残差
        public double ResiErrDist;                //斜距残差
    }

    /*
    * 计算旋转角度
    * double hz
    * double dv
    * */
    public static String getRotAng(double hz, double dv){
        return "%R1Q,9027:"+hz+","+dv+",0,0,0\r";
    }

}
