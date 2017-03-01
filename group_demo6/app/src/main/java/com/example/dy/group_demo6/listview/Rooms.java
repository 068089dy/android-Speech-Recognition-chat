package com.example.dy.group_demo6.listview;

/**
 * Created by dy on 17-2-20.
 */

public class Rooms {
    private String Subject;//主题
    private String Name;
    private String Jid;
    private int Occupants;//群中人数
    private String Description;//描述

    public Rooms(String Name,String Jid,int Occupants,String Description,String Subject){
        this.Name = Name;
        this.Jid = Jid;
        this.Occupants = Occupants;
        this.Description = Description;
        this.Subject = Subject;

    }

    public void setSubject(String subject){
        this.Subject = subject;
    }
    public void setName(String Name){
        this.Name = Name;
    }
    public void setJid(String Jid){
        this.Jid = Jid;
    }
    public void setOccupants(int Occupants){
        this.Occupants = Occupants;
    }
    public void setDescription(String Description){
        this.Description = Description;
    }

    public String getName(){
        return Name;
    }
    public String getJid(){
        return Jid;
    }
    public String getDescription(){
        return Description;
    }
    public int getOccupants(){
        return Occupants;
    }
    public String getSubject(){
        return Subject;
    }
}
