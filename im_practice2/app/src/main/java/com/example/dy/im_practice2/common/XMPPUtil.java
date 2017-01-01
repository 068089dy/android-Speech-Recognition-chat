package com.example.dy.im_practice2.common;


import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;

/**
 * Created by 068089dy on 2016/10/15.
 */
public class XMPPUtil {
    public static XMPPConnection getXMPPConnection(String server, int port){
        try{
            ConnectionConfiguration config = new ConnectionConfiguration(server,port);//类 俩参数
            /** 是否启用安全验证 */
            config.setSASLAuthenticationEnabled(false);
            /** 是否启用调试 */
            //config.setDebuggerEnabled(true);
            /*
            config.setReconnectionAllowed(true);//允许重新连接
            config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);//禁止安全模式
            config.setSendPresence(true);//已连接标志

            SASLAuthentication.supportSASLMechanism("PLAIN",0);//设置验证方式
            */
            XMPPConnection connection = new XMPPConnection(config,null);//子类
            connection.connect();
            return connection;
        }
        catch(Exception e){

        }
        return null;
    }
    public static XMPPConnection getXMPPConnection(String server){
        return getXMPPConnection(server,5222);
    }
}
