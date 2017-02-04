package com.example.dy.im_practice2;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Handler;

import com.example.dy.im_practice2.listview.ChatActivity_listview.Msg;
import com.example.dy.im_practice2.listview.ChatActivity_listview.MsgAdapter;
import com.example.dy.im_practice2.data.XMPP_data;

import com.example.dy.im_practice2.receiver.LoccalReceiver;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
//import org.jivesoftware.smack.packet.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import java.util.List;



public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView msgListView;
    private EditText inputText;
    private Button send;
    private ImageView leftBtn;
    private MsgAdapter adapter;
    private List<Msg> msgList = new ArrayList<Msg>();
    private IntentFilter intentFilter;
    private LoccalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private String username;
    private OnRefresh filerefresh = new OnRefresh();
    private Boolean activity_isAlive = true;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            Bundle b = msg.getData();
            String data = b.getString("data");
            Log.d("hand_msg","addstart");
            if(data == null || data == "") {

            }else{
                Log.d("hand_msg","addstart");
                Msg mssg = new Msg(data, Msg.TYPE_RECEIVED);
                msgList.add(mssg);
                adapter.notifyDataSetChanged();//刷新listview
                msgListView.setSelection(msgList.size());
                //save_message(username,null);//clear message_file
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//取消最上面那个栏
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);


        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder context = new StringBuilder();
        String line1 = null;
        try{
            in = openFileInput(username);
            reader = new BufferedReader(new InputStreamReader(in));
            line1 = reader.readLine();
            Log.d("line1",line1);
        }catch(Exception e){

        }finally{
            if(reader != null){
                try{
                    reader.close();
                }catch(Exception e){
                    try{
                        reader.close();
                    }catch(IOException e1){
                        e1.printStackTrace();
                    }
                }
            }
        }
        filerefresh.start();
        initview();



    }
    public void initview(){
        adapter = new MsgAdapter(ChatActivity.this, R.layout.msg_item, msgList);
        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);
        leftBtn = (ImageView) findViewById(R.id.leftBtn);
        msgListView = (ListView) findViewById(R.id.msg_list_view);
        msgListView.setAdapter(adapter);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        Log.d("username",username);
        intentFilter = new IntentFilter();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter.addAction("new.message");
        localReceiver = new LoccalReceiver();
        localBroadcastManager.registerReceiver(localReceiver,intentFilter);

        send.setOnClickListener(this);
        leftBtn.setOnClickListener(this);
    }

    //发送文本
    public void sendTextMessage(XMPPConnection connection, String input, String username) throws Exception{
        //ChatManager chatManager = connection.getUser();
        Chat chat = connection.getChatManager().createChat(username+"@"+XMPP_data.connection.getServiceName(),null);
        chat.sendMessage(input);
    }



    public String readLine(){
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder context = new StringBuilder();
        String line1 = null;
        try{
            in = openFileInput(username);
            reader = new BufferedReader(new InputStreamReader(in));
            line1 = reader.readLine();
            Log.d("line1",line1);
            String line = "";
            while((line = reader.readLine())!=null){
                context.append(line);
            }
            save_message(username,context.toString());
            Log.d("save_message",context.toString());
        }catch(Exception e){

        }finally{
            if(reader != null){
                try{
                    reader.close();
                }catch(Exception e){
                    try{
                        reader.close();
                    }catch(IOException e1){
                        e1.printStackTrace();
                    }
                }
            }
        }
        return line1;
    }

    public void save_message(String filename,String data){
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try{
            out = openFileOutput(filename,MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(data);
        }catch(Exception e){

        }finally {
            try{
                if (writer != null){
                    writer.close();
                }
            }catch(Exception e){
            }
        }
    }

    class OnRefresh extends Thread {
        public void run() {
            try {
                while(true) {
                    if(activity_isAlive){

                    }else{
                        break;
                    }
                    String data = readLine();

                    if(data == null || data == "") {

                    }else{
                        Log.d("thread_msg","send");
                        Message msg = handler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("data", data);
                        msg.setData(b);
                        handler.sendMessage(msg); // 向Handler发送消息,更新UI
                        //save_message(username,null);//clear message_file
                    }
                }
            }catch(Exception e){

            }
        }
    }


    public void onClick(View v){
        switch(v.getId()){
            case R.id.send:
                String content = inputText.getText().toString();
                if (!"".equals(content)) {
                    try {
                        Intent intent = getIntent();
                        username = intent.getStringExtra("username");
                        sendTextMessage(XMPP_data.connection, content, username);
                        Log.d("send to",username+XMPP_data.connection.getServiceName());
                        Msg msg = new Msg(content, Msg.TYPE_SEND);
                        msgList.add(msg);
                        adapter.notifyDataSetChanged();//刷新listview
                        msgListView.setSelection(msgList.size());
                        inputText.setText("");
                    } catch (Exception e) {
                        Toast.makeText(ChatActivity.this, "send error!", Toast.LENGTH_LONG).show();
                    }

                }
                break;
            case R.id.leftBtn:
                activity_isAlive = false;
                finish();
                break;
            default:
                break;

        }
    }
    public void onDestroy(){
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            activity_isAlive = false;

            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


}
