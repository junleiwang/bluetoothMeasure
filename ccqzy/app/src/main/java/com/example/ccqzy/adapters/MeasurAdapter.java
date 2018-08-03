package com.example.ccqzy.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ccqzy.R;
import com.example.ccqzy.beans.MeasurePoint;
import com.lidroid.xutils.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/1/18.
 */

public class MeasurAdapter extends BaseAdapter {
    private List<MeasurePoint> name = new ArrayList<>();
    Context context;
    HashMap<Integer, View> lmap = new HashMap<Integer, View>();

    public MeasurAdapter(List<MeasurePoint> name, Context context) {
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
        if (lmap.get(position)==null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.measure_item,null);
            viewHolder.tvPointName = (TextView) convertView.findViewById(R.id.tv_point_name);
            viewHolder.tvPointX = (TextView) convertView.findViewById(R.id.tv_pointX12);
            viewHolder.tvPointY = (TextView) convertView.findViewById(R.id.tv_pointY12);
            viewHolder.tvPointH = (TextView) convertView.findViewById(R.id.tv_pointH12);
            viewHolder.rlBackColor = (RelativeLayout) convertView.findViewById(R.id.rl_dat_info);
            convertView.setTag(viewHolder);
            lmap.put(position,convertView);
        }else {
            convertView = lmap.get(position);
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

      if (name.get(position).getIsOver().equals("true")){
          viewHolder.tvPointName.setBackgroundColor(Color.parseColor("#08f710"));
          viewHolder.tvPointX.setBackgroundColor(Color.parseColor("#08f710"));
          viewHolder.tvPointY.setBackgroundColor(Color.parseColor("#08f710"));
          viewHolder.tvPointH.setBackgroundColor(Color.parseColor("#08f710"));
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

    /**
     * ListView单条更新
     *
     * @param position
     * @param listView
     */
    public void updataView(final int position, ListView listView, MeasurePoint point) {

        int firstVisiblePosition = listView.getFirstVisiblePosition();
        int lastVisiblePosition = listView.getLastVisiblePosition();
        if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
            name.set(position, point);
            LogUtils.d("position:" + position + " firstVisiblePosition:" + firstVisiblePosition + " lastVisiblePosition:" + lastVisiblePosition);
            View childAt = listView.getChildAt(position - firstVisiblePosition);
//				View childAt = listView.getChildAt(position);
            getView(position, childAt, listView);
            LogUtils.d("单条数据跟新完毕这是在adapter里面的数据");
        }
    }
}
