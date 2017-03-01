package com.d3.d3xmpp.xmpp;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.packet.RosterPacket.ItemType;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.packet.VCard;
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
import org.xmlpull.v1.XmlPullParser;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;

import com.d3.d3xmpp.activites.ChatActivity;
import com.d3.d3xmpp.constant.Constants;
import com.d3.d3xmpp.constant.MyApplication;
import com.d3.d3xmpp.model.ChatItem;
import com.d3.d3xmpp.model.Friend;
import com.d3.d3xmpp.model.Room;
import com.d3.d3xmpp.util.FormatTools;
import com.d3.d3xmpp.util.PinyinComparator;
import com.d3.d3xmpp.util.Tool;

public class XmppConnection {

	private static XMPPConnection connection = null;
	private static XmppConnection xmppConnection;
	public Roster roster;
	private static Chat newchat;
	private static MultiUserChat mulChat;
	private static List<Friend> friendList = new ArrayList<Friend>();
	private XmppConnecionListener connectionListener;
	private XmppMessageInterceptor xmppMessageInterceptor;
	private XmppMessageListener messageListener;
	public static List<Room> myRooms = new ArrayList<Room>();
	public static List<Room> leaveRooms = new ArrayList<Room>();

	static {
		try {
			Class.forName("org.jivesoftware.smack.ReconnectionManager");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ����ģʽ
	 * 
	 * @return
	 */
	public static XmppConnection getInstance() {
		if (xmppConnection == null) {
			xmppConnection = new XmppConnection();
		}
		return xmppConnection;
	}
	
	public void setNull(){
		connection = null;
	}

	/**
	 * ��������
	 */
	public XMPPConnection getConnection() {
		if (connection == null) {
			openConnection();
		}
		return connection;
	}

	/**
	 * ������
	 */
	public boolean openConnection() {
		try {
			if (null == connection || !connection.isAuthenticated()) {
				XMPPConnection.DEBUG_ENABLED = true;// ����DEBUGģʽ
				// ��������
				ConnectionConfiguration config = new ConnectionConfiguration(Constants.SERVER_HOST,
						Constants.SERVER_PORT, Constants.SERVER_NAME);
//				if (Build.VERSION.SDK_INT >= 14) {
//					config.setKeystoreType("AndroidCAStore"); //$NON-NLS-1$  
//					config.setTruststorePassword(null);
//					config.setKeystorePath(null);
//				} else {
//					config.setKeystoreType("BKS"); //$NON-NLS-1$  
//					String path = System.getProperty("javax.net.ssl.trustStore"); //$NON-NLS-1$  
//					if (path == null)
//						path = System.getProperty("java.home") + File.separator //$NON-NLS-1$  
//								+ "etc" + File.separator + "security" //$NON-NLS-1$ //$NON-NLS-2$  
//								+ File.separator + "cacerts.bks"; //$NON-NLS-1$  
//					config.setKeystorePath(path);
//				}
				// config.setSASLAuthenticationEnabled(false);
				config.setReconnectionAllowed(true);
				config.setSecurityMode(SecurityMode.disabled);
				config.setSASLAuthenticationEnabled(false);
				config.setSendPresence(true); // ״̬��Ϊ���ߣ�Ŀ��Ϊ��ȡ������Ϣ
				connection = new XMPPConnection(config);
				connection.connect();// ���ӵ�������
				// ���ø���Provider����������ã�����޷���������
				configureConnection(ProviderManager.getInstance());
				// ����B�ӱO 
				connectionListener = new XmppConnecionListener();
				connection.addConnectionListener(connectionListener);
				xmppMessageInterceptor = new XmppMessageInterceptor();
				messageListener = new XmppMessageListener();
				connection.addPacketInterceptor(xmppMessageInterceptor,new PacketTypeFilter(Message.class));
				connection.addPacketListener(messageListener,new PacketTypeFilter(Message.class));
				connection.addPacketListener(new XmppPresenceListener(), new PacketTypeFilter(Presence.class));
				connection.addPacketInterceptor(new XmppPresenceInterceptor(), new PacketTypeFilter(Presence.class));
				// connection.addPacketListener(arg0, arg1);
				ProviderManager.getInstance().addIQProvider("muc", "MZH", new MUCPacketExtensionProvider());
				return true;
			}
		} catch (XMPPException xe) {
			xe.printStackTrace();
			connection = null;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * �ر�����
	 */
	public void closeConnection() {
		if (connection != null) {
			connection.removeConnectionListener(connectionListener);
			ProviderManager.getInstance().removeIQProvider("muc", "MZH");
			try {
				connection.disconnect();
			} catch (Exception e) {
				if(Constants.IS_DEBUG)
				Log.e("asmack dis", e.getMessage());
				e.printStackTrace();
			}
			finally{
				connection = null;
				xmppConnection = null;
			}
		}
		if(Constants.IS_DEBUG)
		Log.e("XmppConnection", "close connection");
	}
	
	
	public void reconnect(){
		new Thread(){
			@Override
			public void run() {
				try {
					sleep(5*1000);
					ChatActivity.isLeaving = true;
					closeConnection();
					login(Constants.USER_NAME, Constants.PWD);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				super.run();
			}
		}.start();
	}
	
	
	public void loadFriendAndJoinRoom(){
		new Thread(){
			public void run() {
				try {
					getFriends();
					sleep(1*1000);
					if (XmppConnection.getInstance().getMyRoom()!=null) {
						for (Room room : XmppConnection.getInstance().getMyRoom()) {
							XmppConnection.getInstance().joinMultiUserChat(Constants.USER_NAME,room.name, false);
						}
					}
					ChatActivity.isLeaving = false;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}
	
	/**
	 * ��¼
	 * 
	 * @param account
	 *            ��¼�ʺ�
	 * @param password
	 *            ��¼����
	 * @return
	 */
	public boolean login(String account, String password) {
		try {
			if (getConnection() == null)
				return false;
			if (!getConnection().isAuthenticated() && getConnection().isConnected()) {
				getConnection().login(account, password);
				// // �����ھQ��B
				Presence presence = new Presence(Presence.Type.available);
				// Constants.USER_STATUS = presence.getStatus();
				presence.setMode(Presence.Mode.available);
				getConnection().sendPacket(presence);

				roster = XmppConnection.getInstance().getConnection().getRoster();
//				friendListner = new FriendListner();
//				roster.addRosterListener(friendListner);
				//���������������������   
//	            MultiUserChat.addInvitationListener(getConnection(), new InvitationListener() {
//					
//					@Override
//					public void invitationReceived(Connection arg0, String arg1, String arg2,
//							String arg3, String arg4, Message arg5) {
//						// TODO Auto-generated method stub
//						
//					}
//				});
				loadFriendAndJoinRoom();
				return true;
			}

		} catch (XMPPException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	/**
	 * ע��
	 * 
	 * @param account
	 *            ע���ʺ�
	 * @param password
	 *            ע������
	 * @return 1��ע��ɹ� 0��������û�з��ؽ��2������˺��Ѿ�����3��ע��ʧ��
	 */
	public IQ regist(String account, String password) {
		if (getConnection() == null)
			return null;
		Registration reg = new Registration();
		reg.setType(IQ.Type.SET);
		reg.setTo(getConnection().getServiceName());
		reg.setUsername(account);
		reg.setPassword(password);
//		reg.addAttribute("android", "geolo_createUser_android");
		PacketFilter filter = new AndFilter(new PacketIDFilter(reg.getPacketID()), new PacketTypeFilter(IQ.class));

		PacketCollector collector = getConnection().createPacketCollector(filter);
		// ��ע���Packet����Listener����Ϊֻ�еȵ�����ע��ɹ������ǲſ��Խ���
		// collector.addPacketListener(packetListener, filter);
		// ��������ˣ�����ע��Packet����ע������Registration��Packet������
		getConnection().sendPacket(reg);

		IQ result = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
		// Stop queuing results
		collector.cancel();

		return result;

	}
	
	
	/**
	 * �޸�����
	 * @param pwd
	 * @return
	 */
	public boolean changPwd(String pwd){
    	try {
			getConnection().getAccountManager().changePassword(pwd);
			return true;
		} catch (XMPPException e) {
			e.printStackTrace();
			return false;
		}
    }
	
	public void setRecevier(String chatName,int chatType){
		if (getConnection() == null)
			return;
		
		if (chatType == ChatItem.CHAT) {
			// �����ػ�
			ChatManager cm = XmppConnection.getInstance().getConnection().getChatManager();
			// ������Ϣ��pc�������ĺ��ѣ���ȡ�Լ��ķ��������ͺ��ѣ�
			newchat = cm.createChat(getFullUsername(chatName), null);
		}
		else if (chatType == ChatItem.GROUP_CHAT) {
			mulChat = new MultiUserChat(getConnection(), getFullRoomname(chatName));
		}
	}
	
	//�����ı���Ϣ
	@SuppressLint("NewApi")
	public void sendMsg(String chatName,String msg,int chatType) throws Exception{
		if (getConnection() == null){
			throw new Exception("XmppException");
		}
		
		if (msg.isEmpty()) {
			Tool.initToast(MyApplication.getInstance(), "���д��ʲô��");
		}
		else {
			if (chatType == ChatItem.CHAT) {
				ChatManager cm = XmppConnection.getInstance().getConnection().getChatManager();
				// ������Ϣ��pc�������ĺ��ѣ���ȡ�Լ��ķ��������ͺ��ѣ�
				Chat newchat = cm.createChat(getFullUsername(chatName), null);
				newchat.sendMessage(msg);
			}
			else if (chatType == ChatItem.GROUP_CHAT) {
				MultiUserChat mulChat = new MultiUserChat(getConnection(), getFullRoomname(chatName));
				mulChat.sendMessage(msg);
			}
		}
	}
	
	//�����ı���Ϣ
	@SuppressLint("NewApi")
	public void sendMsg(String msg,int chatType) throws Exception{
		if (getConnection() == null){
			throw new Exception("XmppException");
		}
		
		if (msg.isEmpty()) {
			Tool.initToast(MyApplication.getInstance(), "���д��ʲô��");
		}
		else {
			if (chatType == ChatItem.CHAT) {
				newchat.sendMessage(msg);
			}
			else if (chatType == ChatItem.GROUP_CHAT) {
				mulChat.sendMessage(msg);
			}
		}
	}
	
	
	//������Ϣ����������
	public void sendMsgWithParms(String msg,String[] parms,Object[] datas,int chatType) throws Exception{
		if (getConnection() == null){
			throw new Exception("XmppException");
		}
		
		org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
		for (int i = 0; i < datas.length; i++) {
			message.setProperty(parms[i], datas[i]);
		}
		message.setBody(msg);
		
		if (chatType == ChatItem.CHAT) {
			newchat.sendMessage(message);
		}
		else if (chatType == ChatItem.GROUP_CHAT) {
			mulChat.sendMessage(msg+":::"+datas[0]);
		}
	}
	
	
	/**
	 * ��������
	 * @param key
	 * @return
	 */
	public List<String> searchUser(String key){
		List<String> userList = new ArrayList<String>();
		try{
			UserSearchManager search = new UserSearchManager(getConnection());
			Form searchForm = search.getSearchForm("search."+Constants.SERVER_NAME);
			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("Username", true);
			answerForm.setAnswer("search", key);
			ReportedData data = search.getSearchResults(answerForm,"search."+Constants.SERVER_NAME);
			
			Iterator<Row> it = data.getRows();
			Row row=null;
			while(it.hasNext()){
				row=it.next();
				userList.add(row.getValues("Username").next().toString());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return userList;
	}
	

	/**
	 * ��Ӻ��� �޷���
	 * 
	 * @param userName  id
	 * @param name   �ǳ�
	 * @return
	 */
	public boolean addUser(String userName) {
		if (getConnection() == null)
			return false;
		try {
			getConnection().getRoster().createEntry(getFullUsername(userName), getFullUsername(userName), null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * ɾ������
	 * 
	 * @param userName
	 * @return
	 */
	public boolean removeUser(String userName) {
		if (getConnection() == null)
			return false;
		try {
			RosterEntry entry = null;
			if (userName.contains("@"))
				entry = getConnection().getRoster().getEntry(userName);
			else
				entry = getConnection().getRoster().getEntry(userName + "@" + getConnection().getServiceName());
			if (entry == null)
				entry = getConnection().getRoster().getEntry(userName);
			getConnection().getRoster().removeEntry(entry);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * �޸��û���Ϣ
	 * 
	 * @param file
	 */
	public boolean changeVcard(VCard vcard) {
		if (getConnection() == null)
			return false;
		try {
			// ���������룬���No VCard for
			ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());
			vcard.save(getConnection());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}


	/**
	 * �޸��û�ͷ��
	 * 
	 * @param file
	 */
	public Bitmap changeImage(File file) {
		Bitmap bitmap = null;
		if (getConnection() == null)
			return bitmap;
		try {
			VCard vcard = Constants.loginUser.vCard;
			// ���������룬���No VCard for
			ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());

			byte[] bytes;
			bytes = getFileBytes(file);
			String encodedImage = StringUtils.encodeBase64(bytes);
//			vcard.setAvatar(bytes, encodedImage);
			// vcard.setEncodedImage(encodedImage);
//			vcard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>" + encodedImage + "</BINVAL>", true);
			vcard.setField("avatar", encodedImage);
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			bitmap = FormatTools.getInstance().InputStream2Bitmap(bais);
			// Image image = ImageIO.read(bais);
			// ����������������ImageIcon ic = new ImageIcon(image);��
			vcard.save(getConnection());

		} catch (Exception e) {
			e.printStackTrace();

		}
		return bitmap;
	}

	/**
	 * ��ȡ�û���Ϣ
	 * @param user
	 * @return
	 */
	public VCard getUserInfo(String user) {  //null ʱ���Լ�
		try {
			VCard vcard = new VCard();
			// ���������룬���No VCard for
			ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());
			if (user == null) {
				vcard.load(getConnection());
			}
			else {
				vcard.load(getConnection(), user + "@" + Constants.SERVER_NAME);
			}
			if (vcard != null)
				return vcard;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * ��ȡ�û�ͷ����Ϣ
	 * 
	 * @param connection
	 * @param user
	 * @return
	 */
	public Bitmap getUserImage(String user) {  //null ʱ���Լ�

		ByteArrayInputStream bais = null;
		try {
			VCard vcard = new VCard();
			// ���������룬���No VCard for
			ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());
			if (user == null) {
				vcard.load(getConnection());
			}
			else {
				vcard.load(getConnection(), user + "@" + Constants.SERVER_NAME);
			}
			if (vcard == null || vcard.getAvatar() == null)
				return null;
			bais = new ByteArrayInputStream(vcard.getAvatar());

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (bais == null)
			return null;
		return FormatTools.getInstance().InputStream2Bitmap(bais);
	}
	
	/**
	 * �ļ�ת�ֽ�
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private byte[] getFileBytes(File file) throws IOException {
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
			int bytes = (int) file.length();
			byte[] buffer = new byte[bytes];
			int readBytes = bis.read(buffer);
			if (readBytes != buffer.length) {
				throw new IOException("Entire file not read");
			}
			return buffer;
		} finally {
			if (bis != null) {
				bis.close();
			}
		}
	}


	/**
	 * ��������
	 * 
	 * @param roomName
	 *            ��������
	 */
	public MultiUserChat createRoom(String roomName) {//String user, 
		if (getConnection() == null)
			return null;

		MultiUserChat muc = null;
		try {
			// ����һ��MultiUserChat
			muc = new MultiUserChat(getConnection(), roomName + "@conference." + getConnection().getServiceName());
			// ����������
			muc.create(roomName);

			// ��������ҵ����ñ�
			Form form = muc.getConfigurationForm();
			// ����ԭʼ������һ��Ҫ�ύ���±���
			Form submitForm = form.createAnswerForm();
			// ��Ҫ�ύ�ı����Ĭ�ϴ�
			for (Iterator fields = form.getFields(); fields.hasNext();) {
				FormField field = (FormField) fields.next();
				if (!FormField.TYPE_HIDDEN.equals(field.getType())
						&& field.getVariable() != null) {
					// ����Ĭ��ֵ��Ϊ��
					submitForm.setDefaultAnswer(field.getVariable());
				}
			}
			// ���������ҵ���ӵ����
//			List<String> owners = new ArrayList<String>();
//			owners.add(getConnection().getUser());// �û�JID
//			submitForm.setAnswer("muc#roomconfig_roomowners", owners);
			// �����������ǳ־������ң�����Ҫ����������
			submitForm.setAnswer("muc#roomconfig_persistentroom", true);
			// ������Գ�Ա����
			submitForm.setAnswer("muc#roomconfig_membersonly", false);
			// ����ռ��������������
			submitForm.setAnswer("muc#roomconfig_allowinvites", true);
			// if (!password.equals("")) {
			// // �����Ƿ���Ҫ����
//			 submitForm.setAnswer("muc#roomconfig_passwordprotectedroom",false);
			// // ���ý�������
			// submitForm.setAnswer("muc#roomconfig_roomsecret", password);
			// }
			// �ܹ�����ռ������ʵ JID �Ľ�ɫ
			// submitForm.setAnswer("muc#roomconfig_whois", "anyone");
			// ��������
			submitForm.setAnswer("muc#roomconfig_roomdesc", "mulchat");
			// ��¼����Ի�
			submitForm.setAnswer("muc#roomconfig_enablelogging", true);
			// ������ע����ǳƵ�¼
			submitForm.setAnswer("x-muc#roomconfig_reservednick", false);
			// ����ʹ�����޸��ǳ�
			submitForm.setAnswer("x-muc#roomconfig_canchangenick", true);
			// �����û�ע�᷿��
			submitForm.setAnswer("x-muc#roomconfig_registration", true);
			// ��������ɵı�����Ĭ��ֵ����������������������
			muc.sendConfigurationForm(submitForm);
//			muc.addMessageListener(new TaxiMultiListener());
		} catch (XMPPException e) {
			Tool.initToast(MyApplication.getInstance(), "���粻����,������");
			Log.e("you wenti", "���粻����,������" + e.getMessage());
			e.printStackTrace();
			return null;
		}

		return muc;
	}


//	public List<Friend> getAllFriends() {
//		List<Friend> list = new ArrayList<Friend>();
//		if (roster == null) {
//			roster = XmppConnection.getInstance().getConnection().getRoster();
//		}
//		
//		Collection<RosterEntry> entries = roster.getEntries();
//		
//		for(RosterEntry entry : entries){
//			list.add(new Friend(XmppConnection.getUsername(entry.getUser()),entry.getType()));
//		}
//		return list;
//	}
//	
	
	/**
	 * �����ȡxmpp����
	 */
	public List<Friend> getFriends() {
		friendList.clear();
		if (roster == null) {
			roster = XmppConnection.getInstance().getConnection().getRoster();
		}
		Collection<RosterEntry> entries = roster.getEntries();
		List<Friend> friendsTemp = new ArrayList<Friend>();
		
		for(RosterEntry entry : entries){
//			if (entry.getType() == ItemType.both) { //������from|| entry.getType() == ItemType.none || entry.getType() == ItemType.to
				friendsTemp.add(new Friend(XmppConnection.getUsername(entry.getUser()),entry.getType()));
//			}
//			friendListAll.add(new Friend(XmppConnection.getUsername(entry.getUser()),entry.getType()));
		} 
		
		//��Ҫ����ĸ����
		Friend[] usersArray = new Friend[friendsTemp.size()];
		for(int i=0;i<friendsTemp.size();i++){
			usersArray[i] = new Friend(friendsTemp.get(i).username,friendsTemp.get(i).type);
		}
		Arrays.sort(usersArray, new PinyinComparator());
		friendList = new ArrayList<Friend>(Arrays.asList(usersArray));
		return friendList;
	}
	
	public List<Friend> getFriendList() {
		return friendList;
	}
	
	public List<Friend> getFriendBothList(){
		List<Friend> friends= new ArrayList<Friend>();
		for (Friend friend : friendList) {
			if (friend.type == ItemType.both) {
				friends.add(friend);
			}
		}
		return friends;
	}
	
	public void changeFriend(Friend friend,ItemType type){
		getFriendList().get(getFriendList().indexOf(friend)).type = type;
	}
	
//	public List<Friend> getFriendListAll() {
//		return friendListAll;
//	}
	
	public List<Room> getMyRoom() {
		return myRooms;
	}
	
	/**
	 * ���������
	 * 
	 * @param user
	 *            �ǳ�
	 * @param restart
	 *            �Ƿ���Ҫ������asmack�Ĵ����������ʱ��Ϊtrue
	 * @param roomsName
	 *            ��������
	 */
	public MultiUserChat joinMultiUserChat(String user, String roomsName, boolean restart) {
		if (getConnection() == null)
			return null;
		try {

			// ʹ��XMPPConnection����һ��MultiUserChat����
			MultiUserChat muc = new MultiUserChat(getConnection(), roomsName + "@conference."
					+ getConnection().getServiceName());
			// �����ҷ��񽫻����Ҫ���ܵ���ʷ��¼����
			DiscussionHistory history = new DiscussionHistory();
			history.setMaxChars(0);
			history.setSince(new Date());
			// �û�����������
			muc.join(user, null, history, SmackConfiguration.getPacketReplyTimeout());
//			muc.join(user);
			if(Constants.IS_DEBUG)
			Log.e("muc", "�����ҡ�" + roomsName + "������ɹ�........");

			return muc;
		} catch (Exception e) {
			e.printStackTrace();
			if(Constants.IS_DEBUG)
			Log.e("muc", "�����ҡ�" + roomsName + "������ʧ��........");
			return null;
		}
		finally{
			if (restart) {
				reconnect();
			}
		}
	}
	
	public void leaveMuc(String roomName){
		// ʹ��XMPPConnection����һ��MultiUserChat����
		MultiUserChat muc = new MultiUserChat(getConnection(), getFullRoomname(roomName));
		muc.leave();
		if(Constants.IS_DEBUG)
		Log.e("muc", "�����ҡ�" + roomName + "���˳��ɹ�........");
	}

	/**
	 * ͨ��jid���username
	 * @param fullUsername
	 * @return
	 */
	public static String getUsername(String fullUsername){
		return fullUsername.split("@")[0];
	}
	
	/**
	 * ͨ��username���jid
	 * @param username
	 * @return
	 */
	public static String getFullUsername(String username){
		return username + "@" + Constants.SERVER_NAME;
	}
	
	/**
	 * ͨ��roomjid��ȡ������
	 * @param fullRoomname
	 * @return
	 */
	public static String getRoomName(String fullRoomname) {
		return fullRoomname.split("@")[0];
	}
	
	/**
	 * ͨ��roomjid��ȡ������
	 * @param fullRoomname
	 * @return
	 */
	public static String getRoomUserName(String fullRoomname) {
		return fullRoomname.split("/")[1];
	}
	/**
	 * ͨ��roomName���roomjid
	 * @param roomName
	 * @return
	 */
	public static String getFullRoomname(String roomName){
		return roomName + "@conference."+ Constants.SERVER_NAME;
	}
	
	/**
	 * ����providers�ĺ��� ASmack��/META-INFȱ��һ��smack.providers �ļ�
	 * 
	 * @param pm
	 */
	public void configureConnection(ProviderManager pm) {

		// Private Data Storage
		pm.addIQProvider("query", "jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());

		// Time
		try {
			pm.addIQProvider("query", "jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (ClassNotFoundException e) {
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
		try {
			pm.addIQProvider("query", "jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {
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
		pm.addIQProvider("sharedgroup", "http://www.jivesoftware.org/protocol/sharedgroup",
				new SharedGroupsInfo.Provider());

		// JEP-33: Extended Stanza Addressing
		pm.addExtensionProvider("addresses", "http://jabber.org/protocol/address", new MultipleAddressesProvider());

		// FileTransfer
		pm.addIQProvider("si", "http://jabber.org/protocol/si", new StreamInitiationProvider());

		pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams", new BytestreamsProvider());

		// Privacy
		pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
		pm.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
		pm.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.MalformedActionError());
		pm.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadLocaleError());
		pm.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadPayloadError());
		pm.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadSessionIDError());
		pm.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.SessionExpiredError());
	}

	
	/**
	 * ֱ��ͨ�� HttpMime's MultipartEntity �ύ���ݵ���������ʵ�ֱ��ύ���ܡ�
	 * 
	 * @return ���������ص�����
	 */

	public static String requestService(String url, Map<String, String> param){
		if(Constants.IS_DEBUG)
		Log.e("url", url);
		String result = "";

		try {
			DefaultHttpClient client = getNewHttpClient();

			HttpPost request = new HttpPost(url);
			List<NameValuePair> paramList = new ArrayList<NameValuePair>();
			
//			if (Constants.USER_NAME!="" && !param.containsKey("userName")) {
//				param.put("userName", Constants.USER_NAME);
//			}

			for (Map.Entry<String, String> entry : param.entrySet()) {
				paramList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				if(Constants.IS_DEBUG)
				Log.e("json parm", entry.getKey()+":"+entry.getValue());
			}
			HttpEntity entity1 = new UrlEncodedFormEntity(paramList, "UTF-8");

			request.setEntity(entity1);

			HttpResponse response = client.execute(request);

			int stateCode = response.getStatusLine().getStatusCode();
			if (stateCode == 201 || stateCode == 200) {
				HttpEntity entity = response.getEntity();

				result = EntityUtils.toString(entity, HTTP.UTF_8);
				if(Constants.IS_DEBUG)
				Log.e("json", result);
			} else {
				result = "";
			}
			request.abort();
		} catch (Exception e) {
			e.printStackTrace();
			if(Constants.IS_DEBUG)
			Log.e("json", e.toString());
		} finally {
			// �ͷ���Դ
			new DefaultHttpClient().getConnectionManager().shutdown();
		}
		return result;
	}

	private static DefaultHttpClient getNewHttpClient() {
		BasicHttpParams timeoutParams = new BasicHttpParams();

		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

//			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
//			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			// �������ӳ�ʱʱ��(��λ����)
			HttpConnectionParams.setConnectionTimeout(timeoutParams, 30000);
			HttpConnectionParams.setSoTimeout(timeoutParams, 150000);

			HttpProtocolParams.setVersion(timeoutParams, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(timeoutParams, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(timeoutParams, registry);

			return new DefaultHttpClient(ccm, timeoutParams);
		} catch (Exception e) {

			return new DefaultHttpClient(timeoutParams);
		}
	}
//	private boolean isFirst = true;
	class MUCPacketExtensionProvider implements IQProvider {
		@Override
		public IQ parseIQ(XmlPullParser parser) throws Exception {
			int eventType = parser.getEventType();
			myRooms.clear();
			leaveRooms.clear();
//			if (!isFirst) {
//				XmppConnection.getInstance().closeConnection();
//			}
//			isFirst = false;
			Room info = null;
			while (true) {
				if (eventType == XmlPullParser.START_TAG) {
					if ("room".equals(parser.getName())) {
						String account = parser.getAttributeValue("", "account");
						String roomName = parser.getAttributeValue("", "roomName");
						String roomJid = parser.getAttributeValue("", "roomJid");
						info = new Room();
						info.name = roomName;
						info.roomid = roomJid;
						myRooms.add(info);
					}
					
					if ("friend".equals(parser.getName())) {
						info.friendList.add(XmppConnection.getUsername(parser.nextText()));
					}
					
				} else if (eventType == XmlPullParser.END_TAG) {
					if ("muc".equals(parser.getName())) {
						break;
					}
				}
				eventType = parser.next();
			}
			return null;
		}

	}
}
