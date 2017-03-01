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
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import com.example.dy.im_practice2.listview.ChatActivity_listview.Msg;
import com.example.dy.im_practice2.listview.ChatActivity_listview.MsgAdapter;
import com.example.dy.im_practice2.data.XMPP_data;

import com.example.dy.im_practice2.receiver.LoccalReceiver;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.parsing.ExceptionLoggingCallback;
import org.json.JSONException;
import org.json.JSONObject;
//import org.jivesoftware.smack.packet.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView msgListView;
    private EditText inputText;
    private TextView tx_title;
    private Button send;
    private ImageView leftBtn;
    private MsgAdapter adapter;
    private List<Msg> msgList = new LinkedList<Msg>();
    private IntentFilter intentFilter;
    private LoccalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private String username;
    private OnRefresh filerefresh = new OnRefresh();
    private Boolean activity_isAlive = true;
    private String filecontent = "";

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            Bundle b = msg.getData();
            String data = b.getString("data");
            Log.d("hand_msg","addstart");
            if(data == null || data.equals("")) {

            }else{
                msgList.clear();
                Map map = new HashMap();
                try {
                    map = toMap(data);
                }catch(Exception e){

                }
                /*
                Iterator<Map.Entry<String,String>> it = map.entrySet().iterator();

                while(it.hasNext()){
                    Map.Entry entry = it.next();
                        //如果是对方的消息
                        if (entry.getKey().toString().startsWith("y")) {
                            Msg mssg = new Msg(entry.getValue().toString(), Msg.TYPE_RECEIVED);
                            msgList.add(mssg);
                            adapter.notifyDataSetChanged();//刷新listview
                            msgListView.setSelection(msgList.size());
                        } else {
                            Msg mssg = new Msg(entry.getValue().toString(), Msg.TYPE_SEND);
                            msgList.add(mssg);
                            adapter.notifyDataSetChanged();//刷新listview
                            msgListView.setSelection(msgList.size());
                        }
                }
                */
                Iterator<Map.Entry<String,String>> it = map.entrySet().iterator();
                int max_int_keynum = 0;
                while(it.hasNext()){
                    Map.Entry entry = it.next();

                    String key = entry.getKey().toString();

                    String keynum = key.substring(1);

                    int int_keynum = Integer.parseInt(keynum);
                    if(max_int_keynum<int_keynum){
                        max_int_keynum = int_keynum;
                        System.out.println("max_int_keynum"+max_int_keynum);
                    }
                }

                for(int i = 0;i<max_int_keynum;i++) {
                    Iterator<Map.Entry<String,String>> it1 = map.entrySet().iterator();
                    while (it1.hasNext()) {
                        Map.Entry entry = it1.next();
                        String key_num = entry.getKey().toString().substring(1);
                        int ikey_num = Integer.parseInt(key_num);
                        if (ikey_num == i+1) {

                            //如果是对方的消息
                            if (entry.getKey().toString().startsWith("y")) {
                                Msg mssg = new Msg(entry.getValue().toString(), Msg.TYPE_RECEIVED);
                                msgList.add(mssg);
                                adapter.notifyDataSetChanged();//刷新listview
                                msgListView.setSelection(msgList.size());

                            } else {
                                Msg mssg = new Msg(entry.getValue().toString(), Msg.TYPE_SEND);
                                msgList.add(mssg);
                                adapter.notifyDataSetChanged();//刷新listview
                                msgListView.setSelection(msgList.size());

                            }
                        break;
                        }
                    }
                }

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
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        filerefresh.start();
        /*

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
        */

        initview();



    }
    public void initview(){
        adapter = new MsgAdapter(ChatActivity.this, R.layout.msg_item, msgList);
        inputText = (EditText) findViewById(R.id.input_text);
        tx_title = (TextView) findViewById(R.id.titleView);
        tx_title.setText(username);
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


    /*
    * 读取一行返回并抹掉这行
    * */
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
    /*
    * 读取filename文件,并返回
    * */
    public String read(String filename){
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder context = new StringBuilder();
        try{
            in = openFileInput(filename);
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while((line = reader.readLine())!=null){
                context.append(line);

            }
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
        return context.toString();
    }
    /*
    * 将data写入filaname
    * */
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
    /*
    * 将json字符串转化为map
    * */
    public Map toMap(String jsonString) throws JSONException {

        JSONObject jsonObject = new JSONObject(jsonString);

        Map result = new HashMap();
        Iterator iterator = jsonObject.keys();
        String key = null;
        String value = null;

        while (iterator.hasNext()) {

            key = (String) iterator.next();
            value = jsonObject.getString(key);
            result.put(key, value);
            //System.out.println("key:"+key+" "+"value"+value);

        }
        return result;

    }
    /*
    * 将map妆化为json字符串
    * */
    public String tojsonstr(Map map){
        //Map result = new HashMap();
        Iterator<Map.Entry<String,String>> iterator = map.entrySet().iterator();
        String key = null;
        String value = null;
        String jsonstr = null;
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            key = entry.getKey().toString();
            value = map.get(key).toString();
            //result.put(key, value);
            System.out.println("key:"+key+" "+"value:"+value);
            String jsonstr1 = "\""+key+"\""+":"+"\""+value+"\"";
            if(jsonstr==null) {
                jsonstr = jsonstr1;
            }else{
                jsonstr = jsonstr + "," + jsonstr1;
            }
        }
        jsonstr = "{"+jsonstr+"}";
        System.out.println(jsonstr);
        return jsonstr;
    }

    class OnRefresh extends Thread {
        public void run() {
            //Log.d("onrefresh","start");
            try {
                while(true) {
                    if(activity_isAlive){

                    }else{
                        break;
                    }
                    String data = read(XMPP_data.my_username+username);
                    //System.out.println("onre data:"+data);
                    //System.out.println("filename:"+filecontent);
                    if(!filecontent.equals(data)) {
                        filecontent = data;
                        Message msg = handler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("data", data);
                        msg.setData(b);
                        handler.sendMessage(msg); // 向Handler发送消息,更新UI
                        Log.d("send to handler",data);
                    }
                    /*
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
                    */
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

                        sendTextMessage(XMPP_data.connection, content, username);
                        Log.d("send to",username+XMPP_data.connection.getServiceName());
                        /*
                        Msg msg = new Msg(content, Msg.TYPE_SEND);
                        msgList.add(msg);
                        adapter.notifyDataSetChanged();//刷新listview
                        msgListView.setSelection(msgList.size());
                        */
                        new_msg_save(XMPP_data.my_username+username,content);
                        inputText.setText("");
                    } catch (Exception e) {
                        Log.d("senderror",e.toString());
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

    /*
    * 新消息存储(我的)
    * 读取username文件，将其转化为map，将受到的消息添加到map,然后转化为json，存入文件
    * */
    private void new_msg_save(String username,String body){

        String jsonstr = read(username);
        Map map = new HashMap();

        try {
            map = toMap(jsonstr);
        }catch (Exception e){
            Log.d("tomaperror",e.toString());
        }

        Iterator<Map.Entry<String,String>> it = map.entrySet().iterator();

        int max_int_keynum = 0;
        while(it.hasNext()){

            Map.Entry entry = it.next();

            String key = entry.getKey().toString();

            String keynum = key.substring(1);

            int int_keynum = Integer.parseInt(keynum);
            if(max_int_keynum<int_keynum){
                max_int_keynum = int_keynum;
            }

        }

        String key = Integer.toString(max_int_keynum+1);
        Log.d("int_keynum",key);
        map.put("m"+key,body);


        save_message(username,tojsonstr(map));
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
