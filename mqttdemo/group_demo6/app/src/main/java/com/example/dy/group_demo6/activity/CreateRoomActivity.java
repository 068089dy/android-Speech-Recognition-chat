package com.example.dy.group_demo6.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dy.group_demo6.R;
import com.example.dy.group_demo6.util.XMPPUtil;
import com.example.dy.group_demo6.data.data;

public class CreateRoomActivity extends AppCompatActivity {
    private EditText et_roomname;
    private Button bt_createroom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        initview();
        //user1SmackAndroid.init(this);
        bt_createroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if(XMPPUtil.creConferenceRoom(data.connection,et_roomname.getText().toString())){
                            if (XMPPUtil.addBookmarkedRoom(data.connection,et_roomname.getText().toString(),data.connection.getUser(),"")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(CreateRoomActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(CreateRoomActivity.this, "创建成功,保存失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }else{
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CreateRoomActivity.this, "创建失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();

            }
        });
    }

    private Handler handler = new Handler();

    private void initview(){
        et_roomname = (EditText) findViewById(R.id.et_roomname);
        bt_createroom = (Button) findViewById(R.id.bt_createroom);
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
