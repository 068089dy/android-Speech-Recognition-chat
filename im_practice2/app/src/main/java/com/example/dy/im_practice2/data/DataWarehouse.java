package com.example.dy.im_practice2.data;

import android.content.Context;

import org.jivesoftware.smack.XMPPConnection;

/**
 * Created by 068089dy on 2016/10/15.
 */
public class DataWarehouse {
    public static Globaldata getGlobalData(Context ctx){
        return (Globaldata)ctx.getApplicationContext();
    }
    public static XMPPConnection getXMPPConnection(Context ctx){
        return getGlobalData(ctx).xmppConnection;
    }
    public static void setXMPPConnection(Context ctx, XMPPConnection conn){
        getGlobalData(ctx).xmppConnection = conn;
    }
}
