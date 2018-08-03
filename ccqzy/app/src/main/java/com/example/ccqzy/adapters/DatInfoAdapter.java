package com.example.ccqzy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ccqzy.R;
import com.example.ccqzy.beans.MeasurePoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/18.
 */

public class DatInfoAdapter extends BaseAdapter {
    private List<MeasurePoint> name = new ArrayList<>();
    Context context;

    public DatInfoAdapter(List<MeasurePoint> name, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.dat_info_item,null);
            viewHolder.tvPointName = (TextView) convertView.findViewById(R.id.tv_point_name);
            viewHolder.tvPointX = (TextView) convertView.findViewById(R.id.tv_pointX);
            viewHolder.tvPointY = (TextView) convertView.findViewById(R.id.tv_pointY);
            viewHolder.tvPointH = (TextView) convertView.findViewById(R.id.tv_pointH);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }

        viewHolder.tvPointName.setText(name.get(position).getPointName());
        viewHolder.tvPointX.setText(name.get(position).getPointX());
        viewHolder.tvPointY.setText(name.get(position).getPointY());
        viewHolder.tvPointH.setText(name.get(position).getPointH());

        return convertView;
    }

    public class ViewHolder{
        TextView tvPointName;
        TextView tvPointX;
        TextView tvPointY;
        TextView tvPointH;
    }
}
