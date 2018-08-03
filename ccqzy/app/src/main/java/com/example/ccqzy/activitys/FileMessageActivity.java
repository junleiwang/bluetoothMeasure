package com.example.ccqzy.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ccqzy.ChangChunActivity;
import com.example.ccqzy.QzyApplication;
import com.example.ccqzy.R;
import com.example.ccqzy.beans.CarbodyCalculatedDatas;
import com.example.ccqzy.beans.CarbodyMeasureInfos;
import com.example.ccqzy.beans.CarbodyMeasuredDatas;
import com.lidroid.xutils.util.LogUtils;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static com.example.ccqzy.QzyApplication.cimcs;

public class FileMessageActivity extends Activity {

    private TextView tvMakeSure;

    //项目名
    private Spinner etProjectName;
    //组成
    private Spinner etComposition;
    //车辆类型
    private Spinner etCarModle;
    //车型
    private Spinner etCarType;
    //钢号
    private Spinner etSteelNo;
    //列次
    private Spinner etColumn;
    //测回
    private Spinner etMeasureNumber;
    //车辆方位
    private Spinner etCarOriention;

    String projectName;
    String composition;
    String carModle;
    String carType;
    String steelNo;
    String column;
    String measureNumber;
    String carOriention;

    DbManager dbCarbodyMeasureInfo;
    String uuid;

    String flog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_message);

        //标题栏透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        dbCarbodyMeasureInfo = x.getDb(((QzyApplication) getApplication()).getCarbodyMeasureConfig());

        flog = getIntent().getStringExtra("type");
        initTitle();
        initView();
    }

    /*
   * 初始化标题头
   * */
    private void initTitle() {
        TextView back = findView(R.id.tv_title_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileMessageActivity.this.finish();
            }
        });

        TextView title = findView(R.id.tv_title_name);
        title.setText("创建文件信息");
    }

    /*
    * 初始化view
    * */
    private void initView() {
        etProjectName = findView(R.id.sp_project_name);
        etComposition = findView(R.id.sp_composition);
        etCarModle = findView(R.id.sp_car_modle);
        etCarType = findView(R.id.sp_car_type);
        etSteelNo = findView(R.id.sp_steel_number);
        etColumn = findView(R.id.sp_column);
        etMeasureNumber = findView(R.id.sp_measure_number);
        etCarOriention = findView(R.id.sp_car_oriention);


        tvMakeSure = findView(R.id.tv_makesure);
        tvMakeSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEtext();
                if (!projectName.equals("") && !composition.equals("") && !carModle.equals("")
                        && !carType.equals("") && !steelNo.equals("") && !column.equals("")
                        && !measureNumber.equals("") && !carOriention.equals("")) {
                  //  saveFileName();
                    try {
                        insertPointName();
                    } catch (DbException e) {
                        e.printStackTrace();
                    }

                    //应该添加测量模板!!!!!
                    Intent intent = new Intent(FileMessageActivity.this, DateModleActivity.class);
                    intent.putExtra("type",flog);
                    intent.putExtra("uuid", uuid);
                    LogUtils.d("uuid =" + uuid);
                    startActivity(intent);
                    ChangChunActivity.instance.showTip("创建文件成功!");
                    FileMessageActivity.this.finish();
                }else {
                    ChangChunActivity.instance.showTip("文件信息不能为空");
                }
            }
        });

    }

    /*
    * 将创建的文件名保存到指定路径下
    * */
    private void saveFileName() {
        String name = projectName+"_" + composition+"_" + carModle+"_" + carType+"_" +
                steelNo+"_" + column+"-" + measureNumber+"_" + carOriention;
        String filePath = Environment.getExternalStorageDirectory() + "/projectDat/";
        File file1 = new File(filePath);
        if (!file1.exists()) {
            file1.mkdir();
        }
        String fileName = name + ".dat";
        File file = new File(filePath, fileName);
        byte[] test = name.getBytes();
        try {
            createFile(file, test);
        } catch (IOException e) {
            e.printStackTrace();
        }

        LogUtils.d("文件名称;" + file);
    }

    /*
  * 将byte【】数组写入到文件中
  * */
    public void createFile(File path, byte[] content) throws IOException {

        FileOutputStream fos = new FileOutputStream(path);
        InputStream inputStream = new ByteArrayInputStream(content);

        byte[] buffer = new byte[1024];
        int r;
        while ((r = inputStream.read(buffer)) > 0) {
            fos.write(buffer, 0, r);
        }

        fos.write(content, 0, content.length);
        fos.close();
    }

    //将新建的文件添加数据库
    private void insertPointName() throws DbException {
        uuid = UUID.randomUUID().toString();
        LogUtils.d("uuid="+uuid.toString());
        getEtext();
        CarbodyMeasureInfos user = null;
        try {
            user = new CarbodyMeasureInfos(uuid,projectName,composition,carType,carModle,steelNo,
                    column, Integer.parseInt(measureNumber),carOriention,""+cimcs.X,""+cimcs.Y,""+cimcs.Z,
                    getTime(System.currentTimeMillis()), UUID.randomUUID().toString(),false,false,new ArrayList<CarbodyCalculatedDatas>(),
                    new ArrayList<CarbodyMeasuredDatas>());
            LogUtils.d("保存数据的时间;" + getTime(System.currentTimeMillis())+"测回"+measureNumber);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            LogUtils.d("这里出现的问题" + user.toString());
            dbCarbodyMeasureInfo.save(user);
            LogUtils.d("添加数据到dbManagerTeast成功" + dbCarbodyMeasureInfo.toString());
        } catch (DbException e) {

        }
    }

    //获取时间的转换格式
    public String getTime(long time) throws ParseException {
        Date d = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        String time1 = sdf.format(d);
        return time1;
    }

    //得到输入的信息
    private void getEtext(){
        projectName = etProjectName.getSelectedItem().toString();
        projectName = etProjectName.getSelectedItem().toString();
        composition = etComposition.getSelectedItem().toString();
        carModle = etCarModle.getSelectedItem().toString();
        carType = etCarType.getSelectedItem().toString();
        steelNo = etSteelNo.getSelectedItem().toString();
        column = etColumn.getSelectedItem().toString();
        measureNumber = etMeasureNumber.getSelectedItem().toString();
        carOriention = etCarOriention.getSelectedItem().toString();
    }

    /*
    * 自定义初始化控件方法
    * */
    public <T> T findView(int view) {
        return (T) super.findViewById(view);
    }
}
