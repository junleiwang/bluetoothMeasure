package com.example.ccqzy.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ccqzy.ChangChunActivity;
import com.example.ccqzy.QzyApplication;
import com.example.ccqzy.R;
import com.example.ccqzy.androidutils.AlertDialogToast;
import com.example.ccqzy.beans.CarbodyCalculatedDatas;
import com.example.ccqzy.beans.CarbodyMeasureInfos;
import com.example.ccqzy.beans.CimcsBean;
import com.lidroid.xutils.util.LogUtils;

import org.xutils.DbManager;
import org.xutils.db.Selector;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import static com.example.ccqzy.QzyApplication.cimcs;
import static com.example.ccqzy.QzyApplication.isConnect;

public class ActionActivity extends Activity implements View.OnClickListener {

    /**
     * rlBluetoothCommit  蓝牙连接模块
     * rlModle            模块模块
     * rlMeasure          测量模块
     * rlBuildStation     建站模块
     * rlDefelation       挠度值模块
     * rlFourLeveling     四角高找平模块
     * rlExportDate       导出数据模块
     * rlUpdate           上传数据模块
     */
    private RelativeLayout rlBluetoothCommit;
    private RelativeLayout rlModle;
    private RelativeLayout rlMeasure;
    private RelativeLayout rlBuildStation;
    private RelativeLayout rlDefelation;
    private RelativeLayout rlFourLeveling;
    private RelativeLayout rlExportDate;
    private RelativeLayout rlUpdate;

    //测试使用
    private ImageView ivTest;

    //上传参数的集合
    List<CarbodyCalculatedDatas> CarbodyCalculatedDatas = new ArrayList<>();
    List<CarbodyMeasureInfos> CarbodyMeasureInfos = new ArrayList<>();

    //测试数据库
    DbManager dbManagerTest;
    DbManager dbManagerAllDates;

