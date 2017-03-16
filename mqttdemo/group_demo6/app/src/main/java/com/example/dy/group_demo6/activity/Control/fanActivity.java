package com.example.dy.group_demo6.activity.Control;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.dy.group_demo6.R;
import com.example.dy.group_demo6.data.mqtt_data;
import com.example.dy.group_demo6.util.mqtt_method;
import com.example.dy.group_demo6.view.ToggleButton;

import org.eclipse.paho.client.mqttv3.MqttTopic;

public class fanActivity extends AppCompatActivity {
    private TextView tv_temp;
    private SeekBar sb_windspeed;
    private ToggleButton tb_switch;
    int pro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan);
        initview();
        Intent intent = getIntent();
        final String deviceid = intent.getStringExtra("deviceid");
        sb_windspeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {//设置滑动监听
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_temp.setText(Integer.toString(progress));
                pro = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String json = "{\"cmd\":\"control\",\"name\":\"fun\",\"value\":"+pro+"}";
                            MqttTopic topic = mqtt_data.client.getTopic("/"+deviceid+"/control");
                            mqtt_method.publish(json, topic, 0);

                        }catch (Exception e){

                        }
                    }
                }).start();
            }
        });


    }
    private void initview(){
        tv_temp = (TextView) findViewById(R.id.tv_temp);
        sb_windspeed = (SeekBar) findViewById(R.id.sb_windspeed);
        //tb_switch = (ToggleButton) findViewById(R.id.tb_switch);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {


            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
