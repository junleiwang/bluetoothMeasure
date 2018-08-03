package com.example.ccqzy.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ccqzy.R;
import com.example.ccqzy.activitys.DateModleActivity;
import com.example.ccqzy.activitys.FileMessageActivity;
import com.example.ccqzy.activitys.MeasureActivity;
import com.example.ccqzy.activitys.MeasureTestActivity;
import com.lidroid.xutils.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/1/18.
 */

public class ModleListAdapter extends BaseAdapter {
    private List<String> name = new ArrayList<>();
    Context context;
    HashMap<Integer,View> lmap = new HashMap<Integer,View>();

    public ModleListAdapter(List<String> name, Context context) {
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
        if (lmap.get(position)==null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.modlelist_item,null);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name_txt);
            viewHolder.tvAddDat = (TextView) convertView.findViewById(R.id.tv_add_dat);
            viewHolder.tvEditdat = (TextView) convertView.findViewById(R.id.tv_edit_dat);
            convertView.setTag(viewHolder);
            lmap.put(position,convertView);
        }else {
            convertView = lmap.get(position);
            viewHolder= (ViewHolder) convertView.getTag();
        }

        if (DateModleActivity.instance.type.equals("notadd")){
            viewHolder.tvAddDat.setVisibility(View.GONE);
            viewHolder.tvEditdat.setVisibility(View.GONE);
        }else {
            viewHolder.tvAddDat.setVisibility(View.VISIBLE);
           // viewHolder.tvEditdat.setVisibility(View.VISIBLE);
        }

        viewHolder.tvName.setText(name.get(position));
        viewHolder.tvAddDat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加模板
                if(DateModleActivity.instance.uuid !=null) {
                    DateModleActivity.instance.listPoint.clear();
                    DateModleActivity.instance.getFileInfo(name.get(position));
                    DateModleActivity.instance.addDateToDB(DateModleActivity.instance.listPoint);
                    LogUtils.d("添加模块到数据库中"+"type="+DateModleActivity.instance.type);
                    if (DateModleActivity.instance.type.equals("automatic")){
                        Intent intent = new Intent(context, MeasureActivity.class);
                        intent.putExtra("itemId", DateModleActivity.instance.uuid);
                        intent.putExtra("isTrue","false");
                        context.startActivity(intent);
                        DateModleActivity.instance.finish();
                    }else {
                        Intent intent = new Intent(context, MeasureTestActivity.class);
                        intent.putExtra("itemId", DateModleActivity.instance.uuid);
                        context.startActivity(intent);
                        DateModleActivity.instance.finish();
                    }
                }else {
                    DateModleActivity.instance.listPoint.clear();
                    Toast.makeText(context, "请先创建文件再添加模板", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, FileMessageActivity.class);
                    context.startActivity(intent);
                    DateModleActivity.instance.finish();
                }
            }
        });

        viewHolder.tvEditdat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击编辑后事件
            }
        });

        return convertView;
    }

    public class ViewHolder{
        TextView tvName;
        TextView tvAddDat;
        TextView tvEditdat;
    }
}
