package com.example.dy.group_demo6;

import android.util.Log;

import com.example.dy.group_demo6.listview.Rooms;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.bookmark.BookmarkManager;
import org.jivesoftware.smackx.bookmark.BookmarkedConference;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.DelayInformation;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dy on 17-2-19.
 */

public class XMPPUtil {

    /*
    * 获取连接
    * */
    public static XMPPConnection getXMPPConnection(String server, int port){
        try{
            ConnectionConfiguration config = new ConnectionConfiguration(server,port);//类 俩参数
            /** 是否启用安全验证 */
            config.setSASLAuthenticationEnabled(false);
            configure(ProviderManager.getInstance());
            /** 是否启用调试 */

            //config.setDebuggerEnabled(true);


            /*
            config.setReconnectionAllowed(true);//允许重新连接
            config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);//禁止安全模式
            config.setSendPresence(true);//已连接标志

            SASLAuthentication.supportSASLMechanism("PLAIN",0);//设置验证方式
            */
            //设置状态为离线
            //config.setSendPresence(false);
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

    /*
    * 创建群组
    *
    * */
    public static boolean creConferenceRoom(XMPPConnection mConnection,String roomName) {

        if (mConnection == null || !mConnection.isConnected()) {
            System.out.println("连接异常...");

            return false;
        }
        /**
         * 判断是否存在同名讨论组
         */
        if (isChatRoom(mConnection,roomName)) {
            System.out.println("创建聊天组存在同名组");
            return false;
        }



        MultiUserChat mMultiUserChat = null;
        String roomService = roomName + "@conference."
                + mConnection.getServiceName();// 房间域名

        try {
            mMultiUserChat = new MultiUserChat(mConnection, roomService);
            // 创建聊天室
            mMultiUserChat.create(roomName);

            /** 聊天室配置表单 */

            Form form = mMultiUserChat.getConfigurationForm(); // 获取配置表单
            Form submitForm = form.createAnswerForm();// 生成提交表单

            // 向提交表单添加默认答复
            for (Iterator<FormField> fields = form.getFields(); fields
                    .hasNext();) {

                FormField mFormField = (FormField) fields.next();
                if (!FormField.TYPE_HIDDEN.equals(mFormField.getType())
                        && mFormField.getVariable() != null) {
                    submitForm.setDefaultAnswer(mFormField.getVariable()); // 设置默认值作为答复
                }
            }

            // 设置聊天室的新拥有者
            List<String> owners = new ArrayList<String>();
            owners.add(mConnection.getUser());//添加自己的jid
            submitForm.setAnswer("muc#roomconfig_roomowners", owners);



            // 这里设置聊天室为公共聊天室
            submitForm.setAnswer("muc#roomconfig_publicroom", true);

            // 这里设置聊天室是永久存在的
            submitForm.setAnswer("muc#roomconfig_persistentroom", true);

            // 房间仅对成员开放
            submitForm.setAnswer("muc#roomconfig_membersonly", false);

            // 允许占有者邀请其他人
            submitForm.setAnswer("muc#roomconfig_allowinvites", true);

            // 进入是否需要密码
            //submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);

            // 设置进入密码
            //submitForm.setAnswer("muc#roomconfig_roomsecret", "password");

            // 能够发现占有者真实 JID 的角色
            // submitForm.setAnswer("muc#roomconfig_whois", "anyone");

            // 登录房间对话
            submitForm.setAnswer("muc#roomconfig_enablelogging", true);

            // 仅允许注册的昵称登录
            submitForm.setAnswer("x-muc#roomconfig_reservednick", true);

            // 允许使用者修改昵称
            submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);

            // 允许用户注册房间
            submitForm.setAnswer("x-muc#roomconfig_registration", false);

            //
            //submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);

            // 发送已完成的表单（有默认值）到服务器来配置聊天室
            mMultiUserChat.sendConfigurationForm(submitForm);
        } catch (Exception e) {
            System.out.println("创建群："+e);
            return false;
        }
        return true;
    }

    /**
     * 群是否存在
     * @param connection
     * @param name 房间的名称
     * @return
     */
    public static boolean isChatRoom(XMPPConnection connection,String roomname){
        String jid = roomname + "@conference." + connection.getServiceName();
        MultiUserChat muc = new MultiUserChat(connection,jid);
        try {
            Collection<HostedRoom> MultiUserChatManager = muc.getHostedRooms(connection,jid);
            if(MultiUserChatManager!=null){
                for (HostedRoom item: MultiUserChatManager){
                    if(item.getJid().equals(jid)){
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 查询会议室成员名字
     *
     */
    public static List<String> getMultiUsers(XMPPConnection connection,String roomName) {

        List<String> mUserList = new ArrayList<String>();
        // 指定获取哪个会议室的成员
        MultiUserChat mUserChat = new MultiUserChat(connection, roomName + "@conference." + connection.getServiceName());
        // 获取成员列表
        Iterator<String> user = mUserChat.getOccupants();
        // 读取列表成员
        while (user.hasNext()) {
            String userName = StringUtils.parseResource(user.next());
            System.out.println("会议室" + roomName + "列表成员--------" + userName);
            mUserList.add(userName);
        }
        return mUserList;
    }


    /**
     * 收藏一个群组
     * @param connection
     * @param name
     * @param nickname
     * @param password
     */
    public static Boolean addBookmarkedRoom(XMPPConnection connection, String name,String nickname,String password){
        try {
            BookmarkManager manager = BookmarkManager.getBookmarkManager(connection);
            String jid = name + "@conference." + connection.getServiceName();
            manager.addBookmarkedConference(name, jid, true, nickname, password);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取已经收藏了的所有群组
     * @param connection
     * @return
     */
    public static List<Rooms> getBookmarkedRooms(XMPPConnection connection) {

        try {
            //群聊管理器
            MultiUserChat muc = new MultiUserChat(connection,connection.getUser());
            BookmarkManager bm = BookmarkManager.getBookmarkManager(connection);
            Collection<BookmarkedConference> conferences = bm.getBookmarkedConferences();
            Iterator<BookmarkedConference> it = conferences.iterator();
            List<Rooms> list = new ArrayList<>();
            while(it.hasNext()) {
                BookmarkedConference bk = it.next();
                /*
                System.out.println("群jid:" + bk.getJid());

                System.out.println("群名:" + bk.getName());
                //System.out.println("群密码:" + it.next().getPassword());
                */
                //描述
                RoomInfo info = muc.getRoomInfo(connection,bk.getJid());
                /*
                System.out.println("群描述:" + info.getDescription());
                //人数
                System.out.println("群人数:" + info.getOccupantsCount());
                System.out.println("群主题:" + info.getSubject());
                */

                Rooms fr = new Rooms(bk.getName(),bk.getJid(),info.getOccupantsCount(),info.getDescription(),info.getSubject());
                list.add(fr);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取已经加入的的群组列表
     * @param connection
     * @return
     */
    public static void getJoinedRooms(XMPPConnection connection){

        //群聊管理器
        MultiUserChat muc = new MultiUserChat(connection,connection.getUser());
        //获取所有加入的房间的jid集合
        Iterator<String> rooms = muc.getJoinedRooms(connection,connection.getUser());
        while (rooms.hasNext()) {
            String room = rooms.next();
            try {
                RoomInfo info = muc.getRoomInfo(connection,room);
                //jid
                System.out.println(info.getRoom());
                //描述
                System.out.println(info.getDescription());
                //人数
                System.out.println(info.getOccupantsCount());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 加入会议室
     *
     * @param user 昵称
     //* @param password 会议室密码
     * @param roomsName 会议室名
     * @param connection
     */
    public static MultiUserChat joinMultiUserChat(XMPPConnection connection,String user,String roomsName,int timeout) {
        try {

                // 使用XMPPConnection创建一个MultiUserChat窗口

                MultiUserChat muc = new MultiUserChat(connection, roomsName + "@conference." + connection.getServiceName());
            /*
            //聊天室服务将会决定要接受的历史记录数量
            DiscussionHistory history = new DiscussionHistory();
            history.setMaxStanzas(0);
            //从哪个日期开始的历史消息
            history.setSince(new Date());
            history.setSince(2015.1.1);
            */
                // 用户加入聊天室
                muc.join(user);
                //muc.join(user,password,DiscussionHistory history,int timeout);

                System.out.println("会议室加入成功........");

                return muc;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("会议室加入失败........");
            return null;
        }
    }

    /*
    * 发送多人聊天消息
    * 注意: 在发送聊天室消息之前，必须先加入聊天室(调用join方法)，否则发送的消息实际上是没有发送成功的。
    * */
    public static void sendRoomMessage(XMPPConnection connection,String roomname,String message) throws Exception {
        //MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
        MultiUserChat muc = new MultiUserChat(connection,roomname+"@conference."+connection.getServiceName());
        //muc.createOrJoin("nickname");
        muc.sendMessage(message);
    }



    public static void configure(ProviderManager pm)
    {

        // Private Data Storage
        pm.addIQProvider("query", "jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());

        // Time
        try
        {
            pm.addIQProvider("query", "jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
        }
        catch (ClassNotFoundException e)
        {
            Log.w("TestClient", "Can't load class for org.jivesoftware.smackx.packet.Time");
        }

        // Roster Exchange
        pm.addExtensionProvider("x", "jabber:x:roster", new RosterExchangeProvider());

        // Message Events
        pm.addExtensionProvider("x", "jabber:x:event", new MessageEventProvider());

        // Chat State
        pm.addExtensionProvider("active", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("composing", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("paused", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("inactive", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("gone", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

        // XHTML
        pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());

        // Group Chat Invitations
        pm.addExtensionProvider("x", "jabber:x:conference", new GroupChatInvitation.Provider());

        // Service Discovery # Items
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());

        // Service Discovery # Info
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());

        // Data Forms
        pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());

        // MUC User
        pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user", new MUCUserProvider());

        // MUC Admin
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin", new MUCAdminProvider());

        // MUC Owner
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());

        // Delayed Delivery
        pm.addExtensionProvider("x", "jabber:x:delay", new DelayInformationProvider());

        // Version
        try
        {
            pm.addIQProvider("query", "jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
        }
        catch (ClassNotFoundException e)
        {
            // Not sure what's happening here.
        }

        // VCard
        pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());

        // Offline Message Requests
        pm.addIQProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());

        // Offline Message Indicator
        pm.addExtensionProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());

        // Last Activity
        pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());

        // User Search
        pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());

        // SharedGroupsInfo
        pm.addIQProvider("sharedgroup", "http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());

        // JEP-33: Extended Stanza Addressing
        pm.addExtensionProvider("addresses", "http://jabber.org/protocol/address", new MultipleAddressesProvider());

        // FileTransfer
        pm.addIQProvider("si", "http://jabber.org/protocol/si", new StreamInitiationProvider());

        pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams", new BytestreamsProvider());

        // Privacy
        pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
        pm.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
        pm.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.MalformedActionError());
        pm.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadLocaleError());
        pm.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadPayloadError());
        pm.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadSessionIDError());
        pm.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.SessionExpiredError());
    }

}