    //平移量旋转角数据库
    DbManager dbCimcs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);

        dbManagerTest = x.getDb(((QzyApplication) getApplication()).getCarbodyCalculatedDatasConfig());
        dbManagerAllDates = x.getDb(((QzyApplication) getApplication()).getCarbodyMeasureConfig());
        dbCimcs = x.getDb(((QzyApplication) getApplication()).getCimcsConfig());

        //设置导航栏透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        initView();

        //初始化标题头
        initTitle();

        //判断是否有未完成的测量项目
        if (isConnect) {
            getCheckStates();
        }

        LogUtils.d("建站坐标=" + cimcs.X + "///" + cimcs.Y + "////" + cimcs.Z);

        cimcsMessage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //判断是否有未完成的测量项目
        if (isConnect) {
            getCheckStates();
            cimcsMessage();
        }
    }

    //初始化标题头
    private void initTitle() {
        TextView back = findView(R.id.tv_title_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionActivity.this.finish();
            }
        });

        TextView title = findView(R.id.tv_title_name);
        title.setText("所有功能");
    }

    /*
    * 初始化view控件
    * */
    private void initView() {
        rlBluetoothCommit = findView(R.id.rl_bluetooth_commit);
        rlModle = findView(R.id.rl_modle);
        rlMeasure = findView(R.id.rl_measure);
        rlBuildStation = findView(R.id.rl_build_station);
        rlDefelation = findView(R.id.rl_deflection);
        rlFourLeveling = findView(R.id.rl_four_leveling);
        rlExportDate = findView(R.id.rl_export_data);
        rlUpdate = findView(R.id.rl_update);

        //////////////////////测试
        ivTest = findView(R.id.iv_action);
        ivTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActionActivity.this,Login2Activity.class);
                startActivity(intent);
            }
        });
        ///////////////////////////////////////
        rlBluetoothCommit.setOnClickListener(this);
        rlModle.setOnClickListener(this);
        rlMeasure.setOnClickListener(this);
        rlBuildStation.setOnClickListener(this);
        rlDefelation.setOnClickListener(this);
        rlFourLeveling.setOnClickListener(this);
        rlExportDate.setOnClickListener(this);
        rlUpdate.setOnClickListener(this);//屏蔽上传功能
    }


    /*
    * 各个模块的点击事件
    * */
    @Override
    public void onClick(View v) {

        Intent intent;
        switch (v.getId()) {
            case R.id.rl_bluetooth_commit://蓝牙连接
                intent = new Intent(ActionActivity.this, BluetoothActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_modle://模板
                if (isConnect) {
                    if (cimcs.X == 0.0 && cimcs.Y == 0.0 && cimcs.Z == 0.0) {
                        ChangChunActivity.instance.showTip("请先建立测站");
                    } else {
                        intent = new Intent(ActionActivity.this, DateModleActivity.class);
                        intent.putExtra("type", "notadd");
                        startActivity(intent);
                    }

                } else {
                    ChangChunActivity.instance.showTip("请先与仪器进行蓝牙连接!");
                    intent = new Intent(ActionActivity.this, DateModleActivity.class);
                    intent.putExtra("type", "notadd");
                    startActivity(intent);
                }

                break;
            case R.id.rl_measure://测量
                if (isConnect) {
                    if (cimcs.X == 0.0 && cimcs.Y == 0.0 && cimcs.Z == 0.0) {
                        ChangChunActivity.instance.showTip("请先建立测站");
                    } else {
                        intent = new Intent(ActionActivity.this, DateMeasureActivity.class);
                        startActivity(intent);
                    }
                } else {
                    ChangChunActivity.instance.showTip("请先与仪器进行蓝牙连接!");
//                    intent = new Intent(ActionActivity.this, DateMeasureActivity.class);
//                    startActivity(intent);
                }

                break;
            case R.id.rl_build_station://建站
                if (isConnect) {
                    intent = new Intent(ActionActivity.this, BuildStationActivity.class);
                    startActivity(intent);
                } else {

                    ChangChunActivity.instance.showTip("请先与仪器进行蓝牙连接!");
                }
                break;
            case R.id.rl_deflection://挠度测量
                if (isConnect) {
                    intent = new Intent(ActionActivity.this, DeflectionActivity.class);
                    startActivity(intent);
                } else {
                    ChangChunActivity.instance.showTip("请先与仪器进行蓝牙连接!");
                }

                break;

            case R.id.rl_four_leveling://四角高找平
                if (isConnect) {
                    if (cimcs.X == 0 && cimcs.Y == 0 && cimcs.Z == 0) {
                        ChangChunActivity.instance.showTip("请先建立测站");
                    } else {
                        intent = new Intent(ActionActivity.this, FourCornersHighActivity.class);
                        startActivity(intent);
                    }
                    ////
//                    intent = new Intent(ActionActivity.this, FourCornersHighActivity.class);
//                    startActivity(intent);
                } else {
                    ChangChunActivity.instance.showTip("请先与仪器进行蓝牙连接!");
                    // Toast.makeText(this, "请先与仪器进行蓝牙连接!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.rl_export_data:
                intent = new Intent(ActionActivity.this, DateExportActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_update:
                intent = new Intent(ActionActivity.this, DateUpActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    //开启应用时检查上传是否有未完成的检查
    private void getCheckStates() {
        boolean isFinished = true;
        String itemId = "";
        try {
            Selector<CarbodyMeasureInfos> selector = dbManagerAllDates.selector(CarbodyMeasureInfos.class);
            CarbodyMeasureInfos = selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (CarbodyMeasureInfos !=null) {
            if (CarbodyMeasureInfos.size() > 0) {
                for (CarbodyMeasureInfos measurePoint : CarbodyMeasureInfos) {
                    if (!measurePoint.isFinished()) {
                        isFinished = false;
                        itemId = measurePoint.getId();
                    }
                }
            } else {
                LogUtils.d("CarbodyMeasureInfos还没有数据!!!");
            }
        }

        if (!isFinished) {
            showBackDialog(itemId);
        }
    }

    //弹出框,确认是否继续测量
    public void showBackDialog(final String itemUuid) {
        final AlertDialogToast dialog = new AlertDialogToast(ActionActivity.this);
        dialog.setTitle("还有未测文件,是否继续测量？");
        dialog.setPositiveButton("是", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActionActivity.this, MeasureActivity.class);
                intent.putExtra("itemId", itemUuid);
                intent.putExtra("isTrue","true");
                startActivity(intent);
                //updateFlog(itemUuid);
                dialog.dismiss();
            }
        });


        dialog.setNegativeButton("否", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFlog(itemUuid);
                dialog.dismiss();
            }
        });

    }

    //更新.dat文件是否结束的数据标示
    public void updateFlog(String id) {
        try {
            CarbodyMeasureInfos user = new CarbodyMeasureInfos();
            user.setId(id);
            user.setFinished(true);
            dbManagerAllDates.update(user, "finished");
            LogUtils.d("模板数据更新成功finished=" + user.isFinished() + "//id==" + user.getId());

        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    //遍历车体坐标系的数据
    private void cimcsMessage() {
        try {
            Selector<CimcsBean> selector = dbCimcs.selector(CimcsBean.class);
            List<CimcsBean> usrePoint = selector.findAll();
            if (usrePoint != null) {
                cimcs.X = usrePoint.get(0).getX();
                cimcs.Y = usrePoint.get(0).getY();
                cimcs.Z = usrePoint.get(0).getZ();
                cimcs.RotX = usrePoint.get(0).getRotx();
                cimcs.RotY = usrePoint.get(0).getRoty();
                cimcs.RotZ = usrePoint.get(0).getRotz();
                cimcs.Scale = usrePoint.get(0).getScale();
                LogUtils.d("shujualsdkjfasdjlfk======" + usrePoint.toString());
                LogUtils.d("车体坐标系; X==" + cimcs.X + "Y==" + cimcs.Y + "Z==" + cimcs.Z);

            } else {
                cimcs.X = 0;
                cimcs.Y = 0;
                cimcs.Z = 0;
                cimcs.RotX = 0;
                cimcs.RotY = 0;
                cimcs.RotZ = 0;
                cimcs.Scale = 0;
                LogUtils.d("建站坐标=" + cimcs.X + "///" + cimcs.Y + "////" + cimcs.Z);
                LogUtils.d("车体坐标系数据库没有数据");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public <T> T findView(int view) {
        return (T) super.findViewById(view);
    }

}
