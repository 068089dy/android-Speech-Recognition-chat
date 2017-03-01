package com.example.dy.im_practice2;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.example.dy.im_practice2.listview.Add_friendActivity_listview.Add;
import com.example.dy.im_practice2.listview.Add_friendActivity_listview.Add_adapter;

import com.example.dy.im_practice2.data.*;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.workgroup.packet.QueueUpdate;
import org.jivesoftware.smackx.workgroup.packet.SessionID;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.example.dy.im_practice2.data.XMPP_data.connection;

public class Add_friendActivity extends AppCompatActivity {
    private EditText edit_search;
    private Button btn_search;
    private ListView lv_search;

    private List<Add> addList = new ArrayList<Add>();
    private Add_adapter adapter;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            Bundle b = msg.getData();
            String data = b.getString("user");
            Add add1 = new Add(R.drawable.head_logo,data);
            addList.add(add1);
            adapter.notifyDataSetChanged();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        initview();
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addList.clear();
                final String user = edit_search.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        search_user(user);
                    }
                }).start();
            }
        });

    }
    private void initview(){
        edit_search = (EditText) findViewById(R.id.edit_search);
        btn_search = (Button) findViewById(R.id.btn_search);
        lv_search = (ListView) findViewById(R.id.lv_search);

        adapter = new Add_adapter(this,R.layout.add_item,addList);
        lv_search.setAdapter(adapter);

        lv_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Add add = addList.get(position);
                Log.d("add"+add.getUser(),"start");
                addfriend(add.getUser()+"@"+XMPP_data.connection.getServiceName());

            }
        });
    }

    private void search_user(String username) {

        ProviderManager.getInstance().addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
        String searchService = "search."+ connection.getServiceName();
        Log.d("server", connection.getServiceName());
        try{
            UserSearchManager search = new UserSearchManager(connection);

            Form searchForm = search.getSearchForm(searchService);

            Form answerForm = searchForm.createAnswerForm();

            answerForm.setAnswer("Username", true);

            answerForm.setAnswer("search",username.trim());

            ReportedData data = search.getSearchResults(answerForm,searchService);

            Iterator<ReportedData.Row> it = data.getRows();
            ReportedData.Row row=null;
            String ansS="";
            while(it.hasNext()){
                row=it.next();

                ansS=row.getValues("Username").next().toString();
                //String group = row.getValues("Group").next().toString();
                //Log.d(group,ansS);
                if(!ansS.equals(XMPP_data.my_username)) {
                    Message message = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("user", ansS);
                    message.setData(b);
                    handler.sendMessage(message);
                }
            }
            //Toast.makeText(this,ansS, Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            Log.d("search error:",e.toString());
        }
    }

    private void addfriend(String userJId){
        Presence p = new Presence(Presence.Type.subscribe);
        p.setTo(userJId);
        try{
            XMPP_data.connection.sendPacket(p);
            Toast.makeText(Add_friendActivity.this,"向"+userJId+"发送好友请求成功！",Toast.LENGTH_LONG).show();
            Roster roster1 = XMPP_data.connection.getRoster();
            roster1.createEntry(userJId, userJId.split("@")[0], new String[]{"test1"});//发送添加好友请求，并将该jid用户加入到自己的roster中,组名为”test1“
            System.out.println("add friend ok");

        }catch(Exception e){
            Log.d("add error",e.toString());
        }
    }
}

