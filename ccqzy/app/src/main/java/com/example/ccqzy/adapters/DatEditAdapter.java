package com.example.ccqzy.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ccqzy.R;
import com.example.ccqzy.activitys.DatEditActivity;
import com.example.ccqzy.beans.ModleDatas;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/18.
 */

public class DatEditAdapter extends BaseAdapter {
    private List<ModleDatas> name = new ArrayList<>();
    Context context;
    int order = 1;
    boolean isAdd = false;

    public DatEditAdapter(List<ModleDatas> name, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.dat_edit_item,null);
            viewHolder.tvPointName = (TextView) convertView.findViewById(R.id.tv_point_name);
            viewHolder.tvPointX = (TextView) convertView.findViewById(R.id.tv_pointX);
            viewHolder.tvPointY = (TextView) convertView.findViewById(R.id.tv_pointY);
            viewHolder.tvPointH = (TextView) convertView.findViewById(R.id.tv_pointH);
            viewHolder.tvMove = (TextView) convertView.findViewById(R.id.tv_add_edit);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }

        if (name.get(position).isIsOver()){
            viewHolder.tvPointName.setTextColor(Color.parseColor("#f70808"));
            viewHolder.tvPointX.setTextColor(Color.parseColor("#f70808"));
            viewHolder.tvPointY.setTextColor(Color.parseColor("#f70808"));
            viewHolder.tvPointH.setTextColor(Color.parseColor("#f70808"));
            viewHolder.tvMove.setTextColor(Color.parseColor("#f70808"));
            viewHolder.tvMove.setText("撤销");
        }else {
            viewHolder.tvPointName.setTextColor(Color.parseColor("#504f4f"));
            viewHolder.tvPointX.setTextColor(Color.parseColor("#504f4f"));
            viewHolder.tvPointY.setTextColor(Color.parseColor("#504f4f"));
            viewHolder.tvPointH.setTextColor(Color.parseColor("#504f4f"));
            viewHolder.tvMove.setTextColor(Color.parseColor("#504f4f"));
            viewHolder.tvMove.setText("移除");
        }

        viewHolder.tvPointName.setText(name.get(position).getMeasurePointName());
        viewHolder.tvPointX.setText(name.get(position).getX());
        viewHolder.tvPointY.setText(name.get(position).getY());
        viewHolder.tvPointH.setText(name.get(position).getH());

        viewHolder.tvMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加到数据模板数据库
                if (name.get(position).isIsOver()){
                    DatEditActivity.instance.itemUpdate(name.get(position).getId(),false);
                }else {
                    DatEditActivity.instance.itemUpdate(name.get(position).getId(),true);
                }

            }
        });

        return convertView;
    }

    public class ViewHolder{
        TextView tvPointName;
        TextView tvPointX;
        TextView tvPointY;
        TextView tvPointH;
        TextView tvMove;
    }
}
