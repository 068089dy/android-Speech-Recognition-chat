package com.d3.d3xmpp.constant;

import com.d3.d3xmpp.model.User;
import com.d3.d3xmpp.util.Util;

public class Constants {
	//����
	public final static boolean IS_DEBUG = true;
	public final static String SERVER_HOST = "123.207.174.226";
	public final static String SERVER_URL = "http://"+SERVER_HOST+":9090/plugins/xmppservice/";
	public static String SERVER_NAME = "d3";
	public final static int SERVER_PORT = 5222;
	public final static String PATH =  Util.getInstance().getExtPath()+"/xmpp";
	public final static String SAVE_IMG_PATH = PATH + "/images";//���ñ���ͼƬ�ļ���·��
	public final static String SAVE_SOUND_PATH = PATH + "/sounds";//���������ļ���·��
	public final static int UPDATE_TIME =  60*1000;   //����λ��ˢ��ʱ�䣬ͬʱҲ���Լ���λ���ϴ�ʱ����
	public final static int ADR_UPDATE_TIME =  30*1000;   //ˢ���Լ���λ��
	public final static String SHARED_PREFERENCES = "openfile";
	public final static String LOGIN_CHECK = "check";
	public final static String LOGIN_ACCOUNT = "account";
	public final static String LOGIN_PWD = "pwd";	
	//URL
	public final static String URL_EXIT_ROOM = SERVER_URL+"exitroom";
	public final static String URL_EXIST_ROOM = SERVER_URL+"existroom";
	public final static String URL_GET_ADR = SERVER_URL+"getadr";
	
	//����
	public static String USER_NAME = "";
	public static String PWD = "";
	public static User loginUser;
}
