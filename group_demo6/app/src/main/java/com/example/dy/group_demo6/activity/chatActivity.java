package com.example.dy.group_demo6.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dy.group_demo6.R;
import com.example.dy.group_demo6.XMPPUtil;
import com.example.dy.group_demo6.data;
import com.example.dy.group_demo6.listview.chat;
import com.example.dy.group_demo6.listview.chatAdapter;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.DelayInformation;

import java.util.ArrayList;
import java.util.List;

public class chatActivity extends AppCompatActivity {
    private chatAdapter adapter;
    private ListView msg_listview;
    private List<chat> msg_list = new ArrayList<chat>();
    private String roomname;
    private EditText et_message;
    private Button bt_send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initview();
        Intent intent = getIntent();
        roomname = intent.getStringExtra("roomname");
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message;
                if(!(message = et_message.getText().toString()).equals("")) {
                    try {
                        XMPPUtil.sendRoomMessage(data.connection, roomname, message);
                        et_message.setText("");

                    } catch (Exception e) {
                        Toast.makeText(chatActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        try {
            MultiUserChat muc = new MultiUserChat(data.connection,roomname+"@conference."+ data.connection.getServiceName());
            OnGroupChatListener ol = new OnGroupChatListener(muc);
            muc.addMessageListener(ol);
        }catch(Exception e){

        }
    }
    private Handler handler = new Handler();
    private void initview(){
        msg_listview = (ListView) findViewById(R.id.msg_listview);
        et_message = (EditText) findViewById(R.id.et_message);
        bt_send = (Button) findViewById(R.id.bt_send);
        adapter = new chatAdapter(chatActivity.this,R.layout.chatitem,msg_list);
        msg_listview.setAdapter(adapter);
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
                            msg_list.add(chat1);
                            adapter.notifyDataSetChanged();
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

            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.chat_menu,menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.lookuser:
                Intent intent = new Intent(chatActivity.this,roomUserActivity.class);
                intent.putExtra("roomname",roomname);
                startActivityForResult(intent,100);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    protected void onActivityResult(int requestCode,int resultCode,Intent data){

    }
}
