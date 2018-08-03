package com.example.ccqzy.activitys;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ccqzy.ChangChunActivity;
import com.example.ccqzy.QzyApplication;
import com.example.ccqzy.R;
import com.example.ccqzy.adapters.ExportListAdapter;
import com.example.ccqzy.beans.CarbodyCalculatedDatas;
import com.example.ccqzy.beans.CarbodyMeasureInfos;
import com.lidroid.xutils.util.LogUtils;

import org.xutils.DbManager;
import org.xutils.db.Selector;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DateExportActivity extends Activity {

    //列表集合
    List<String> name = new ArrayList<>();
    //文件名称
    private String fileName;
    private RelativeLayout rlNoDate;

    //文件集合
    List<CarbodyMeasureInfos> allPoint = new ArrayList<>();
    //点集合
    List<CarbodyCalculatedDatas> allPoint1= new ArrayList<>();
    private ListView lvDateExport;

    //填充listview的adapter
    ExportListAdapter exportAdapter;

    //文件数据库
    DbManager dbCarbodyMeasureInfo;
    DbManager dbManager1;

    public static DateExportActivity instance;

    public DateExportActivity() {
        instance = DateExportActivity.this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_export);

        //设置导航栏透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //文件数据库
        dbCarbodyMeasureInfo = x.getDb(((QzyApplication) getApplication()).getCarbodyMeasureConfig());
        dbManager1 = x.getDb(((QzyApplication) getApplication()).getCarbodyCalculatedDatasConfig());

        //初始化标题头
        initTitle();
        initView();

        //显示数据列表
        queryPoint();

    }

    //初始化view控件
    private void initView() {
        lvDateExport = findView(R.id.lv_date_export);
        rlNoDate = findView(R.id.rl_no_date);
    }

    /*
    * 初始化标题头
    * */
    private void initTitle() {
        TextView back = findView(R.id.tv_title_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateExportActivity.this.finish();
            }
        });

        TextView title = findView(R.id.tv_title_name);
        title.setText("数据导出");
    }

    //更新列表方法
    public void getExportDate(List<CarbodyMeasureInfos> name) {
        exportAdapter = new ExportListAdapter(name, this);
        lvDateExport.setAdapter(exportAdapter);
        exportAdapter.notifyDataSetChanged();
    }

    //从数据库中获取数据,显示数据列表
    public void queryPoint() {
        try {
            Selector<CarbodyMeasureInfos> selector = dbCarbodyMeasureInfo.selector(CarbodyMeasureInfos.class);
            allPoint = selector.findAll();
            if (allPoint != null) {
                LogUtils.d("详情的点坐标数据库db_all+///cardalsdkj==" + allPoint + allPoint.size());
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        if (allPoint!=null) {
            if (allPoint.size() > 0) {
                getExportDate(allPoint);
                rlNoDate.setVisibility(View.GONE);
            } else {
                rlNoDate.setVisibility(View.VISIBLE);
                LogUtils.d("没有数据");
            }
        }else {
            rlNoDate.setVisibility(View.VISIBLE);
            LogUtils.d("没有数据");
        }
    }

    //从数据库中获取数据
    public String getStrInfos(String itemId) {
        String infos = "";
        String infos2 = "";
        try {
            Selector<CarbodyCalculatedDatas> selector = dbManager1.selector(CarbodyCalculatedDatas.class);
            allPoint1 = selector.findAll();
            if (allPoint1 != null) {
                LogUtils.d("详情的点坐标数据库db_all+///cardalsdkj==" + allPoint1 + allPoint1.size());
                for (CarbodyCalculatedDatas user : allPoint1) {
                    //测量的MeasureInfoId与文件id相同,并且isOver为true(表示已经测量过的数据)
                    if (user.getMeasureInfoId().equals(itemId) && user.isIsOver()){
                       infos = user.getMeasurePointName()+";"+user.getX()+";"+user.getY()+";"+user.getH()+"\r\n";
                        LogUtils.d("infos=="+infos);
                        infos2 = infos2+infos;
                    }
                    LogUtils.d(infos2);
                }


            } else {

                LogUtils.d("没有数据");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return infos2;
    }

    //单条目数据导出的方法
    public void getItemDates(String itemName, String flogId){
        if (allPoint != null){
            for (CarbodyMeasureInfos user1 : allPoint){
                if (user1.getId().equals(flogId)){
                    String infos1 = getStrInfos(flogId);
                    LogUtils.d("infos1=="+infos1);
                    saveFileName(itemName,infos1);
                }
            }
        }else {
            LogUtils.d("文件信息为空");
        }
    }

    //上传所有数据过程添加数据的方法
    public void getAllDates(){
        if (allPoint != null){
            for (CarbodyMeasureInfos user : allPoint){
                fileName = user.getName() + "_" + user.getComponent() + "_" + user.getCarModel() + "_"
                        + user.getCarType() + "_" + user.getSteelNo() + "_" + user.getTrainNo() + "-"
                        + user.getObserveNo() + "_" + user.getPosition() + ".dat";
                String infos1 = getStrInfos(user.getId());
                saveFileName(fileName,infos1);
            }
        }else {
            LogUtils.d("文件信息为空");
        }
    }

    /*
* 将创建的文件名保存到指定路径下
* */
    public void saveFileName(String fileName, String infos) {
        String filePath = Environment.getExternalStorageDirectory() + "/PD_IM_TS/"+"/filedat";
        File file1 = new File(filePath);
        if (!file1.exists()) {
            file1.mkdir();
        }

        File file = new File(filePath, fileName);
        byte[] test = infos.getBytes();
        LogUtils.d("infos="+infos.toString());
        LogUtils.d("我是测试数据byte="+ String.valueOf(test));
        try {
            createFile(file, test);
        } catch (IOException e) {
            e.printStackTrace();
        }

        LogUtils.d("文件名称;" + file);
        ChangChunActivity.instance.showTip("导数数据成功,请在"+filePath+"查看");
       // Toast.makeText(this, "导数数据成功,请在"+filePath+"查看", Toast.LENGTH_SHORT).show();
    }

    /*
  * 将byte【】数组写入到文件中
  * */
    public void createFile(File path, byte[] content) throws IOException {

        FileOutputStream fos = new FileOutputStream(path);
        InputStream inputStream = new ByteArrayInputStream(content);

        byte[] buffer = new byte[1024];
        LogUtils.d("infoContent数据="+ String.valueOf(content));
        int r;
        while ((r = inputStream.read(buffer)) > 0) {
            fos.write(buffer, 0, r);
            LogUtils.d("buffer==="+ String.valueOf(buffer));
        }
        fos.close();
    }

    //自定义获取findviewByid()方法
    public <T> T findView(int view) {
        return (T) super.findViewById(view);
    }
}
