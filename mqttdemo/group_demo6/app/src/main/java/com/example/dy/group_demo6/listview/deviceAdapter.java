package com.example.dy.group_demo6.listview;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dy.group_demo6.R;
import com.example.dy.group_demo6.activity.Control.airconditionActivity;
import com.example.dy.group_demo6.activity.Control.buldActivity;
import com.example.dy.group_demo6.activity.Control.fanActivity;
import com.example.dy.group_demo6.activity.Control.tandhsensorActivity;
import com.example.dy.group_demo6.activity.deviceActivity;

import java.util.List;

/**
 * Created by dy on 17-3-7.
 */

public class deviceAdapter extends ArrayAdapter<device>{
    private int resourceId;
    public deviceAdapter(Context context, int textViewResourceId, List<device> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    public View getView(int position, View converView, ViewGroup parent){
        device de = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView deviceid = (TextView) view.findViewById(R.id.deviceid);
        TextView devicename = (TextView) view.findViewById(R.id.devicename);
        TextView devicetype = (TextView) view.findViewById(R.id.devicetype);
        deviceid.setText(de.getDeviceid());
        devicename.setText(de.getDevicename());
        if(de.getDevicetype().equals("1")) {
            devicetype.setText("传感器(1)");
        }else if(de.getDevicetype().equals("2")){
            devicetype.setText("风扇(2)");
        }else if(de.getDevicetype().equals("3")){
            devicetype.setText("灯泡(3)");
        }else if(de.getDevicetype().equals("4")){
            devicetype.setText("空调(4)");
        }

        return view;
    }
}
