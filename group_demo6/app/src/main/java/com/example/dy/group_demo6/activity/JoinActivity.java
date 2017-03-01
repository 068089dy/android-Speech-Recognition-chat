package com.example.dy.group_demo6.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dy.group_demo6.R;
import com.example.dy.group_demo6.XMPPUtil;
import com.example.dy.group_demo6.data;

public class JoinActivity extends AppCompatActivity {
    private EditText et_joinname;
    private Button bt_join;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        et_joinname = (EditText) findViewById(R.id.join_name);
        bt_join = (Button) findViewById(R.id.bt_join);
        bt_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(XMPPUtil.joinMultiUserChat(data.connection,data.connection.getUser(),et_joinname.getText().toString(),5000)!=null){
                    if(XMPPUtil.addBookmarkedRoom(data.connection,et_joinname.getText().toString(),data.connection.getUser(),"")){
                        Toast.makeText(JoinActivity.this,"加入成功",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(JoinActivity.this,"加入成功,保存失败",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(JoinActivity.this,"加入失败",Toast.LENGTH_SHORT).show();
                }

            }
        });
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
