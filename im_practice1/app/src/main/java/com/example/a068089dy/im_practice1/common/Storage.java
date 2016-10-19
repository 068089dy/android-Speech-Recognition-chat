package com.example.a068089dy.im_practice1.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 068089dy on 2016/10/16.
 * SharedPreference类用于存储数据
 */
public class Storage {
    public static final String STORAGE_FILE_NAME = "chatclient.config";
    //
    public static void putString(Context ctx, String key, String value){
        SharedPreferences sharedPreferences = getSharedPreferences(ctx);
        sharedPreferences.edit().putString(key,value).commit();
    }
    //“...”动态传入参数，可以不传或传多个
    public static String getString(Context ctx,String key,String... defaultValue){
        SharedPreferences sharedPreferences  = getSharedPreferences(ctx);
        String dv = "";
        for(String v:defaultValue){
            dv = v;
            break;
        }
        return sharedPreferences.getString(key,dv);
    }
    //
    public static void putBoolean(Context ctx,String key,boolean value){
        SharedPreferences sharedPreferences = getSharedPreferences(ctx);
        sharedPreferences.edit().putBoolean(key, value).commit();
    }
    //
    public static boolean getBoolean(Context ctx,String key,boolean... defaultValue){
        SharedPreferences sharedPreferences  = getSharedPreferences(ctx);
        boolean dv = false;
        for(boolean v:defaultValue){
            dv = v;
            break;
        }
        return sharedPreferences.getBoolean(key,dv);
    }
    private static SharedPreferences getSharedPreferences(Context ctx){
        return ctx.getSharedPreferences(STORAGE_FILE_NAME,Context.MODE_PRIVATE);
    }
}
