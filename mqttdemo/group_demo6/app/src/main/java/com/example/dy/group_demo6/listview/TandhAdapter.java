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
 * Created by dy on 17-3-8.
 */

public class TandhAdapter extends ArrayAdapter<Tandh> {
    private int resourceId;

    public TandhAdapter(Context context, int textViewResourceId, List<Tandh> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    public View getView(int position, View converView, ViewGroup parent){
        Tandh tandh = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView roomname = (TextView) view.findViewById(R.id.tv_name);
        TextView roomoccpants = (TextView) view.findViewById(R.id.tv_state);
        roomname.setText(tandh.getName());
        roomoccpants.setText(tandh.getStates());
        return view;
    }
}
