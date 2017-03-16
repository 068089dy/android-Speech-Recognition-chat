package com.example.dy.group_demo6.listview;

/**
 * Created by dy on 17-3-7.
 */

public class device {
    private String deviceid;
    private String devicename;
    private String devicetype;
    public device(String deviceid,String devicename,String devicetype){
        this.deviceid = deviceid;
        this.devicename = devicename;
        this.devicetype = devicetype;
    }
    public String getDeviceid(){
        return deviceid;
    }
    public String getDevicename(){
        return devicename;
    }
    public String getDevicetype(){
        return devicetype;
    }

}
