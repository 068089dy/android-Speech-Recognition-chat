package com.example.dy.im_practice2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dy.im_practice2.data.XMPP_data;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
/*

*/

public class sign_on_Activity extends AppCompatActivity implements View.OnClickListener {
    private EditText edit_user;
    private EditText edit_passwd;
    private Button btn_signon;
    private ImageView btn_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_on_);
        initdata();
    }
    private void initdata(){
        edit_user = (EditText) findViewById(R.id.username);
        edit_passwd = (EditText) findViewById(R.id.password);
        btn_signon = (Button) findViewById(R.id.btn_signon);
        btn_back = (ImageView) findViewById(R.id.leftBtn);
        btn_signon.setOnClickListener(this);
        btn_back.setOnClickListener(this);
    }
    public void onClick(View v){
        switch(v.getId()){
            case R.id.btn_signon:
                String username = edit_user.getText().toString();
                String password = edit_passwd.getText().toString();
                ConnectionConfiguration connConfig = new ConnectionConfiguration("123.207.174.226", 5222);
                XMPPConnection con = new XMPPConnection(connConfig);
                
                try{
                    con.connect();
                    if(createAccount(con,username,password)){
                        Toast.makeText(sign_on_Activity.this,"sign on success!",Toast.LENGTH_LONG).show();
                        con.disconnect();
                    }else{
                        Toast.makeText(sign_on_Activity.this,"sign on faield!",Toast.LENGTH_LONG).show();
                        con.disconnect();
                    }
                }catch(Exception e){
                    Toast.makeText(sign_on_Activity.this,"sign on faield!",Toast.LENGTH_LONG).show();
                    con.disconnect();
                }
                break;
            case R.id.leftBtn:
                Intent intent = new Intent(sign_on_Activity.this,loginactivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
    //注册用户
    public static boolean createAccount(XMPPConnection connection, String regUserName, String regUserPwd)
    {
        try {
            connection.getAccountManager().createAccount(regUserName, regUserPwd);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
