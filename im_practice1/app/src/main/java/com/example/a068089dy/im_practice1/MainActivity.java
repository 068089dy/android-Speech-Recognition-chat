package com.example.a068089dy.im_practice1;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;

import android.widget.ListView;
import android.widget.Toast;

import com.example.a068089dy.im_practice1.MainActivity_listview.friend;
import com.example.a068089dy.im_practice1.MainActivity_listview.friendAdapter;
import com.example.a068089dy.im_practice1.data.XMPP_data;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private List<friend> friendList = new ArrayList<friend>();
    private SwipeRefreshLayout swipeLayout;
    private String username;
    private String loginserver;
    private String servername;
    private String chat_user;
    private friendAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//取消最上面那个栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initdata();
        adapter = new friendAdapter(MainActivity.this,R.layout.friend_item,friendList);
        ListView friendListView = (ListView) findViewById(R.id.friend_listview);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(MainActivity.this);
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_red_light);




        friendListView.setAdapter(adapter);
        //注册监听器
        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view,int position,long id){
                friend frid = friendList.get(position);
                Toast.makeText(MainActivity.this,frid.getName(),Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this,ChatActivity.class);
                intent.putExtra("username",frid.getName());
                startActivity(intent);
            }
        });
    }

    public void onRefresh(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
                //friendList.addAll(friendList);

                adapter.notifyDataSetChanged();
                initdata();
            }
        },0);
    }

    private void initdata(){


        Roster roster = XMPP_data.connection.getRoster();
        Collection<RosterGroup> entriesGroup = roster.getGroups();

        for(RosterGroup group: entriesGroup){
            Collection<RosterEntry> entries = group.getEntries();
            Log.d("group", group.getName());
            for (RosterEntry entry : entries) {

                Log.d("name:",entry.getName());
                friend user = new friend(entry.getName(),R.drawable.user1);
                friendList.add(user);
                Iterator<friend> it = friendList.iterator();
                if (friendList.size() == 0){
                    friendList.add(user);
                    while(it.hasNext()){

                        if(!entry.getName().equals(it.next().getName())){
                            friendList.add(user);
                        }

                    }
                }


            }
        }
    }
}