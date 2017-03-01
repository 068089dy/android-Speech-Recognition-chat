package com.example.dy.im_practice2.listview.MainActivity_listview;

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
 * Created by 068089dy on 2016/10/16.
 */
public class friendAdapter extends ArrayAdapter<friend> {
    private int resourceId;
    public friendAdapter(Context context, int textViewResourceId, List<friend> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    public View getView(int position, View converView, ViewGroup parent){
        friend frid = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        ImageView friendimage = (ImageView) view.findViewById(R.id.friend_image);
        TextView friendname = (TextView) view.findViewById(R.id.friend_name);
        friendimage.setImageResource(frid.getimageId());
        friendname.setText(frid.getName());
        return view;
    }
}
