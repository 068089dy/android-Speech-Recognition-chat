package com.example.dy.group_demo6.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dy.group_demo6.R;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;

import cn.bmob.newsmssdk.BmobSMS;
import cn.bmob.newsmssdk.exception.BmobException;
import cn.bmob.newsmssdk.listener.RequestSMSCodeListener;
import cn.bmob.newsmssdk.listener.VerifySMSCodeListener;

import static com.example.dy.group_demo6.util.XMPPUtil.createAccount;

public class signupActivity extends AppCompatActivity {
    private Button btn_signup;
    private EditText et_username;
    private EditText et_password;

    boolean asd = false;
    String Bmob_Application_ID = "bf3fe974a31df1f3dbd8a20fcb34bb70";
    private Button btn_vfcode;
    private EditText et_phonenum;
    private EditText et_vfcode;

    private boolean isActivitylive = true;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            Bundle b = msg.getData();
            String data = b.getString("data");
            Toast.makeText(signupActivity.this,data,Toast.LENGTH_SHORT).show();

            if (data.equals("success!")) {
                isActivitylive = false;
                finish();
            }

        }
    };
    private Handler thandler = new Handler(){
        public void handleMessage(Message msg){
            Bundle b = msg.getData();
            String timedata = b.getString("timedata");
            if(timedata!=null) {
                if (!timedata.equals("0")) {
                    Log.d("start", timedata);
                    btn_vfcode.setEnabled(false);
                    btn_vfcode.setText(timedata + "s后重新获取");
                } else if (timedata.equals("0")) {
                    btn_vfcode.setEnabled(true);
                    btn_vfcode.setText("获取验证码");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        BmobSMS.initialize(this,Bmob_Application_ID);
        initview();
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = et_username.getText().toString();
                final String password = et_password.getText().toString();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(!verify(et_phonenum.getText().toString(),et_vfcode.getText().toString())){


                            ConnectionConfiguration connConfig = new ConnectionConfiguration("iot.celitea.cn",5222);
                            XMPPConnection con = new XMPPConnection(connConfig);
                            try{

                                con.connect();
                                if(createAccount(con,username,password)){
                                    Log.d("create","success");
                                    Message msg = handler.obtainMessage();
                                    Bundle b = new Bundle();
                                    b.putString("data","success!");
                                    msg.setData(b);
                                    handler.sendMessage(msg);
                                    con.disconnect();
                                }else{


                                    Message msg = handler.obtainMessage();
                                    Bundle b = new Bundle();
                                    b.putString("data","faield!");
                                    msg.setData(b);
                                    handler.sendMessage(msg);
                                    con.disconnect();
                                }
                            }catch(Exception e){
                                Log.d("sign on error1",e.toString());

                                Message msg = handler.obtainMessage();
                                Bundle b = new Bundle();
                                b.putString("data","faield!");
                                msg.setData(b);
                                handler.sendMessage(msg);
                                con.disconnect();
                            }
                        }
                    }
                }).start();
            }
        });
        btn_vfcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Phone = et_phonenum.getText().toString();
                if(Phone.equals("")){
                    Toast.makeText(signupActivity.this,"请输入电话号码",Toast.LENGTH_LONG).show();
                }else {
                    Log.d("send code","sttart");
                    request(Phone);

                }
            }
        });
    }
    private void initview(){
        btn_signup = (Button) findViewById(R.id.signup);
        et_password = (EditText) findViewById(R.id.edittext_passwd);
        et_username = (EditText) findViewById(R.id.edittext_user);

        btn_vfcode = (Button) findViewById(R.id.btn_vfcode);
        et_phonenum = (EditText) findViewById(R.id.et_phonenum);
        et_vfcode = (EditText) findViewById(R.id.et_vfcode);
    }
    private boolean verify(String phone_num,String vf_code){

        BmobSMS.verifySmsCode(this,phone_num, vf_code, new VerifySMSCodeListener() {

            @Override
            public void done(BmobException ex) {
                // TODO Auto-generated method stub
                if(ex==null){//短信验证码已验证成功
                    Log.i("bmob", "验证通过");
                    Toast.makeText(signupActivity.this,"验证通过",Toast.LENGTH_LONG).show();
                    asd = true;
                }else{
                    Log.i("bmob", "验证失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage());
                    Toast.makeText(signupActivity.this,"验证失败",Toast.LENGTH_LONG).show();
                    asd = false;
                }
            }
        });
        Log.d("返回","wer");
        return asd;
    }
    private void request(String phone_num){
        BmobSMS.requestSMSCode(this, phone_num, "celitea验证码",new RequestSMSCodeListener() {

            @Override
            public void done(Integer smsId,BmobException ex) {
                // TODO Auto-generated method stub
                if(ex==null){//验证码发送成功
                    Log.i("bmob", "短信id："+smsId);//用于查询本次短信发送详情
                    Toast.makeText(signupActivity.this,"发送成功",Toast.LENGTH_LONG).show();
                    vf_btn_wait();

                }else{
                    Toast.makeText(signupActivity.this,"发送失败"+ex,Toast.LENGTH_LONG).show();
                    Log.d("send error",ex.toString());
                }
            }
        });
    }
    private void vf_btn_wait(){
        new Thread(new Runnable(){
            public void run(){
                int i;
                for(i = 60; i>=0 ;i--) {
                    if(isActivitylive) {
                        try {
                            Thread.sleep(1000);
                            //从消息池中取出一个message
                            Message msg = thandler.obtainMessage();
                            //Bundle是message中的数据
                            Bundle b = new Bundle();
                            b.putString("timedata", Integer.toString(i));
                            msg.setData(b);
                            //传递数据
                            thandler.sendMessage(msg); // 向Handler发送消息,更新UI
                        } catch (Exception e) {

                        }
                    }else{
                        break;
                    }

                }
            }
        }).start();
    }
}
