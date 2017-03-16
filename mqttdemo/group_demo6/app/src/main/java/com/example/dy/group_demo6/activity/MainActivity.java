package com.example.dy.group_demo6.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.dy.group_demo6.R;
import com.example.dy.group_demo6.listview.Rooms;
import com.example.dy.group_demo6.util.XMPPUtil;
import com.example.dy.group_demo6.data.data;
import com.example.dy.group_demo6.listview.RoomsAdapter;
import com.example.dy.group_demo6.listview.chat;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;
import org.jivesoftware.smackx.packet.DelayInformation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private List<Rooms> roomlist = new ArrayList<Rooms>();
    private RoomsAdapter adapter;
    private ListView roomlistview;
    private Handler handler = new Handler();
    private SwipeRefreshLayout swipeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initview();
        initdata();
        roomlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Rooms room = roomlist.get(position);
                Intent intent = new Intent(MainActivity.this,roomActivity.class);
                intent.putExtra("roomname",room.getName());
                startActivityForResult(intent,100);
            }
        });
        initdata();

    }

    private void initdata(){
        MultiUserChat muc = new MultiUserChat(data.connection,data.connection.getUser());
        muc.addParticipantStatusListener(new ParticipantStatus());//群状态监听
        muc.addMessageListener(new OnGroupChatListener(muc));//群消息监听
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
                                    System.out.println("群名"+room.getName());
                                    System.out.println("群人数"+room.getOccupants());
                                    System.out.println("群人数"+room.getSubject());


                                    if (XMPPUtil.joinMultiUserChat(data.connection,data.connection.getUser(),room.getName(),5000)!=null) {
                                        roomlist = XMPPUtil.getBookmarkedRooms(data.connection);
                                        adapter = new RoomsAdapter(MainActivity.this, R.layout.roomitem, roomlist);
                                        roomlistview.setAdapter(adapter);
                                    }
                                }
                            }

                        }
                    }
                });
            }
        }).start();
    }

    //刷新列表
    public void onRefresh(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);

                initdata();
            }
        },0);
    }

    private void initdata1(){
        MultiUserChat muc = new MultiUserChat(data.connection,data.connection.getUser());
        muc.addParticipantStatusListener(new ParticipantStatus());//群状态监听
        muc.addMessageListener(new OnGroupChatListener(muc));//群消息监听
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
                                    System.out.println("群名"+room.getName());
                                    System.out.println("群人数"+room.getOccupants());
                                    System.out.println("群人数"+room.getSubject());



                                        adapter = new RoomsAdapter(MainActivity.this, R.layout.roomitem, roomlist);
                                        roomlistview.setAdapter(adapter);

                                }
                            }

                        }
                    }
                });
            }
        }).start();
    }

    private void initview(){
        roomlistview = (ListView) findViewById(R.id.room_listview);
        adapter = new RoomsAdapter(MainActivity.this, R.layout.roomitem, roomlist);
        roomlistview.setAdapter(adapter);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(MainActivity.this);
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_red_light);
    }




    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.create_room:
                Intent intent = new Intent(MainActivity.this,CreateRoomActivity.class);
                startActivityForResult(intent,100);
                break;
            case R.id.join_room:
                Intent intent1 = new Intent(MainActivity.this,JoinActivity.class);
                startActivityForResult(intent1,100);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        initdata1();
    }

    /**
     * 会议室状态监听事件
     *
     * @author Administrator
     *
     */
    class ParticipantStatus implements ParticipantStatusListener {

        @Override
        public void adminGranted(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void adminRevoked(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void banned(String arg0, String arg1, String arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void joined(String participant) {
            System.out.println(StringUtils.parseResource(participant)+ " has joined the room.");
        }

        @Override
        public void kicked(String arg0, String arg1, String arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void left(String participant) {
            // TODO Auto-generated method stub
            System.out.println(StringUtils.parseResource(participant)+ " has left the room.");

        }

        @Override
        public void membershipGranted(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void membershipRevoked(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void moderatorGranted(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void moderatorRevoked(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void nicknameChanged(String participant, String newNickname) {
            System.out.println(StringUtils.parseResource(participant)+ " is now known as " + newNickname + ".");
        }

        @Override
        public void ownershipGranted(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void ownershipRevoked(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void voiceGranted(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void voiceRevoked(String arg0) {
            // TODO Auto-generated method stub

        }

    }

    class OnGroupChatListener implements PacketListener {
        private MultiUserChat mChat = null;

        public OnGroupChatListener(MultiUserChat chat) {
            mChat = chat;
        }

        @Override
        public void processPacket(Packet packet) {
            Message msg = (Message) packet;
            DelayInformation inf = (DelayInformation) msg.getExtension("x",
                    "jabber:x:delay");
            if (msg.getFrom().equals(mChat.getRoom())) {
                return;
            }
            if (msg.getBody() != null) {
                if (inf == null) { // 新消息

                    System.out.println("--------------群组消息------------");
                    System.out.println("来自:" + msg.getFrom());

                    System.out.println("消息:" + msg.getBody());
                    String from1  = msg.getFrom().split("@")[1];
                    final String from = from1.split("/")[1];
                    final String message = msg.getBody();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            chat chat1 = new chat(from,message);

                        }
                    });

                } else {
                    //旧消息，该房间的所有消息
                }
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            data.connection.disconnect();

            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
