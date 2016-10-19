package com.example.ding.wechatpractice1;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ding on 16-9-1.
 */
public class DateFomats {


    /** 转换获取出入的字符串时间值  */
    public static String getStringTime(String strTime){
        SimpleDateFormat sd = new SimpleDateFormat("MM-dd"+"//\0\0"+"HH:"+"mm");
        long sTime = Long.valueOf(strTime);

        return sd.format(new Date(sTime * 1000));
    }


    /** 获取并格式化当前时间值  */
    public static String getCurrentTime(long date){
        SimpleDateFormat sd = new SimpleDateFormat("MM-dd"+"\t\t"+"HH:"+"mm");
        return sd.format(date);
    }
}
