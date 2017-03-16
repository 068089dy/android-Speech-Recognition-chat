package com.example.dy.group_demo6.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.dy.group_demo6.R;
import com.example.dy.group_demo6.util.Sql;
import com.example.dy.group_demo6.activity.Control.airconditionActivity;
import com.example.dy.group_demo6.activity.Control.buldActivity;
import com.example.dy.group_demo6.activity.Control.fanActivity;
import com.example.dy.group_demo6.activity.Control.tandhsensorActivity;
import com.example.dy.group_demo6.listview.device;
import com.example.dy.group_demo6.listview.deviceAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class deviceActivity extends AppCompatActivity{
    private List<device> devicename_list = new ArrayList<>();
    private android.os.Handler handler = new android.os.Handler();
    private deviceAdapter adapter;
    private ListView lv_device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        initView();
        lv_device.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                device de = devicename_list.get(position);

                if(de.getDevicetype().equals("1")) {
                    Intent intent = new Intent(deviceActivity.this, tandhsensorActivity.class);
                    intent.putExtra("deviceid", de.getDeviceid());
                    startActivity(intent);
                }else if(de.getDevicetype().equals("2")){
                    Intent intent = new Intent(deviceActivity.this, fanActivity.class);
                    intent.putExtra("deviceid", de.getDeviceid());
                    startActivity(intent);
                }else if(de.getDevicetype().equals("3")){
                    Intent intent = new Intent(deviceActivity.this, buldActivity.class);
                    intent.putExtra("deviceid", de.getDeviceid());
                    startActivity(intent);
                }else if(de.getDevicetype().equals("4")){
                    Intent intent = new Intent(deviceActivity.this, airconditionActivity.class);
                    intent.putExtra("deviceid", de.getDeviceid());
                    startActivity(intent);
                }

            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                Sql sql = new Sql("uuuu");
                List<device> list = sql.getDevices();
                Iterator<device> it1 = list.iterator();


                if(list!=null){
                    Log.d("list","start");
                    Iterator<device> it = list.iterator();
                    while(it.hasNext()){
                        device de = it.next();
                        String id = de.getDeviceid();
                        String name = de.getDevicename();
                        String type = de.getDevicetype();

                        Log.d("name",id);
                        devicename_list.add(de);
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();

    }
    private void initView(){
        lv_device = (ListView) findViewById(R.id.lv_device);
        adapter = new deviceAdapter(this,R.layout.deviceitem,devicename_list);

        lv_device.setAdapter(adapter);

    }


}
