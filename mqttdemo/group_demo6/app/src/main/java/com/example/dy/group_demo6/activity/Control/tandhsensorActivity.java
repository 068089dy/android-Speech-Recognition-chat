package com.example.dy.group_demo6.activity.Control;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ListView;

import com.example.dy.group_demo6.R;
import com.example.dy.group_demo6.listview.Tandh;
import com.example.dy.group_demo6.listview.TandhAdapter;
import com.example.dy.group_demo6.data.mqtt_data;
import com.example.dy.group_demo6.util.json_method;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class tandhsensorActivity extends AppCompatActivity {

    private ListView lv_state;
    private TandhAdapter adapter;
    private List<Tandh> list = new ArrayList<Tandh>();
    //String[] topic = {"/5ecf7fc22fe/state"};
    private Handler handler = new Handler();
    String deviceid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tandhsensor);
        Intent intent = getIntent();
        deviceid = intent.getStringExtra("deviceid");

        initview();
        initdata();
    }

    private void initview(){

        lv_state = (ListView) findViewById(R.id.lv_state);
        adapter = new TandhAdapter(this,R.layout.tandhitem,list);
        lv_state.setAdapter(adapter);
    }
    private void initdata(){
        /*
        int[] i = {1};
        try {
            mqtt_method.subscribe(mqtt_data.client, topic, i);
        }catch(Exception e){

        }
        */
        mqtt_data.client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {}

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                // subscribe后得到的消息会执行到这里面
                /*
                System.out.println("接收消息主题:"+s);
                System.out.println("接收消息Qos:"+mqttMessage.getQos());
                */
                final String message = new String(mqttMessage.getPayload());
                //System.out.println("接收消息内容:"+message);

                /*
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(tandhsensorActivity.this,message,Toast.LENGTH_SHORT).show();

                    }
                });
                */
                Map map = json_method.toMap(new String(mqttMessage.getPayload()));
                Iterator<Tandh> it  = list.iterator();
                boolean isinlist = false;
                int j = 0;
                while(it.hasNext()){
                    j++;
                    if(it.next().getName().equals(map.get("name").toString())){
                        isinlist = true;
                        Tandh t1 = null;
                        t1 = list.get(j-1);
                        Tandh t2 = new Tandh(t1.getName(),map.get("value").toString());
                        list.set(j-1,t2);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                        break;
                    }
                }
                if(!isinlist){
                    Tandh t = new Tandh(map.get("name").toString(),map.get("value").toString());
                    list.add(t);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });

                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {}
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
/*
            try {
                mqtt_method.unsubscribe(mqtt_data.client, topic);
            }catch (Exception e){

            }
*/
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
