package com.example.ccqzy.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ccqzy.R;
import com.example.ccqzy.activitys.PointListActivity;
import com.example.ccqzy.beans.BuildStationPoint;
import com.lidroid.xutils.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import static com.example.ccqzy.QzyApplication.isOrder;

/**
 * Created by Administrator on 2018/1/18.
 */

public class ListPointAdapter extends BaseAdapter {
    private List<BuildStationPoint> name = new ArrayList<>();
    Context context;

    public ListPointAdapter(List<BuildStationPoint> name, Context context) {
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
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.list_point_item, null);
            viewHolder.tvUp = (TextView) convertView.findViewById(R.id.tv_up);
            viewHolder.tvDown = (TextView) convertView.findViewById(R.id.tv_down);
            viewHolder.tvAddPoint = (TextView) convertView.findViewById(R.id.tv_btn_measure);
            viewHolder.tvPointName = (TextView) convertView.findViewById(R.id.tv_point_name);
            viewHolder.tvPointX = (TextView) convertView.findViewById(R.id.tv_pointX12);
            viewHolder.tvPointY = (TextView) convertView.findViewById(R.id.tv_pointY12);
            viewHolder.tvPointH = (TextView) convertView.findViewById(R.id.tv_pointH12);
            viewHolder.tvCancle = (TextView) convertView.findViewById(R.id.tv_cancle);
            viewHolder.rlUpDown = (RelativeLayout) convertView.findViewById(R.id.rl_up_down);
            viewHolder.rlBackColor = (RelativeLayout) convertView.findViewById(R.id.rl_dat_info);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvPointName.setText(name.get(position).getPointName());
        viewHolder.tvPointX.setText(name.get(position).getX());
        viewHolder.tvPointY.setText(name.get(position).getY());
        viewHolder.tvPointH.setText(name.get(position).getH());
        viewHolder.tvAddPoint.setText(String.valueOf(name.get(position).getOrderNum()));

        if (isOrder){
            viewHolder.rlUpDown.setVisibility(View.VISIBLE);
        }else {
            viewHolder.rlUpDown.setVisibility(View.GONE);
        }

        //列表第一条和最后一条的处理方法
        if (position ==0){
            viewHolder.tvUp.setVisibility(View.INVISIBLE);
        }else if (position ==name.size()-1){
            viewHolder.tvDown.setVisibility(View.INVISIBLE);
        }
        //点击向上移动一个位置
        viewHolder.tvUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemId1 = name.get(position).getId();
                String itemId2 = name.get(position-1).getId();
                int order1 = name.get(position-1).getOrderNum();
                int order2 = name.get(position).getOrderNum();

                PointListActivity.instance.itemUpDown(itemId1,order1,itemId2,order2);
                LogUtils.d("互换位置成功!!!");

            }
        });

        //点击向下移动一个位置
        viewHolder.tvDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemId1 = name.get(position).getId();
                String itemId2 = name.get(position+1).getId();
                int order1 = name.get(position+1).getOrderNum();
                int order2 = name.get(position).getOrderNum();

                PointListActivity.instance.itemUpDown(itemId1,order1,itemId2,order2);
                LogUtils.d("互换位置成功!!!");
            }
        });

        viewHolder.tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除一个条目的方法
                PointListActivity.instance.deleteItemById(name.get(position).getId());

            }
        });

        viewHolder.rlBackColor.setBackgroundColor(Color.RED);

        if (position == selectItem) {
            //convertView.setBackgroundColor(Color.RED);
            viewHolder.rlBackColor.setBackgroundColor(Color.GREEN);
        } else {
            // convertView.setBackgroundColor(Color.TRANSPARENT);
            viewHolder.rlBackColor.setBackgroundColor(Color.WHITE);
        }

        return convertView;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    private int selectItem = -1;

    public class ViewHolder {
        TextView tvUp;//向上移动
        TextView tvDown;//向下移动
        TextView tvAddPoint;//点号
        TextView tvPointName;//点名
        TextView tvPointX;//坐标x
        TextView tvPointY;//坐标y
        TextView tvPointH;//坐标h
        TextView tvCancle;//删除按钮
        RelativeLayout rlUpDown;//移动按钮区域
        RelativeLayout rlBackColor;//背景
    }
}
