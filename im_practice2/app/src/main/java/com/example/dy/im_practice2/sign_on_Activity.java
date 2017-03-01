package com.example.dy.im_practice2;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dy.im_practice2.data.XMPP_data;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.VCard;
/*

*/

public class sign_on_Activity extends AppCompatActivity implements View.OnClickListener {
    private EditText edit_user;
    private EditText edit_passwd;
    private Button btn_signon;
    private ImageView btn_back;



    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            Bundle b = msg.getData();
            String data = b.getString("data");
            Toast.makeText(sign_on_Activity.this,data,Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_on_);
        initdata();
    }
    private void initdata(){
        edit_user = (EditText) findViewById(R.id.username);
        edit_passwd = (EditText) findViewById(R.id.password);
        btn_signon = (Button) findViewById(R.id.btn_signon);
        btn_back = (ImageView) findViewById(R.id.leftBtn);
        btn_signon.setOnClickListener(this);
        btn_back.setOnClickListener(this);
    }
    public void onClick(View v){
        switch(v.getId()){
            case R.id.btn_signon:
                final String username = edit_user.getText().toString();
                final String password = edit_passwd.getText().toString();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ConnectionConfiguration connConfig = new ConnectionConfiguration("123.207.174.226",5222);
                        XMPPConnection con = new XMPPConnection(connConfig);
                        try{

                            con.connect();
                            if(createAccount(con,username,password)){

                                Message msg = handler.obtainMessage();
                                Bundle b = new Bundle();
                                b.putString("data","sign on success!");
                                msg.setData(b);
                                handler.sendMessage(msg);
                                con.disconnect();
                            }else{


                                Message msg = handler.obtainMessage();
                                Bundle b = new Bundle();
                                b.putString("data","sign on faield!");
                                msg.setData(b);
                                handler.sendMessage(msg);
                                con.disconnect();
                            }
                        }catch(Exception e){
                            Log.d("sign on error1",e.toString());

                            Message msg = handler.obtainMessage();
                            Bundle b = new Bundle();
                            b.putString("data","sign on faield!");
                            msg.setData(b);
                            handler.sendMessage(msg);
                            con.disconnect();
                        }
                    }
                }).start();

                break;
            case R.id.leftBtn:
                Intent intent = new Intent(sign_on_Activity.this,loginactivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
    //注册用户
    public static boolean createAccount(XMPPConnection connection,final String regUserName,final String regUserPwd)
    {
        try {
            connection.getAccountManager().createAccount(regUserName, regUserPwd);
            return true;
        } catch (Exception e) {
            Log.d("sign on error",e.toString());
            return false;
        }
    }
    /**
     * 上传头像
     */
    private void setUserImage(final XMPPConnection connection, final byte[] image) throws XMPPException {
        final VCard card = new VCard();
        card.load(connection);

        new Thread() {
            @Override
            public void run() {
                try {
                    PacketFilter filter = new AndFilter(new PacketIDFilter(card.getPacketID()), new PacketTypeFilter(IQ.class));
                    PacketCollector collector = connection.createPacketCollector(filter);
                    String encodeImage = StringUtils.encodeBase64(image);
                    card.setAvatar(image, encodeImage);
                    //card.setEncodedImage(encodeImage);
                    card.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>" + encodeImage + "</BINVAL>", true);
                    Log.i("other", "上传头像的方法！");
                    card.save(connection);
                    IQ iq = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
                    if (iq != null && iq.getType() == IQ.Type.RESULT) {
                        Message msg = handler.obtainMessage();
                        msg.what = 2;
                        handler.sendMessage(msg);
                    }
                } catch (XMPPException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
