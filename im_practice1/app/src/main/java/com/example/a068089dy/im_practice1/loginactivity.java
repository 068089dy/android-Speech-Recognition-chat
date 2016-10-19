package com.example.a068089dy.im_practice1;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a068089dy.im_practice1.common.Const;
import com.example.a068089dy.im_practice1.common.Storage;
import com.example.a068089dy.im_practice1.common.XMPPUtil;
import com.example.a068089dy.im_practice1.data.DataWarehouse;
import com.example.a068089dy.im_practice1.data.Logindata;

import org.jivesoftware.smack.XMPPConnection;

public class loginactivity extends AppCompatActivity implements Const{
    private EditText mEditTextusername;
    private EditText mEditTextpasswd;
    private EditText mEditTextserver;
    private CheckBox savepasswd;
    private CheckBox autologin;
    public Logindata mLoginData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);
        mEditTextusername = (EditText) findViewById(R.id.edittext_user);
        mEditTextpasswd = (EditText) findViewById(R.id.edittext_passwd);
        mEditTextserver = (EditText) findViewById(R.id.server);

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
        if(mLoginData.isAutoLogin){
            onClick_Login(null);
        }
        if(mLoginData.isSavePasswd){
            savepasswd.setChecked(mLoginData.isSavePasswd);
        }

    }
    //用于在线程中登陆的方法
    private boolean login(){
        try{
            XMPPConnection connection = XMPPUtil.getXMPPConnection(mLoginData.loginserver);
            if(connection == null){
                throw new Exception("connection error!");
            }

            connection.login(mLoginData.username,mLoginData.passwd);
            DataWarehouse.setXMPPConnection(this,connection);
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    private Handler mHandler = new Handler();
    //登陆按钮的单击事件
    public void onClick_Login(View view){

        //将登陆信息存储到全局变量
        mLoginData.username = mEditTextusername.getText().toString();
        mLoginData.passwd = mEditTextpasswd.getText().toString();
        mLoginData.loginserver = mEditTextserver.getText().toString();

        mLoginData.isAutoLogin = autologin.isChecked();
        mLoginData.isSavePasswd = savepasswd.isSaveEnabled();

        //存储全局变量
        Storage.putString(this,KEY_USERNAME,mLoginData.username);

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
                    Intent intent = new Intent(loginactivity.this,MainActivity.class);
                    startActivity(intent);
                    //finish();
                }
                else{
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(loginactivity.this, "登陆失败，请检查输入信息。", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}