package com.example.ccqzy.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ccqzy.R;
import com.example.ccqzy.activitys.BuildStationActivity;
import com.example.ccqzy.beans.StationPoint;
import com.lidroid.xutils.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import static com.example.ccqzy.QzyApplication.MEASURE;

/**
 * Created by Administrator on 2018/1/18.
 */

public class OrientPointAdapter extends BaseAdapter {
    private List<StationPoint> name = new ArrayList<>();
    Context context;
    private long firstTime = 0;

    public OrientPointAdapter(List<StationPoint> name, Context context) {
        this.name = name;
        this.context = context;
    }

    @Override
    public int getCount() {
        return name.size();
    }

    @Override
    public Object getItem(int position) {
        return name.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder ;
        if (convertView==null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.orient_point_item,null);
            viewHolder.tvMeasure = (TextView) convertView.findViewById(R.id.tv_btn_measure);
            viewHolder.tvPointName = (TextView) convertView.findViewById(R.id.tv_point_name);
            viewHolder.tvPointX = (TextView) convertView.findViewById(R.id.tv_pointX12);
            viewHolder.tvPointY = (TextView) convertView.findViewById(R.id.tv_pointY12);
            viewHolder.tvPointH = (TextView) convertView.findViewById(R.id.tv_pointH12);
            viewHolder.tvCancle = (TextView) convertView.findViewById(R.id.tv_cancle);
            viewHolder.rlBackColor = (RelativeLayout) convertView.findViewById(R.id.rl_dat_info);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }

        viewHolder.tvPointName.setText(name.get(position).getPointNum());
        viewHolder.tvPointX.setText(name.get(position).getX());
        viewHolder.tvPointY.setText(name.get(position).getY());
        viewHolder.tvPointH.setText(name.get(position).getH());

        viewHolder.tvMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long nowTime = System.currentTimeMillis();
                if (nowTime-firstTime >3000) {
                    //点击测量
                    BuildStationActivity.instance.sendMessage(MEASURE);
                    BuildStationActivity.instance.itemId = name.get(position).getId();
                    LogUtils.d("id=="+BuildStationActivity.instance.itemId);
                    firstTime = nowTime;
                }else {
                    Toast.makeText(context, "请勿连续点击!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewHolder.tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //删除一个条目的方法
                BuildStationActivity.instance.deleteItemById(name.get(position).getId());
            }
        });

            viewHolder.rlBackColor.setBackgroundColor(Color.RED);

        if (position == selectItem) {
            //convertView.setBackgroundColor(Color.RED);
            viewHolder.rlBackColor.setBackgroundColor(Color.GREEN);
        }
        else {
           // convertView.setBackgroundColor(Color.TRANSPARENT);
            viewHolder.rlBackColor.setBackgroundColor(Color.WHITE);
        }

        return convertView;
    }

    public  void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }
    private int  selectItem=-1;

    public class ViewHolder{
        TextView tvMeasure;
        TextView tvPointName;
        TextView tvPointX;
        TextView tvPointY;
        TextView tvPointH;
        TextView tvCancle;
        RelativeLayout rlBackColor;
    }
}
