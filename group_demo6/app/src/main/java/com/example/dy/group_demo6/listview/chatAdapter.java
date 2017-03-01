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

public class chatAdapter extends ArrayAdapter<chat> {
    private int resourceId;
    public chatAdapter(Context context, int textViewResourceId, List<chat> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    public View getView(int position, View converView, ViewGroup parent){
        chat chat1 = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView username = (TextView) view.findViewById(R.id.tv_username);
        TextView message = (TextView) view.findViewById(R.id.tv_message);
        username.setText(chat1.getUsername()+":");
        message.setText(chat1.getMessage());
        return view;
    }
}
