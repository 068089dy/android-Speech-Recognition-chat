package com.example.dy.group_demo6.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dy.group_demo6.R;

import java.util.List;

/**
 * Created by dy on 17-2-21.
 */

public class RoomsAdapter extends ArrayAdapter<Rooms> {
    private int resourceId;
    public RoomsAdapter(Context context, int textViewResourceId, List<Rooms> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    public View getView(int position, View converView, ViewGroup parent){
        Rooms room = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView roomname = (TextView) view.findViewById(R.id.tv_roomname);
        TextView roomoccpants = (TextView) view.findViewById(R.id.tv_roomoccpants);
        roomname.setText("群名："+room.getName());
        roomoccpants.setText("群人数："+Integer.toString(room.getOccupants()));
        return view;
    }
}
