package com.example.dy.im_practice2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dy.im_practice2.common.Const;
import com.example.dy.im_practice2.common.Storage;
import com.example.dy.im_practice2.common.XMPPUtil;
import com.example.dy.im_practice2.data.DataWarehouse;
import com.example.dy.im_practice2.data.Logindata;
import com.example.dy.im_practice2.data.XMPP_data;

import org.jivesoftware.smack.XMPPConnection;

public class loginactivity extends AppCompatActivity implements Const {
    public static loginactivity ctx = null;

    private EditText mEditTextusername;
    private EditText mEditTextpasswd;
    private EditText mEditTextserver;
    private CheckBox savepasswd;
    private CheckBox autologin;
    public Logindata mLoginData;
    private ProgressBar login_bar;
    private LinearLayout Init_layout;
    Intent intent;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            Bundle b = msg.getData();
            String data = b.getString("data");
            if(data.equals("finish")){
                login_bar.setVisibility(View.GONE);
                finish();
            }else if(data.equals("start")){
                login_bar.setVisibility(View.VISIBLE);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);
        ctx = this;
        Init_layout = (LinearLayout) findViewById(R.id.init_layout);
        Init_layout.setVisibility(View.GONE);
        mEditTextusername = (EditText) findViewById(R.id.edittext_user);
        mEditTextpasswd = (EditText) findViewById(R.id.edittext_passwd);
        mEditTextserver = (EditText) findViewById(R.id.server);
        login_bar = (ProgressBar) findViewById(R.id.login_bar);

        autologin = (CheckBox) findViewById(R.id.auto_login);
        savepasswd = (CheckBox) findViewById(R.id.save_password);
        mLoginData = DataWarehouse.getGlobalData(this).logindata;
        //调出存储在Storage中的信息
        mLoginData.username = Storage.getString(this,KEY_USERNAME);
        mLoginData.passwd = Storage.getString(this,KEY_PASSWD);
        mLoginData.loginserver = Storage.getString(this,KEY_LOGIN_SERVER);
        mLoginData.isAutoLogin = Storage.getBoolean(this,KEY_AUTO_LOGIN);
        mLoginData.isSavePasswd = Storage.getBoolean(this,KEY_SAVE_PASSWORD);

        mEditTextusername.setText(mLoginData.username);
        mEditTextserver.setText(mLoginData.loginserver);
        mEditTextpasswd.setText(mLoginData.passwd);
        autologin.setChecked(mLoginData.isAutoLogin);
        savepasswd.setChecked(mLoginData.isSavePasswd);

        intent = new Intent(loginactivity.this,MainActivity.class);
        if(login_bar.getVisibility() == View.VISIBLE){
            login_bar.setVisibility(View.GONE);
        }

        if(mLoginData.isAutoLogin){
            Init_layout.setVisibility(View.VISIBLE);
            onClick_Login(null);
        }
        if(mLoginData.isSavePasswd){
            savepasswd.setChecked(mLoginData.isSavePasswd);
            mEditTextpasswd.setText(mLoginData.passwd);
        }

    }
    //用于在线程中登陆的方法
    private boolean login(){
        try{
            Message msg = handler.obtainMessage();
            Bundle b = new Bundle();
            b.putString("data", "start");
            msg.setData(b);
            handler.sendMessage(msg); // 向Handler发送消息,更新UI
            Log.d("login","start!");
            XMPPConnection connection = XMPPUtil.getXMPPConnection(mLoginData.loginserver);
            if(connection == null){
                throw new Exception("connection error!");
            }

            connection.login(mLoginData.username,mLoginData.passwd);
            //DataWarehouse.setXMPPConnection(this,connection);
            XMPP_data.connection = connection;
            XMPP_data.my_username = Storage.getString(this,KEY_USERNAME);
            XMPP_data.my_loginserver = Storage.getString(this,KEY_LOGIN_SERVER);
            XMPP_data.my_password = Storage.getString(this,KEY_PASSWD);

        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    private Handler mHandler = new Handler();
    //
    public void onClick_Register(View v){
        Log.d("onClick_Register","start");
        Intent intent = new Intent(loginactivity.this,sign_on_Activity.class);
        startActivity(intent);

    }
    //登陆按钮的单击事件
    public void onClick_Login(View view){
        Log.d("onclick_login","start");


        //将登陆信息存储到全局变量
        mLoginData.username = mEditTextusername.getText().toString();
        mLoginData.passwd = mEditTextpasswd.getText().toString();
        mLoginData.loginserver = mEditTextserver.getText().toString();

        mLoginData.isAutoLogin = autologin.isChecked();
        mLoginData.isSavePasswd = savepasswd.isSaveEnabled();

        //存储全局变量
        Storage.putString(this,KEY_USERNAME,mLoginData.username);
        Storage.putString(this,KEY_PASSWD,mLoginData.passwd);
        Storage.putString(this,KEY_LOGIN_SERVER,mLoginData.loginserver);
        Storage.putBoolean(this,KEY_AUTO_LOGIN,mLoginData.isAutoLogin);
        Storage.putBoolean(this,KEY_SAVE_PASSWORD,mLoginData.isSavePasswd);
        if(mLoginData.isAutoLogin){
            Storage.putString(this,KEY_PASSWD,mLoginData.passwd);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                if(login()){//登陆成功
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(loginactivity.this, "登陆成功！", Toast.LENGTH_SHORT).show();
                        }
                    });

                    intent.putExtra("username",mLoginData.username);
                    intent.putExtra("loginserver",mLoginData.loginserver);
                    Message msg = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("data", "finish");
                    msg.setData(b);
                    handler.sendMessage(msg); // 向Handler发送消息,更新UI
                    startActivity(intent);
                    //finish();
                }
                else{
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(loginactivity.this, "登陆失败，请检查输入信息。", Toast.LENGTH_SHORT).show();
                            Message msg = handler.obtainMessage();
                            Bundle b = new Bundle();
                            b.putString("data", "finish");
                            msg.setData(b);
                            handler.sendMessage(msg); // 向Handler发送消息,更新UI
                        }
                    });
                }
            }
        }).start();
    }
}