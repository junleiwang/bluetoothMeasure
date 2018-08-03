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
import com.example.ccqzy.beans.MeasurePoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/18.
 */

public class UpdateAdapter extends BaseAdapter {
    private List<MeasurePoint> name = new ArrayList<>();
    Context context;

    public UpdateAdapter(List<MeasurePoint> name, Context context) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder ;
        if (convertView==null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.update_item,null);
            viewHolder.tvPointName = (TextView) convertView.findViewById(R.id.tv_point_name);
            viewHolder.tvPointX = (TextView) convertView.findViewById(R.id.tv_pointX12);
            viewHolder.tvPointY = (TextView) convertView.findViewById(R.id.tv_pointY12);
            viewHolder.tvPointH = (TextView) convertView.findViewById(R.id.tv_pointH12);
            viewHolder.rlBackColor = (RelativeLayout) convertView.findViewById(R.id.rl_dat_info);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }

        viewHolder.tvPointName.setText(name.get(position).getPointName());
        viewHolder.tvPointX.setText(name.get(position).getPointX());
        viewHolder.tvPointY.setText(name.get(position).getPointY());
        viewHolder.tvPointH.setText(name.get(position).getPointH());

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
        TextView tvPointName;
        TextView tvPointX;
        TextView tvPointY;
        TextView tvPointH;
        RelativeLayout rlBackColor;
    }
}
