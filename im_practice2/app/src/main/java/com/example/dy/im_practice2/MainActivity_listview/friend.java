package com.example.dy.im_practice2.MainActivity_listview;

/**
 * Created by 068089dy on 2016/10/15.
 */
public class friend {
    private String name;
    private int imageId;
    public friend(String name, int imageId){
        this.name = name;
        this.imageId = imageId;
    }
    public String getName(){
        return name;
    }
    public int getimageId(){
        return imageId;
    }
}
