package com.example.dy.im_practice2.data;

import android.app.Application;

import org.jivesoftware.smack.XMPPConnection;

/**
 * Created by 068089dy on 2016/10/15.
 */
public class Globaldata extends Application {
    public XMPPConnection xmppConnection;
    public Logindata logindata = new Logindata();
}
