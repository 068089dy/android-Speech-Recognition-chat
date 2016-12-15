package com.example.a068089dy.im_practice1;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.a068089dy.im_practice1.ChatActivity_listview.Msg;
import com.example.a068089dy.im_practice1.ChatActivity_listview.MsgAdapter;
import com.example.a068089dy.im_practice1.common.XMPPUtil;
import com.example.a068089dy.im_practice1.data.DataWarehouse;
import com.example.a068089dy.im_practice1.data.Logindata;
import com.example.a068089dy.im_practice1.data.XMPP_data;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;


import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private ListView msgListView;
    private EditText inputText;
    private Button send;
    private MsgAdapter adapter;
    private List<Msg> msgList = new ArrayList<Msg>();
    public Logindata mLoginData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initmsg();
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                login();
            }
        }).start();*/
        adapter = new MsgAdapter(ChatActivity.this,R.layout.msg_item,msgList);
        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);
        msgListView = (ListView) findViewById(R.id.msg_list_view);
        msgListView.setAdapter(adapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = inputText.getText().toString();
                if(!"".equals(content)){
                    Msg msg = new Msg(content,Msg.TYPE_SEND);
                    msgList.add(msg);
                    adapter.notifyDataSetChanged();//刷新listview
                    msgListView.setSelection(msgList.size());
                    inputText.setText("");
                    try {
                        sendTextMessage(XMPP_data.connection,"user1",content);
                    }catch(Exception e){

                    }

                }
            }
        });
    }


    //发送文本
    public void sendTextMessage(XMPPConnection connection,String username,String content) throws Exception{
        //ChatManager chatManager = connection.getUser();
        Chat chat = ChatManager.getInstanceFor(connection).createChat("user1@vm-124-97-ubuntu", new MessageListener() {
            public void processMessage(Chat chat, Message message) {
                // Print out any messages we get back to standard out.
                System.out.println("Received message: " + message);
                Log.d("received","message!");
            }
        });
        chat.sendMessage(content);
    }


    private void initmsg(){
        Msg msg1 = new Msg("hello1",1);
        msgList.add(msg1);
        Msg msg2 = new Msg("hello1",0);
        msgList.add(msg2);
    }
}
