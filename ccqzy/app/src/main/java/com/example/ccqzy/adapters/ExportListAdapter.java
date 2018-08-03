package com.example.ccqzy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ccqzy.R;
import com.example.ccqzy.activitys.DateExportActivity;
import com.example.ccqzy.beans.CarbodyMeasureInfos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/18.
 */

public class ExportListAdapter extends BaseAdapter {
    private List<CarbodyMeasureInfos> name = new ArrayList<>();
    Context context;

    public ExportListAdapter(List<CarbodyMeasureInfos> name, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.exportlist_item,null);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name_export);
            viewHolder.tvExportDat = (TextView) convertView.findViewById(R.id.tv_export_dat);
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
        viewHolder.tvExportDat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //导出文件,name.get(position)为文件名,""为文件里的内容
                DateExportActivity.instance.getItemDates(fileName,name.get(position).getId());
            }
        });

        return convertView;
    }

    public class ViewHolder{
        TextView tvName;
        TextView tvExportDat;
        TextView tvTime;
    }

}
