package com.example.ccqzy.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.ccqzy.ChangChunActivity;
import com.example.ccqzy.QzyApplication;
import com.example.ccqzy.R;
import com.example.ccqzy.adapters.EditModleAdapter;
import com.example.ccqzy.beans.EditModle;
import com.example.ccqzy.beans.MeasurePoint;
import com.lidroid.xutils.util.LogUtils;

import org.xutils.DbManager;
import org.xutils.db.Selector;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.example.ccqzy.QzyApplication.isOrder;


public class DatInfosActivity extends Activity implements View.OnClickListener {
    //文件信息集合
    private ListView lvPointInfo;
    //模板按钮
    private TextView tvOrder;
    private TextView tvSaveDat;
    private TextView tvCopyDat;
    private RelativeLayout rlUPDOWN1;

    String datInfos;
    String titleName;
    EditModleAdapter adapter;

    DbManager editModleDB;
    final String password = "123456";

    public List<MeasurePoint> listPoint = new ArrayList<>();
    private List<EditModle> allListPoint = null;

    public static DatInfosActivity instance;

    public DatInfosActivity() {
        instance = DatInfosActivity.this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dat_infos);
        //设置导航栏透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        datInfos = getIntent().getStringExtra("datInfos");
        titleName = getIntent().getStringExtra("name");
        editModleDB = x.getDb(((QzyApplication) getApplication()).getEditModleConfig());
        initTitle();
        initView();
        getFileInfo(titleName);
        //将模板文件加载到数据库中
        addDateToDB(listPoint);

