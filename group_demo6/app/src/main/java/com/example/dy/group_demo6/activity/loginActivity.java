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
import com.example.dy.group_demo6.XMPPUtil;
import com.example.dy.group_demo6.data;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class loginActivity extends AppCompatActivity {
    private EditText et_username;
    private EditText et_password;
    private EditText et_serverip;
    private Button btn_login;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initview();
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(login()){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(loginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(loginActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }else{
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(loginActivity.this,"登录shiba",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }

    private void initview(){
        et_username = (EditText) findViewById(R.id.edittext_user);
        et_password = (EditText) findViewById(R.id.edittext_passwd);
        et_serverip = (EditText) findViewById(R.id.server);
        btn_login = (Button) findViewById(R.id.signin);
    }

    private boolean login(){
        XMPPConnection con = XMPPUtil.getXMPPConnection("115.28.142.203");
        try {
            con.login(et_username.getText().toString(), et_password.getText().toString());
            data.connection = con;
            return true;
        }catch (XMPPException e){
            return false;
        }
    }

}
