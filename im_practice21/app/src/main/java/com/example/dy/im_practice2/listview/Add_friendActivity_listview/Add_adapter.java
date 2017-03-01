package com.example.dy.im_practice2.listview.Add_friendActivity_listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.dy.im_practice2.R;

import java.util.List;

/**
 * Created by dy on 17-1-31.
 */

public class Add_adapter extends ArrayAdapter<Add>{

    int resourceId;

    public Add_adapter(Context context, int textresourceId, List<Add> objects){
        super(context,textresourceId,objects);
        resourceId = textresourceId;
    }

    public View getView(int position, View converView, ViewGroup parent){
        Add add = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        ImageView Addimage = (ImageView) view.findViewById(R.id.friend_image);
        TextView Addname = (TextView) view.findViewById(R.id.friend_name);
        Addimage.setImageResource(add.getImageId());
        Addname.setText(add.getUser());
        return view;
    }
}
