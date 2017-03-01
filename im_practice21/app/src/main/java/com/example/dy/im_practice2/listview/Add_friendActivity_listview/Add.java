package com.example.dy.im_practice2.listview.Add_friendActivity_listview;

/**
 * Created by dy on 17-1-31.
 */

public class Add {
    private int ImageId;
    private String user;

    public Add (int ImageId,String user){
        this.ImageId = ImageId;
        this.user = user;
    }

    public int getImageId(){
        return ImageId;
    }
    public String getUser(){
        return user;
    }
}
