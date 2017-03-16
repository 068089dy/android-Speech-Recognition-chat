package com.example.dy.group_demo6.activity.Control;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dy.group_demo6.R;
import com.example.dy.group_demo6.data.mqtt_data;
import com.example.dy.group_demo6.util.mqtt_method;
import com.example.dy.group_demo6.view.ToggleButton;

import org.eclipse.paho.client.mqttv3.MqttTopic;


public class buldActivity extends AppCompatActivity {
    private ToggleButton btn_buld;
    String onjson = "{\"cmd\":\"control\",\"value\":\"1\"}";
    String offjson = "{\"cmd\":\"control\",\"value\":\"0\"}";
    ImageView light;
    MqttTopic topic = null;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buld);
        initview();
        Intent intent = getIntent();
        final String deviceid = intent.getStringExtra("deviceid");
        topic = mqtt_data.client.getTopic("/"+deviceid+"/control");

    }
    private void initview(){
        btn_buld = (ToggleButton) findViewById(R.id.btn_buld);
        light = (ImageView) findViewById(R.id.light);
        btn_buld.setToggleOn(false);
        btn_buld.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if(on) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mqtt_method.publish(onjson, topic, 0);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "已开启", Toast.LENGTH_SHORT).show();
                                        light.setImageResource(R.drawable.lighta);
                                    }
                                });
                            }catch(Exception e){
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "开启失败", Toast.LENGTH_SHORT).show();
                                        btn_buld.setToggleOn(false);
                                    }
                                });
                            }

                        }
                    }).start();


                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mqtt_method.publish(offjson, topic, 0);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "已关闭", Toast.LENGTH_SHORT).show();
                                        light.setImageResource(R.drawable.lightb);
                                    }
                                });
                            }catch(Exception e){
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "关闭失败", Toast.LENGTH_SHORT).show();
                                        btn_buld.setToggleOn(false);
                                    }
                                });
                            }

                        }
                    }).start();
                }
            }
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
