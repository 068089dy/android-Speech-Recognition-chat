package com.example.dy.group_demo6.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dy.group_demo6.R;
import com.example.dy.group_demo6.data.Storage;
import com.example.dy.group_demo6.util.XMPPUtil;
import com.example.dy.group_demo6.data.data;
import com.example.dy.group_demo6.data.mqtt_data;
import com.example.dy.group_demo6.util.mqtt_method;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class loginActivity extends AppCompatActivity {
    private EditText et_username;
    private EditText et_password;

    private Button btn_login;
    private Button btn_signup;

    private Boolean isXmpplogin = false;
    private Boolean isMqttlogin = false;

    private Handler handler = new Handler();


    String KEY_USERNAME = "username";
    String KEY_PASSWD = "password";
    String KEY_XMPP_SERVER = "xmpp_server";
    String KEY_MQTT_SERVER = "mqtt_server";
    String KEY_SAVE_PASSWORD = "save_password";
    String KEY_AUTO_LOGIN = "auto_login";

    String username;
    String password;
    String xmppserver = "iot.celitea.cn";
    String mqttserver = "tcp://iot.celitea.cn";
    boolean save_password;
    boolean auto_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initview();
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //xmpp登录
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(xmpplogin()){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(loginActivity.this,"xmpp登录成功",Toast.LENGTH_SHORT).show();
                                    //Intent intent = new Intent(loginActivity.this,MainActivity.class);
                                    //startActivity(intent);
                                    //finish();
                                    isXmpplogin = true;
                                }
                            });
                        }else{
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(loginActivity.this,"xmpp登录shiba",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();

                //mqtt登录
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(mqttlogin(et_username.getText().toString(),et_password.getText().toString(),mqttserver,et_username.getText().toString())){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(loginActivity.this,"mqtt登录成功",Toast.LENGTH_SHORT).show();
                                    //Intent intent = new Intent(loginActivity.this,MainActivity.class);
                                    //startActivity(intent);
                                    //finish();
                                    mqtt_data.HOST = mqttserver;
                                    mqtt_data.USERNAME = et_username.getText().toString();
                                    mqtt_data.USERID = mqtt_data.USERNAME;
                                    isMqttlogin = true;
                                }
                            });
                        }else{
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(loginActivity.this,"mqtt登录shiba",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            if (isXmpplogin && isMqttlogin) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Toast.makeText(loginActivity.this,"mqtt登录成功",Toast.LENGTH_SHORT).show();
                                        Storage.putString(loginActivity.this,KEY_USERNAME,et_username.getText().toString());
                                        Storage.putString(loginActivity.this,KEY_PASSWD,et_password.getText().toString());


                                        //Storage.putBoolean(loginActivity.this,KEY_AUTO_LOGIN,auto_login);
                                        //Storage.putBoolean(loginActivity.this,KEY_SAVE_PASSWORD,save_password);
                                        Intent intent = new Intent(loginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                break;
                            }
                        }
                    }
                }).start();
            }
        });
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginActivity.this,signupActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initview(){
        et_username = (EditText) findViewById(R.id.edittext_user);
        et_password = (EditText) findViewById(R.id.edittext_passwd);

        btn_login = (Button) findViewById(R.id.signin);
        btn_signup = (Button) findViewById(R.id.button_register);

        et_username.setText(Storage.getString(this,KEY_USERNAME));

        et_password.setText(Storage.getString(this,KEY_PASSWD));
    }

    private boolean xmpplogin(){
        XMPPConnection con = XMPPUtil.getXMPPConnection(xmppserver);
        try {
            con.login(et_username.getText().toString(), et_password.getText().toString());
            data.connection = con;
            return true;
        }catch (XMPPException e){
            return false;
        }
    }

    private boolean mqttlogin(final String username, final String password, String host,final String userid){

        final String[] Topic = {"/device/state"};
        try {
            mqtt_data.client = mqtt_method.connect(username,password,userid,host,Topic);
            return true;
        }catch (Exception e){
           return false;
        }
    }

}


