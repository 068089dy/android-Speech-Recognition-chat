package com.example.dy.im_practice2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.example.dy.im_practice2.ChatActivity_listview.Msg;
import com.example.dy.im_practice2.MainActivity_listview.friend;
import com.example.dy.im_practice2.MainActivity_listview.friendAdapter;
import com.example.dy.im_practice2.data.XMPP_data;


import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.packet.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;



public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private List<friend> friendList = new ArrayList<friend>();
    private SwipeRefreshLayout swipeLayout;
    private friendAdapter adapter;
    Intent intent;
    private LocalBroadcastManager localBroadcastManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//取消最上面那个栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initdata();
        adapter = new friendAdapter(MainActivity.this,R.layout.friend_item,friendList);
        ListView friendListView = (ListView) findViewById(R.id.friend_listview);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(MainActivity.this);
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_dark,
                    android.R.color.holo_blue_light,
                    android.R.color.holo_green_light,
                    android.R.color.holo_red_light);

        friendListView.setAdapter(adapter);
        //注册监听器
        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
           public void onItemClick(AdapterView<?> parent, View view, int position, long id){
               friend frid = friendList.get(position);
               //Toast.makeText(MainActivity.this,frid.getName(), Toast.LENGTH_LONG).show();
               intent = new Intent(MainActivity.this,ChatActivity.class);
               intent.putExtra("username",frid.getName());
               Log.d("chat","start");
               startActivity(intent);
           }
        });

        TaxiChatManagerListener chatManagerListener = new TaxiChatManagerListener();
        XMPP_data.connection.getChatManager().addChatListener(chatManagerListener);
    }

    public void onRefresh(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
                //friendList.addAll(friendList);
                friendList.clear();
                adapter.notifyDataSetChanged();
                initdata();
            }
        },0);
    }

    class TaxiChatManagerListener implements ChatManagerListener {

        public void chatCreated(Chat chat, boolean arg1) {
            chat.addMessageListener(new MessageListener() {
                public void processMessage(Chat arg0, Message msg) {

                    //发送消息用户
                    msg.getFrom();
                    //消息内容
                    String from = msg.getFrom();
                    String body = msg.getBody();
                    intent.putExtra("have_msg",true);

                    Intent intent = new Intent("new.message");
                    localBroadcastManager.sendBroadcast(intent);
                    String[] user_name = from.split("@");
                    save_message(user_name[0],body);
                    Log.d(user_name[0],"save in "+body);



                }
            });
        }
    }
    private void initdata(){
        Roster roster = XMPP_data.connection.getRoster();
        Collection<RosterGroup> entriesGroup = roster.getGroups();

        for(RosterGroup group: entriesGroup){
            Collection<RosterEntry> entries = group.getEntries();
            Log.d("group", group.getName());
            for (RosterEntry entry : entries) {

                Log.d("name:",entry.getName());
                friend user = new friend(entry.getName(),R.drawable.head_logo);
                friendList.add(user);
                //msg_map.put(user.getName(),null);
                Iterator<friend> it = friendList.iterator();
                if (friendList.size() == 0){
                    friendList.add(user);
                    while(it.hasNext()){

                        if(!entry.getName().equals(it.next().getName())){
                            friendList.add(user);
                            //msg_map.put(user.getName(),null);
                        }

                    }
                }
            }
        }
    }
    public void save_message(String filename,String data){

        FileOutputStream out = null;
        BufferedWriter writer = null;
        try{
            String str = read();
            out = openFileOutput(filename,MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            Log.d("writer",str+"\n"+data);
            writer.write(str+"\n"+data);
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
    public String read(){
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder context = new StringBuilder();
        try{
            in = openFileInput("user1");
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_friend) {
            Toast.makeText(MainActivity.this,"action",Toast.LENGTH_LONG).show();
        }
        switch(id){
            case R.id.add_friend:
                break;
            case R.id.clear_list:
                break;
            case R.id.remove_friend:
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
