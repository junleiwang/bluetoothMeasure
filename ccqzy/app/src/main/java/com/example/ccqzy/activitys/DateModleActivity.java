package com.example.ccqzy.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ccqzy.QzyApplication;
import com.example.ccqzy.R;
import com.example.ccqzy.adapters.ModleListAdapter;
import com.example.ccqzy.beans.CarbodyCalculatedDatas;
import com.example.ccqzy.beans.MeasurePoint;
import com.example.ccqzy.beans.ModleDatas;
import com.lidroid.xutils.util.LogUtils;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DateModleActivity extends Activity implements View.OnClickListener {

    //搜索本地的模板文件
    private TextView tvDownModle;
    private TextView tvNext;//测试view

    public ListView lvModleList;//列表集合

    List<String> name = new ArrayList();

    //填充列表条目的adapter
    ModleListAdapter modleListAdapter;

    String datInfos;
   public String uuid;//模板往数据库添加时每个条目的id

   public List<MeasurePoint> listPoint = new ArrayList<>();

    //Carbody坐标数据库
    DbManager dbCarbodyDatas;

    //模板数据库
    DbManager dbModleData;

    public static DateModleActivity instance;
    public DateModleActivity(){
        instance = DateModleActivity.this;
    }

    public String type ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_modle);

        //设置导航栏透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //数据库模板点坐标
        dbCarbodyDatas = x.getDb(((QzyApplication) getApplication()).getCarbodyCalculatedDatasConfig());

        dbModleData = x.getDb(((QzyApplication) getApplication()).getModleConfig());

        //从文件夹界面获取uuid
        uuid = getIntent().getStringExtra("uuid");
        type = getIntent().getStringExtra("type");

        //初始化标题头
        initTitle();

        // 初始化view控件
        initView();

        //得到模板.dat文件
        getDatFiles();

    }

    /*
    * 初始化控件
    * */
    private void initView() {
        tvNext = findView(R.id.tv_next);
        tvDownModle = findView(R.id.tv_down_modle);
        lvModleList = findView(R.id.lv_modle_list);

        tvDownModle.setOnClickListener(this);
        tvNext.setOnClickListener(this);

        lvModleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //展示条目的详细信息
                listPoint.clear();
                if (type.equals("notadd")) {
                    getFileInfo(name.get(position));
                    LogUtils.d("listPoint==" + listPoint.toString());
                    Intent intent = new Intent(DateModleActivity.this, DatInfosActivity.class);
                    intent.putExtra("datInfos", datInfos);
                    intent.putExtra("name", name.get(position));
                    startActivity(intent);
                }else {
                    getFileInfo(name.get(position));
                    Intent intent = new Intent(DateModleActivity.this, DatEditActivity.class);
                    intent.putExtra("datInfos", datInfos);
                    intent.putExtra("name", name.get(position));
                    startActivity(intent);
                }
            }
        });
    }


    //初始化标题头
    private void initTitle() {
        TextView back = findView(R.id.tv_title_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateModleActivity.this.finish();
            }
        });

        TextView title = findView(R.id.tv_title_name);
        title.setText("数据模板");
    }

    /*
    * 展示获取的文件列表
    * */
    public void getModleDates(List<String> name) {
        modleListAdapter = new ModleListAdapter(name, this);
        lvModleList.setAdapter(modleListAdapter);
        modleListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_next:
                Intent intent = new Intent(DateModleActivity.this, FileMessageActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_down_modle:
                //已经停用////////////////////////////////////////////////////////
                name.clear();
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    File path = Environment.getExternalStorageDirectory();// 获得SD卡路径
                    LogUtils.d("path==" + path);
                    // File path = new File("/mnt/sdcard/");
                    File[] files = path.listFiles();// 读取
                    getFileName(files);

                    LogUtils.d(name.size() + name.toString());
                    getModleDates(name);
                }
                break;

            default:
                break;
        }
    }

    //获取.dat文件
    public void getDatFiles(){
        name.clear();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //修改的部分
            File path = new File(Environment.getExternalStorageDirectory()+"/PD_IM_TS/"+"/modelfile/");// 获得SD卡路径
            //File path = Environment.getExternalStorageDirectory();
            LogUtils.d("path==" + path);
            if (!path.exists()) {
                path.mkdir();
            }
            // 修改部分
            File[] files = path.listFiles();// 读取
            getFileName(files);

            LogUtils.d(name.size() + name.toString());
            getModleDates(name);
        }
    }

    /*
    * 得到本地所有的.dat文件
    * */
    private void getFileName(File[] files) {
        name.clear();
        if (files != null) {// 先判断目录是否为空，否则会报空指针
            for (File file : files) {
                if (file.isDirectory()) {
                } else {
                    String fileName = file.getName();
                    if (fileName.endsWith(".dat")) {
                    //if (fileName.endsWith(".xlsx")) {
                        Set<String> map = new HashSet<>();
                        String s = fileName.toString();
                        LogUtils.d("文件名dat：：  " + s);
                        // map.put("Name", fileName.substring(0, fileName.lastIndexOf(".")));
                        map.add(s);
                        if (map.size() > 0) {
                            for (String name1 : map) {
                                name.add(name1);
                            }
                        }

                    }
                }
            }
        }
    }

    /*
    * 获取文件的详细信息
    * 将信息逐行解析并把数据存放到list数组中
    * */
    public void getFileInfo(String fileName) {
        String filepath = "/mnt/sdcard/PD_IM_TS/modelfile/" + fileName;
        try {
            FileInputStream fileInputStream = new FileInputStream(filepath);
            StringBuffer stringBuffer = new StringBuffer();
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            String strLine = null;
            while ((strLine = dataInputStream.readLine()) != null) {
                stringBuffer.append(strLine + "\n");
                analyzeLine(strLine);
            }
            datInfos = new String(stringBuffer);
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
    private void analyzeLine(String line){
        if (line == null){
            LogUtils.d("没有数据");
        }else {
            //以；为分隔符进行数据提取
            String[] split = line.split("[;]");
            MeasurePoint pointObj = new MeasurePoint();
            pointObj.setPointName(split[0]);
            pointObj.setPointX(split[1]);
            pointObj.setPointY(split[2]);
            pointObj.setPointH(split[3]);
            listPoint.add(pointObj);
        }
    }

    /*
    * 将数据存储到数据库中
    * */
    public void addDateToDB(List<MeasurePoint> list1){
        int order = 1;
        for (MeasurePoint mp:list1){
            if (list1.size()>0){
                try {
                    //添加到测量库
                    insertPointName(mp,order);
                    //添加到模板库
                    insertModleData(mp,order);

                } catch (DbException e) {
                    e.printStackTrace();
                }
                order = order+1;
            }
        }
    }

    //将新建的文件添加数据库
    public void insertPointName(MeasurePoint list,int order) throws DbException {

        CarbodyCalculatedDatas user = null;
        try {
            LogUtils.d("要添加的uuid="+uuid);
            user = new CarbodyCalculatedDatas(UUID.randomUUID().toString(),uuid, UUID.randomUUID().toString(),
                    list.getPointName(),order,false,list.getPointX(),list.getPointY(),list.getPointH(),getTime(System.currentTimeMillis()),false);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            dbCarbodyDatas.save(user);
            LogUtils.d("添加数据到dbCarbodyDatas成功" + dbCarbodyDatas.toString());
        } catch (DbException e) {

        }
    }

    //将新建的文件添加模板数据库
    public void insertModleData(MeasurePoint list,int order) throws DbException {

        ModleDatas user = null;
        try {
            LogUtils.d("要添modle加的uuid="+uuid);
            user = new ModleDatas(UUID.randomUUID().toString(),uuid, UUID.randomUUID().toString(),
                    list.getPointName(),order,false,list.getPointX(),list.getPointY(),list.getPointH(),getTime(System.currentTimeMillis()),false);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            dbModleData.save(user);
            LogUtils.d("添加数据到dbModleData是对方的身份的身份成功" + dbModleData.toString());
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

    //自定义findviewbyid()方法
    public <T> T findView(int view) {
        return (T) super.findViewById(view);
    }

}
