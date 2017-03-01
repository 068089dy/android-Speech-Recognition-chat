package com.example.dy.group_demo6.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.example.dy.group_demo6.R;
import com.example.dy.group_demo6.XMPPUtil;
import com.example.dy.group_demo6.data;

import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.Iterator;
import java.util.List;

public class roomUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_user);
        Intent intent = getIntent();
        final String roomname = intent.getStringExtra("roomname");
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> list = XMPPUtil.getMultiUsers(data.connection,roomname);
                if (list!=null) {
                    Iterator<String> it = list.iterator();


                    while (it.hasNext()) {
                        System.out.println(roomname + "群成员：");
                        System.out.println(it.next());
                    }
                }else{
                    System.out.println("qunweikong");
                }

            }
        }).start();

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
