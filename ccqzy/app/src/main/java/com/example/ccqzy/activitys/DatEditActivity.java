package com.example.ccqzy.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ccqzy.QzyApplication;
import com.example.ccqzy.R;
import com.example.ccqzy.adapters.DatEditAdapter;
import com.example.ccqzy.beans.CarbodyCalculatedDatas;
import com.example.ccqzy.beans.MeasurePoint;
import com.example.ccqzy.beans.ModleDatas;
import com.lidroid.xutils.util.LogUtils;

import org.xutils.DbManager;
import org.xutils.db.Selector;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DatEditActivity extends Activity {
    //文件信息集合
    private TextView tvADD;
    private ListView lvPointInfo;
    String datInfos;
    String titleName;
    DatEditAdapter adapter;

    public List<ModleDatas> allPoint = new ArrayList<>();


    public DbManager dbModle;
    DbManager dbCarbodyDatas;

    int order = 1;
    public static DatEditActivity instance;
    public DatEditActivity() {
        instance = DatEditActivity.this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dat_edit);
        //设置导航栏透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

       dbModle = x.getDb(((QzyApplication) getApplication()).getModleConfig());
        //数据库模板点坐标
        dbCarbodyDatas = x.getDb(((QzyApplication) getApplication()).getCarbodyCalculatedDatasConfig());
//        LogUtils.d("数据库dbmodle==" + dbModle.toString());
        try {
            dbModle.dropDb();
            LogUtils.d("数据库dbmodle删除成功");
        } catch (DbException e) {
            e.printStackTrace();
        }

        datInfos = getIntent().getStringExtra("datInfos");
        titleName = getIntent().getStringExtra("name");
        initTitle();
        initView();
        getFileInfo(titleName);

        queryPoint();
        LogUtils.d("DatEdit界面开启");

    }


    /*
    * 初始化控件
    * */
    private void initView() {
        lvPointInfo = findView(R.id.lv_dat_info);
        tvADD = findView(R.id.tv_add);
        tvADD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //把模板数据库里选中的点添加到测量数据库中
                queryModle();

                //进入到测量界面
                if (DateModleActivity.instance.type.equals("automatic")){
                    Intent intent = new Intent(DatEditActivity.this, MeasureActivity.class);
                    intent.putExtra("itemId", DateModleActivity.instance.uuid);
                    intent.putExtra("isTrue","false");
                    startActivity(intent);
                    DateModleActivity.instance.finish();
                    DatEditActivity.this.finish();
                }else {
                    Intent intent = new Intent(DatEditActivity.this, MeasureTestActivity.class);
                    intent.putExtra("itemId", DateModleActivity.instance.uuid);
                    startActivity(intent);
                    DateModleActivity.instance.finish();
                    DatEditActivity.this.finish();
                }

            }
        });
    }

    /*
    * 初始化标题头
    * */
    private void initTitle() {
        TextView back = findView(R.id.tv_title_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatEditActivity.this.finish();
            }
        });

        TextView title = findView(R.id.tv_title_name);
        title.setText("文件信息");
    }

    /*
    * 将.dat文件的内容展示出来的列表
    * */
    private void getDates(List<ModleDatas> list) {
        adapter = new DatEditAdapter(list, this);
        lvPointInfo.setDividerHeight(0);
        lvPointInfo.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    /*
* 获取文件的详细信息
* 将信息逐行解析并把数据存放到list数组中
* */
    public void getFileInfo(String fileName) {
        if (dbModle==null){
        dbModle = x.getDb(((QzyApplication) getApplication()).getModleConfig());
            LogUtils.d("数据库dbmodle添加成功");
        }
        String filepath = "/mnt/sdcard/PD_IM_TS/modelfile/" + fileName;
        try {
            FileInputStream fileInputStream = new FileInputStream(filepath);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            String strLine = null;
            while ((strLine = dataInputStream.readLine()) != null) {
                analyzeLine(strLine);
            }
            dataInputStream.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    * 解析每一行读到的数据
    * */
    private void analyzeLine(String line) {
        if (line == null) {
            LogUtils.d("没有数据");
        } else {
            //以；为分隔符进行数据提取
            String[] split = line.split("[;]");
            MeasurePoint pointObj = new MeasurePoint();
            pointObj.setPointName(split[0]);
            pointObj.setPointX(split[1]);
            pointObj.setPointY(split[2]);
            pointObj.setPointH(split[3]);

            try {
                insertModleData(pointObj);
            } catch (DbException e) {
                e.printStackTrace();
            }

        }
    }

    //将新建的文件添加模板数据库
    public void insertModleData(MeasurePoint list) throws DbException {

        ModleDatas user = null;
        try {
            LogUtils.d("要添modle加的uuid=" + DateModleActivity.instance.uuid);
            user = new ModleDatas(UUID.randomUUID().toString(), DateModleActivity.instance.uuid, UUID.randomUUID().toString(),
                    list.getPointName(), 1, false, list.getPointX(), list.getPointY(), list.getPointH(), getTime(System.currentTimeMillis()), false);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            dbModle.save(user);
            LogUtils.d("所有数据添加模板数据成功==========" + dbModle.toString());
        } catch (DbException e) {

        }
    }

    //将新建的文件添加数据库
    public void insertPointName(ModleDatas list,int order) throws DbException {

        CarbodyCalculatedDatas user = null;
        try {
            LogUtils.d("要添加的uuid="+DateModleActivity.instance.uuid);
            user = new CarbodyCalculatedDatas(UUID.randomUUID().toString(),DateModleActivity.instance.uuid, UUID.randomUUID().toString(),
                    list.getMeasurePointName(),order,list.isIsInitail(),list.getX(),list.getY(),list.getH(),getTime(System.currentTimeMillis()),list.isIsOver());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            dbCarbodyDatas.save(user);
            LogUtils.d("从modle数据库添加到dbCarbodyDatas数据库成功" + dbCarbodyDatas.toString());
        } catch (DbException e) {

        }
    }

    //更改某个数据
    public void itemUpdate(String id, boolean ismove) {
        try {
            LogUtils.d("运行到这里了");
            ModleDatas user = new ModleDatas();
            user.setId(id);
            user.setIsOver(ismove);
            dbModle.update(user, "isOver");
            LogUtils.d("移除modle数据更新成功");

            //更新列表
            queryPoint();

        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    //从数据库中获取数据
    public void queryPoint() {

        try {
            Selector<ModleDatas> selector = dbModle.selector(ModleDatas.class);
            allPoint = selector.findAll();
            if (allPoint != null) {
                LogUtils.d("详情的建站坐标点数据库dbStation=" + allPoint + allPoint.size());
                getDates(allPoint);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    //从数据库中获取数据
    public void queryModle(){

        try {
            Selector<ModleDatas> selector = dbModle.selector(ModleDatas.class);
            List<ModleDatas> modleAllPoint = selector.findAll();
            if (modleAllPoint != null) {
                LogUtils.d("详情的建站坐标点数据库dbStation=" + modleAllPoint + modleAllPoint.size());
                for (ModleDatas md :modleAllPoint){
                    if (md.isIsOver()){
                        LogUtils.d("移除点不添加");
                    }else {
                        insertPointName(md,order);
                        order = order+1;
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    //获取时间的转换格式
    public String getTime(long time) throws ParseException {
        Date d = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        String time1 = sdf.format(d);
        return time1;
    }

    public <T> T findView(int view) {
        return (T) super.findViewById(view);
    }
}
