package com.example.dy.group_demo6.listview;

/**
 * Created by dy on 17-2-21.
 */

public class chat {
    private String username;
    private String message;
    public chat(String username,String message){
        this.message = message;
        this.username = username;
    }
    public String getUsername(){
        return username;
    }
    public String getMessage(){
        return message;
    }
}
