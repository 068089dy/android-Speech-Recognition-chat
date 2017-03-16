package com.example.dy.group_demo6.activity.Control;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dy.group_demo6.R;
import com.example.dy.group_demo6.data.mqtt_data;
import com.example.dy.group_demo6.util.mqtt_method;

import org.eclipse.paho.client.mqttv3.MqttTopic;

import java.util.ArrayList;
import java.util.List;

public class airconditionActivity extends AppCompatActivity {

    private String json = "{\"cmd\":\"control\",\"value\":{\"temp\":1111,\"speed\":11,\"mode\":111}}";

    private TextView tv_mode;//模式
    private TextView tv_windspeed;//风速
    private TextView tv_temperature;//温度
    private ImageView iv_aira;//升温
    private ImageView iv_airb;//降温
    private Button btn_node;//模式按钮
    private Button btn_windspeed;//风速按钮
    private Button tbtn_switch;//开关
    private Button btn_confirm;//确定按钮

    private int temp;
    private List<String> mode_list = new ArrayList<>();
    int mode = 0;
    private List<String> winds_list = new ArrayList<>();
    int winds = 0;
    String deviceid;
    MqttTopic topic = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aircondition);
        Intent intent = getIntent();
        deviceid = intent.getStringExtra("deviceid");
        topic = mqtt_data.client.getTopic("/"+deviceid+"/control");
        mode_list.add("自动");
        mode_list.add("制冷");
        mode_list.add("抽湿");
        mode_list.add("制热");
        mode_list.add("送风");

        winds_list.add("自动");
        winds_list.add("低");
        winds_list.add("中");
        winds_list.add("高");
        winds_list.add("固定");

        initview();
        viewsetting();

    }

    private void initview(){
        tv_mode = (TextView) findViewById(R.id.tv_mode);
        tv_temperature = (TextView) findViewById(R.id.tv_temp);
        tv_windspeed = (TextView) findViewById(R.id.tv_windspeed);
        iv_aira = (ImageView) findViewById(R.id.iv_aira);
        iv_airb = (ImageView) findViewById(R.id.iv_airb);
        btn_node = (Button) findViewById(R.id.btn_mode);
        btn_windspeed = (Button) findViewById(R.id.btn_windspeed);
        tbtn_switch = (Button) findViewById(R.id.offon);
        btn_confirm = (Button) findViewById(R.id.btn_sure);

        temp = Integer.parseInt(tv_temperature.getText().toString());
        tbtn_switch.setText("关闭");
    }
    private void viewsetting(){
        iv_aira.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(temp>17) {
                    temp--;
                    tv_temperature.setText(Integer.toString(temp));
                }else{
                    Toast.makeText(airconditionActivity.this,"已到达最低温度",Toast.LENGTH_SHORT).show();
                }
            }
        });
        iv_airb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(temp<30) {
                    temp++;
                    tv_temperature.setText(Integer.toString(temp));
                }else{
                    Toast.makeText(airconditionActivity.this,"已到达最高温度",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_windspeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(winds>=4){
                    winds = 0;
                    tv_windspeed.setText("风速："+winds_list.get(winds));
                }else{
                    winds++;
                    tv_windspeed.setText("风速："+winds_list.get(winds));
                }
            }
        });

        btn_node.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode>=4){
                    mode = 0;
                    tv_mode.setText("模式："+mode_list.get(mode));
                }else{
                    mode++;
                    tv_mode.setText("模式："+mode_list.get(mode));
                }
            }
        });

        tbtn_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tbtn_switch.getText().toString().equals("关闭")){
                    tbtn_switch.setText("打开");
                }else{
                    tbtn_switch.setText("关闭");
                }
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            mqtt_method.publish("{\"cmd\":\"control\",\"name\":\"air\",\"value\":{\"temp\":1111,\"speed\":11,\"mode\":111}}",topic,0);
                        }catch(Exception e){

                        }
                    }
                }).start();
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
