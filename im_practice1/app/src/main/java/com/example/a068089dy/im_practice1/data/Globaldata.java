package com.example.a068089dy.im_practice1.data;

import android.app.Application;
import org.jivesoftware.smack.XMPPConnection;

/**
 * Created by 068089dy on 2016/10/15.
 */
public class Globaldata extends Application {
    public XMPPConnection xmppConnection;
    public Logindata logindata = new Logindata();
}