        //遍历数据库中得到的信息
        queryList();
    }


    /*
    * 初始化控件
    * */
    private void initView() {
        lvPointInfo = findView(R.id.lv_modle_edit);
        tvOrder = findView(R.id.tv_edit_order);
        tvSaveDat = findView(R.id.tv_save_modle);
        tvCopyDat = findView(R.id.tv_copy_modle);
        rlUPDOWN1 = findView(R.id.rl_up_down1);
        tvOrder.setOnClickListener(this);
        tvSaveDat.setOnClickListener(this);
        tvCopyDat.setOnClickListener(this);
    }

    /*
    * 初始化标题头
    * */
    private void initTitle() {
        TextView back = findView(R.id.tv_title_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatInfosActivity.this.finish();
            }
        });

        TextView title = findView(R.id.tv_title_name);
        title.setText("文件信息");
    }

    /*
    * 将.dat文件的内容展示出来的列表
    * */
    private void getDates(List<EditModle> list) {
        adapter = new EditModleAdapter(list, this);
        lvPointInfo.setDividerHeight(0);
        lvPointInfo.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_edit_order:
                //调序
                showDialog();
                break;
            case R.id.tv_save_modle:
                //保存模板
                File file = new File(Environment.getExternalStorageDirectory()+"/PD_IM_TS/modelfile/" +titleName);
                if (file.exists()){
                    file.delete();
                    LogUtils.d("删除成功!!");
                }
                saveFileName(titleName,getStrInfos());

                break;
            case R.id.tv_copy_modle:
                //复制模板
                showDialog2();
                break;

            default:
                break;
        }
    }


    //弹出框,输入密码弹框
    public void showDialog() {
        LayoutInflater factory = LayoutInflater.from(DatInfosActivity.this);//提示框
        final View view = factory.inflate(R.layout.editbox_layout, null);//这里必须是final的
        final EditText edit = (EditText) view.findViewById(R.id.editText);//获得输入框对象

        new AlertDialog.Builder(DatInfosActivity.this)
                .setTitle("请输入管理密码")//提示框标题
                .setView(view)
                .setPositiveButton("确定",//提示框的两个按钮
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //事件
                                if (edit.getText().toString().equals(password)) {
                                    if (isOrder) {
                                        isOrder = false;
                                        tvOrder.setText("调序");
                                        tvOrder.setTextColor(Color.parseColor("#504f4f"));
                                    } else {
                                        isOrder = true;
                                        tvOrder.setText("禁调");
                                        tvOrder.setTextColor(Color.parseColor("#f70808"));
                                    }
                                    queryList();
                                } else {
                                    ChangChunActivity.instance.showTip("密码错误!");
                                }
                            }
                        }).setNegativeButton("取消", null).create().show();
    }

    //弹出框,输入密码弹框
    public void showDialog2() {
        LayoutInflater factory = LayoutInflater.from(DatInfosActivity.this);//提示框
        final View view = factory.inflate(R.layout.editbox_layout, null);//这里必须是final的
        final EditText edit = (EditText) view.findViewById(R.id.editText);//获得输入框对象

        new AlertDialog.Builder(DatInfosActivity.this)
                .setTitle("请输入新的dat文件名称")//提示框标题
                .setView(view)
                .setPositiveButton("确定",//提示框的两个按钮
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //事件
                                String name = edit.getText().toString() + ".dat";
                                saveFileName(name, getStrInfos());

                            }
                        }).setNegativeButton("取消", null).create().show();
    }

    /*
* 获取文件的详细信息
* 将信息逐行解析并把数据存放到list数组中
* */
    public void getFileInfo(String fileName) {
        listPoint.clear();
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
            listPoint.add(pointObj);
        }
    }

    /*
* 将创建的文件名保存到指定路径下
* */
    public void saveFileName(String fileName, String infos) {
        String newName = "/"+"PD_IM_TS/modelfile"+"/";
        String filePath = Environment.getExternalStorageDirectory() + newName;
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
        ChangChunActivity.instance.showTip("操作成功,请在模板列表查看");
        DateModleActivity.instance.getDatFiles();
        DatInfosActivity.this.finish();

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
            Selector<EditModle> selector = editModleDB.selector(EditModle.class);
            allListPoint = selector.findAll();
            if (allListPoint != null) {
                Collections.sort(allListPoint);

                for (EditModle user :allListPoint) {
                    //测量的MeasureInfoId与文件id相同,并且isOver为true(表示已经测量过的数据)
                    infos = user.getPointName() + ";" + user.getX() + ";" + user.getY() + ";" + user.getH() + "\r\n";
                    infos2 = infos2 + infos;
                    LogUtils.d("新的更改后的.dat文件顺序"+infos2);
                }
            } else {
                LogUtils.d("没有数据");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }


        return infos2;
    }


    /*
* 将数据存储到数据库中
* */
    public void addDateToDB(List<MeasurePoint> list1) {
        int order = 1;
        for (MeasurePoint mp : list1) {
            if (list1.size() > 0) {
                try {
                    //添加模板编辑库
                    insertPointName(mp, order);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                order = order + 1;
            }
        }
    }

    //将新建的文件添加数据库
    public void insertPointName(MeasurePoint list, int order) throws DbException {

        EditModle user = null;
        try {

            user = new EditModle(UUID.randomUUID().toString(), order, list.getPointName(), list.getPointX(), list.getPointY(), list.getPointH());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            editModleDB.save(user);
            LogUtils.d("添加数据到dbCarbodyDatas成功" + editModleDB.toString());
        } catch (DbException e) {

        }
        order = order + 1;
    }

    /**
     * 列表中的条目向上移动一个位置
     */
    public void itemUpDown(String itemId1, int order1, String itemId2, int order2) {
        update(itemId1, order1);
        update(itemId2, order2);
        queryList();

    }

    //更改数据
    public void update(String id, int order) {
        try {
            EditModle user = new EditModle();
            user.setId(id);
            user.setOrderNum(order);

            editModleDB.update(user, "orderNum");
            LogUtils.d("模板数据更新成功");

        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    //遍历数据库列表展示
    public void queryList() {
        try {
            Selector<EditModle> selector = editModleDB.selector(EditModle.class);
            allListPoint = selector.findAll();
            if (allListPoint != null) {
                LogUtils.d("详情的建站坐标点数据库db=" + allListPoint + allListPoint.size());

                Collections.sort(allListPoint);
                getDates(allListPoint);
                LogUtils.d("更新列表成功!!!");
            } else {
                LogUtils.d("没有数据");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        if (isOrder) {
            rlUPDOWN1.setVisibility(View.VISIBLE);
        } else {
            rlUPDOWN1.setVisibility(View.GONE);
        }
    }

    public <T> T findView(int view) {
        return (T) super.findViewById(view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            editModleDB.dropDb();
        } catch (DbException e) {
            e.printStackTrace();
        }
        isOrder = false;
    }

}
