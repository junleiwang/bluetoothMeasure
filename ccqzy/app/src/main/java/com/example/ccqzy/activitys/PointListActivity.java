package com.example.ccqzy.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ccqzy.ChangChunActivity;
import com.example.ccqzy.QzyApplication;
import com.example.ccqzy.R;
import com.example.ccqzy.adapters.ListPointAdapter;
import com.example.ccqzy.beans.BuildStationPoint;
import com.lidroid.xutils.util.LogUtils;

import org.xutils.DbManager;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.example.ccqzy.QzyApplication.isOrder;

public class PointListActivity extends Activity implements View.OnClickListener {

    //点列表
    private ListView lvPoint;
    //模板按钮
    private TextView tvOrder;
    private TextView tvOutDat;
    private TextView tvAddPt;
    private RelativeLayout rlUPDOWN1;
    private EditText etDatName;
    String datName;

    //建站点列表数据库
    DbManager dbStationPoint;

    //列表填充
    ListPointAdapter lpAdapter;
    AdapterView.OnItemClickListener mListOnItemClick;

    List<BuildStationPoint> allPoint = new ArrayList<>();

    public static PointListActivity instance;

    public PointListActivity() {
        instance = PointListActivity.this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_list);


        //标题栏透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //初始化建站点的数据库
        dbStationPoint = x.getDb(((QzyApplication) getApplication()).getBuildStationConfig());

        //初始化头部标题
        initTitle();
        //初始化控件
        initView();

