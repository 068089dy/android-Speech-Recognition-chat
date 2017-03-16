package com.example.dy.group_demo6.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.dy.group_demo6.R;
import com.example.dy.group_demo6.data.data;
import com.example.dy.group_demo6.listview.Rooms;
import com.example.dy.group_demo6.listview.RoomsAdapter;
import com.example.dy.group_demo6.util.XMPPUtil;

import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class roomActivity extends AppCompatActivity  implements View.OnClickListener{
    private TextView tv_name;
    private TextView tv_jid;
    private TextView tv_occ;
    private TextView tv_description;
    private Button btn_device;
    private Button btn_chat;
    String roomname;
    private Handler handler = new Handler();
    List<Rooms> roomlist = new ArrayList<Rooms>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        initview();
        Intent intent = getIntent();
        roomname = intent.getStringExtra("roomname");
        initdata();

    }
    private void initview(){
        tv_description = (TextView) findViewById(R.id.tv_description);
        tv_occ = (TextView) findViewById(R.id.tv_occ);
        tv_jid = (TextView) findViewById(R.id.tv_jid);
        tv_name = (TextView) findViewById(R.id.tv_name);

        btn_chat = (Button) findViewById(R.id.chat);
        btn_device = (Button) findViewById(R.id.device);
        btn_device.setOnClickListener(this);
        btn_chat.setOnClickListener(this);
    }
    public void onClick(View v){
        switch(v.getId()){
            case R.id.chat:
                Intent intent1 = new Intent(roomActivity.this,chatActivity.class);
                intent1.putExtra("roomname",roomname);
                startActivity(intent1);
                break;
            case R.id.device:
                Intent intent2 = new Intent(roomActivity.this,deviceActivity.class);
                intent2.putExtra("roomname",roomname);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }
    private void initdata(){
        /*
        MultiUserChat muc = new MultiUserChat(data.connection,data.connection.getUser());
        muc.addParticipantStatusListener(new MainActivity.ParticipantStatus());//群状态监听
        muc.addMessageListener(new MainActivity.OnGroupChatListener(muc));//群消息监听
        */
        new Thread(new Runnable() {
            @Override
            public void run() {
                roomlist = XMPPUtil.getBookmarkedRooms(data.connection);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(roomlist!=null) {
                            System.out.println("开始listview");
                            if(roomlist!=null){
                                Iterator<Rooms> it = roomlist.iterator();
                                while(it.hasNext()){
                                    Rooms room = it.next();
                                    if(room.getName().equals(roomname)) {
                                        tv_name.setText("房间名："+room.getName());
                                        System.out.println("群名" + room.getName());
                                        tv_occ.setText("当前房间人数："+Integer.toString(room.getOccupants()));
                                        System.out.println("群人数" + room.getOccupants());

                                        System.out.println("群主题" + room.getSubject());
                                        tv_description.setText("房间描述："+room.getDescription());
                                        System.out.println("群描述" + room.getDescription());
                                        tv_jid.setText("房间jid："+room.getJid());
                                        System.out.println("群Jid" + room.getJid());


                                    }

                                    if (XMPPUtil.joinMultiUserChat(data.connection,data.connection.getUser(),room.getName(),5000)!=null) {

                                    }
                                }
                            }

                        }
                    }
                });
            }
        }).start();
    }
}
