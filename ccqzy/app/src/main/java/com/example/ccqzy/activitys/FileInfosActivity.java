package com.example.ccqzy.activitys;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ccqzy.QzyApplication;
import com.example.ccqzy.R;
import com.example.ccqzy.adapters.DatInfoAdapter;
import com.example.ccqzy.beans.CarbodyCalculatedDatas;
import com.example.ccqzy.beans.MeasurePoint;
import com.lidroid.xutils.util.LogUtils;

import org.xutils.DbManager;
import org.xutils.db.Selector;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class FileInfosActivity extends Activity {

    //文件信息集合
    private ListView lvPointInfo;
    DatInfoAdapter adapter;
    //单条目id
    String itemId;

    //测点集合
    List<MeasurePoint> listMeasure = new ArrayList<>();
    //接收从数据库里获取的点坐标
    List<CarbodyCalculatedDatas> allPoint = null;

    DbManager dbManager1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_infos);

        //设置导航栏透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        dbManager1 = x.getDb(((QzyApplication) getApplication()).getCarbodyCalculatedDatasConfig());
        itemId = getIntent().getStringExtra("itemId");
        LogUtils.d("ID="+itemId);
        initTitle();
        initView();

        queryPoint();
    }

    /*
* 初始化控件
* */
    private void initView() {
        lvPointInfo = findView(R.id.lv_dat_info);
    }

    /*
    * 初始化标题头
    * */
    private void initTitle() {
        TextView back = findView(R.id.tv_title_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileInfosActivity.this.finish();
            }
        });

        TextView title = findView(R.id.tv_title_name);
        title.setText("文件信息");
    }

    /*
    * 将.dat文件的内容展示出来的列表
    * */
    private void getDates(List<MeasurePoint> list) {
        adapter = new DatInfoAdapter(list, this);
        lvPointInfo.setDividerHeight(0);
        lvPointInfo.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    //从数据库中获取数据
    public void queryPoint() {
        try {
            Selector<CarbodyCalculatedDatas> selector = dbManager1.selector(CarbodyCalculatedDatas.class);
            allPoint = selector.findAll();
           // LogUtils.d("fileinfosactivty的数据库="+allPoint.toString());
        } catch (DbException e) {
            e.printStackTrace();
        }

        if (allPoint != null) {
            for (CarbodyCalculatedDatas user : allPoint) {
                //添加到列表中id=itemId,且已经测量过的isOver为true的数据加入集合
                if (user.getMeasureInfoId().equals(itemId) && user.isIsOver()) {
                    MeasurePoint measurePoint = new MeasurePoint();
                    measurePoint.setPointName(user.getMeasurePointName());
                    measurePoint.setPointX(user.getX());
                    measurePoint.setPointY(user.getY());
                    measurePoint.setPointH(user.getH());
                    listMeasure.add(measurePoint);
                }
            }
            LogUtils.d("listMeasure=" + listMeasure.toString());
            getDates(listMeasure);
        } else {
            LogUtils.d("没有数据");
        }
    }

    public <T> T findView(int view) {
        return (T) super.findViewById(view);
    }
}