        //单条目点击事件
        mListOnItemClick = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lpAdapter.setSelectItem(position);
                lpAdapter.notifyDataSetChanged();

            }
        };

        //获取数据库中的点数据
        queryPoint();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isOrder){
            rlUPDOWN1.setVisibility(View.VISIBLE);
        }else {
            rlUPDOWN1.setVisibility(View.GONE);
        }
    }

    /*
       * 初始化标题头
       * */
    private void initTitle() {
        TextView back = findView(R.id.tv_title_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PointListActivity.this.finish();
            }
        });

        TextView title = findView(R.id.tv_title_name);
        title.setText("模板点列表");
    }

    /*
    * 初始化view
    * */
    private void initView() {
        tvOrder = findView(R.id.tv_edit_order);
        tvOutDat = findView(R.id.tv_make_modle);
        tvAddPt = findView(R.id.tv_add_pt);
        etDatName = findView(R.id.et_dat_name);
        rlUPDOWN1 = findView(R.id.rl_up_down1);

        lvPoint = findView(R.id.lv_point);
        lvPoint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PointListActivity.this, AddPointActivity.class);
                intent.putExtra("itemId", allPoint.get(position).getId());
                startActivity(intent);
                finish();
            }
        });

        tvOrder.setOnClickListener(this);
        tvOutDat.setOnClickListener(this);
        tvAddPt.setOnClickListener(this);

    }

    /*
    * 刷新列表数据
    * */
    public void getPointListDates(List<BuildStationPoint> list) {
        lpAdapter = new ListPointAdapter(list, this);
        lvPoint.setAdapter(lpAdapter);
        lpAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_make_modle:
                //点击导出模板按钮
               /* datName = etDatName.getText().toString()+".dat";
                if (!TextUtils.isEmpty(datName)){
                    saveFileName(datName,getStrInfos());
                }else {
                    ChangChunActivity.instance.showTip("文件名不能为空!!");
                }*/
                showDialog2();
                break;
            case R.id.tv_add_pt:
                intent = new Intent(PointListActivity.this, AddPointActivity.class);
                intent.putExtra("itemId", UUID.randomUUID().toString());
                startActivity(intent);
                PointListActivity.this.finish();
                break;
            case R.id.tv_edit_order:
                showDialog();
                break;


            default:
                break;
        }

    }
    String password = "123456";

    //弹出框,输入密码弹框
    public void showDialog2() {
        LayoutInflater factory = LayoutInflater.from(PointListActivity.this);//提示框
        final View view = factory.inflate(R.layout.editbox_layout, null);//这里必须是final的
        final EditText edit = (EditText) view.findViewById(R.id.editText);//获得输入框对象

        new AlertDialog.Builder(PointListActivity.this)
                .setTitle("请输入新的dat文件名称")//提示框标题
                .setView(view)
                .setPositiveButton("确定",//提示框的两个按钮
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //事件
                                String name = edit.getText().toString() + ".dat";

                                if (!TextUtils.isEmpty(name)){
                                    saveFileName(name,getStrInfos());
                                }else {
                                    ChangChunActivity.instance.showTip("文件名不能为空!!");
                                }
                                //saveFileName(name, getStrInfos());

                            }
                        }).setNegativeButton("取消", null).create().show();
    }

    //弹出框,输入密码弹框
    public void showDialog(){
        LayoutInflater factory = LayoutInflater.from(PointListActivity.this);//提示框
        final View view = factory.inflate(R.layout.editbox_layout, null);//这里必须是final的
        final EditText edit=(EditText)view.findViewById(R.id.editText);//获得输入框对象

        new AlertDialog.Builder(PointListActivity.this)
                .setTitle("请输入管理密码")//提示框标题
                .setView(view)
                .setPositiveButton("确定",//提示框的两个按钮
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //事件
                                if (edit.getText().toString().equals(password)){
                                    if (isOrder){
                                        isOrder = false;
                                        tvOrder.setText("调序");
                                        tvOrder.setTextColor(Color.parseColor("#504f4f"));
                                    }else {
                                        isOrder = true;
                                        tvOrder.setText("禁调");
                                        tvOrder.setTextColor(Color.parseColor("#f70808"));
                                    }
                                    queryPoint();
                                }else {
                                    ChangChunActivity.instance.showTip("密码错误!");
                                }
                            }
                        }).setNegativeButton("取消", null).create().show();
    }


    /*
 * 将创建的文件名保存到指定路径下
 * */
    public void saveFileName(String fileName, String infos) {
        //String filePath = Environment.getExternalStorageDirectory() + "/projectDat/";
        String filePath = Environment.getExternalStorageDirectory() + "/PD_IM_TS/"+"/modelfile/";
        File file1 = new File(filePath);
        if (!file1.exists()) {
            file1.mkdir();
        }

        File file = new File(filePath, fileName);
        byte[] test = infos.getBytes();
        LogUtils.d("infos=" + infos.toString());
        try {
            createFile(file, test);
        } catch (IOException e) {
            e.printStackTrace();
        }

        LogUtils.d("文件名称;" + file);
        ChangChunActivity.instance.showTip("导数数据成功,请在" + filePath + "查看");
        // Toast.makeText(this, "导数数据成功,请在"+filePath+"查看", Toast.LENGTH_SHORT).show();
    }

    /*
  * 将byte【】数组写入到文件中
  * */
    public void createFile(File path, byte[] content) throws IOException {

        FileOutputStream fos = new FileOutputStream(path);
        InputStream inputStream = new ByteArrayInputStream(content);

        byte[] buffer = new byte[1024];
        LogUtils.d("infoContent数据=" + String.valueOf(content));
        int r;
        while ((r = inputStream.read(buffer)) > 0) {
            fos.write(buffer, 0, r);
            LogUtils.d("buffer===" + String.valueOf(buffer));
        }
        fos.close();
    }

    //从数据库中获取数据
    public String getStrInfos() {
        String infos = "";
        String infos2 = "";
        try {
            Selector<BuildStationPoint> selector = dbStationPoint.selector(BuildStationPoint.class);
            allPoint = selector.findAll();
            if (allPoint != null) {
                //将数据重新排序导出
                Collections.sort(allPoint);
                for (BuildStationPoint user : allPoint) {
                    //测量的MeasureInfoId与文件id相同,并且isOver为true(表示已经测量过的数据)
                    infos = user.getPointName() + ";" + user.getX() + ";" + user.getY() + ";" + user.getH() + "\r\n";
                    infos2 = infos2 + infos;
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

    //从数据库中获取数据
    public void queryPoint() {

        try {
            Selector<BuildStationPoint> selector = dbStationPoint.selector(BuildStationPoint.class);
            allPoint = selector.findAll();
            if (allPoint != null) {
                LogUtils.d("详情的建站坐标点数据库db=" + allPoint + allPoint.size());

                Collections.sort(allPoint);
                getPointListDates(allPoint);
                LogUtils.d("更新列表成功!!!");
            } else {
                LogUtils.d("没有数据");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        if (isOrder){
            rlUPDOWN1.setVisibility(View.VISIBLE);
        }else {
            rlUPDOWN1.setVisibility(View.GONE);
        }

    }


    //通过自己构建条件来删除
    public void deleteItemById(String id) {
        try {
            dbStationPoint.delete(BuildStationPoint.class, WhereBuilder.b("id", "=", id));
            LogUtils.d("删除成功,刷新成功");
            //参数解析 第一个参数是列名 第二个参数是条件= != > <等等条件，第三个参数为传递的值
            //如果条件参数不止一个的话，我们还可以使用.and("id", "=", id)方法
            //同理还有.or("id", "=", id)方法
            queryPoint();
        } catch (DbException e) {
        }
    }

    /**
    * 列表中的条目向上移动一个位置
     *
    * */
    public void itemUpDown(String itemId1, int order1, String itemId2, int order2){
        update(itemId1,order1);
        update(itemId2,order2);
        queryPoint();

    }

    //更改数据
    public void update(String id, int order) {
        try {
            BuildStationPoint user = new BuildStationPoint();
            user.setId(id);
            user.setOrderNum(order);

            dbStationPoint.update(user,"orderNum");
            LogUtils.d("模板数据更新成功");

        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /*
    * 自定义初始化控件方法
    * */
    public <T> T findView(int view) {
        return (T) super.findViewById(view);
    }

}
