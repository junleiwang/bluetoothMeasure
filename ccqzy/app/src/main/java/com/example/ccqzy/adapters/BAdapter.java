package com.example.ccqzy.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ccqzy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/18.
 */

public class BAdapter extends BaseAdapter {
    private List<BluetoothDevice> devices = new ArrayList<>();
    Context context;

    public BAdapter(List<BluetoothDevice> devices, Context context) {
        this.devices = devices;
        this.context = context;
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.blooth_item,null);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tvMac = (TextView) convertView.findViewById(R.id.tv_mac);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }

        viewHolder.tvName.setText(devices.get(position).getName());
        viewHolder.tvMac.setText(devices.get(position).getAddress());

        return convertView;
    }

    public class ViewHolder{
        TextView tvName;
        TextView tvMac;
    }
}
