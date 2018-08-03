package com.example.ccqzy.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ccqzy.QzyApplication;
import com.example.ccqzy.R;
import com.example.ccqzy.activitys.DateUpActivity;
import com.example.ccqzy.androidutils.NetConnectManager;
import com.example.ccqzy.beans.CarbodyMeasureInfos;
import com.lidroid.xutils.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/18.
 */

public class UpdateListAdapter extends BaseAdapter {
    private List<CarbodyMeasureInfos> name = new ArrayList<>();
    Context context;

    public UpdateListAdapter(List<CarbodyMeasureInfos> name, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.uplist_item,null);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name_export);
            viewHolder.tvExportDat = (TextView) convertView.findViewById(R.id.tv_export_dat);
            viewHolder.tvUped = (TextView) convertView.findViewById(R.id.tv_uped_dat);
            viewHolder.tvCancle = (TextView) convertView.findViewById(R.id.tv_cancle);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_export_time);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }

        final String fileName = name.get(position).getName() + "_" + name.get(position).getComponent() + "_" + name.get(position).getCarModel() + "_"
                + name.get(position).getCarType() + "_" + name.get(position).getSteelNo() + "_" + name.get(position).getTrainNo() + "-"
                + name.get(position).getObserveNo() + "_" + name.get(position).getPosition() + ".dat";

        viewHolder.tvTime.setText(name.get(position).getCreationTime());
        viewHolder.tvName.setText(fileName);

        //如果已经上传,上传按钮消失,已上传标准出现
        if (name.get(position).isUped()){
            viewHolder.tvExportDat.setVisibility(View.INVISIBLE);
            viewHolder.tvUped.setVisibility(View.VISIBLE);
            viewHolder.tvUped.setTextColor(Color.parseColor("#f70808"));
        }else {
            viewHolder.tvExportDat.setVisibility(View.VISIBLE);
            viewHolder.tvUped.setVisibility(View.GONE);
        }

        viewHolder.tvExportDat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //判断是否有网络
                if (NetConnectManager.isNetWorkAvailable(QzyApplication.getAppContext())) {
                    LogUtils.d("所选取的条目id======================================"+name.get(position).getId());
                    //获取数据,并上传
                    //DateUpActivity.instance.getItemDates(DateUpActivity.instance.all.get(position).getId());
                    DateUpActivity.instance.getItemDates(name.get(position).getId());
                } else {
                    Toast.makeText(context, "网络不可用,请检测网络连接", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //单条目删除功能
        viewHolder.tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateUpActivity.instance.deleteDb1(DateUpActivity.instance.all.get(position).getId());
            }
        });

        return convertView;
    }

    public class ViewHolder{
        TextView tvName;
        TextView tvExportDat;
        TextView tvCancle;
        TextView tvTime;

        TextView tvUped;
    }
}
