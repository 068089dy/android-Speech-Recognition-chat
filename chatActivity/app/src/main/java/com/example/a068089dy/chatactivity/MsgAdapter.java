package com.example.a068089dy.chatactivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;

/**
 * Created by 068089dy on 2016/10/17.
 */
public class MsgAdapter extends ArrayAdapter<Msg>{
    private int resourceId;
    public MsgAdapter(Context context, int textViewResourceId, List<Msg> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    public View getView(int position, View converView, ViewGroup parent){
        Msg msg = getItem(position);
        View view;
        ViewHoloder viewHoloder;
        if(converView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHoloder = new ViewHoloder();
            viewHoloder.leftLayout = (LinearLayout) view.findViewById(R.id.left_layout);
            viewHoloder.rightLayout = (LinearLayout) view.findViewById(R.id.right_layout);
            viewHoloder.leftMsg = (TextView) view.findViewById(R.id.left_msg);
            viewHoloder.rightMsg = (TextView) view.findViewById(R.id.right_msg);
            view.setTag(viewHoloder);

        }else{
            view = converView;
            viewHoloder = (ViewHoloder) view.getTag();
        }

        if(msg.getType() == msg.TYPE_RECEIVED){
            //显示左面，右面隐藏
            viewHoloder.rightLayout.setVisibility(View.GONE);
            viewHoloder.leftLayout.setVisibility(View.VISIBLE);
            viewHoloder.leftMsg.setText(msg.getContent());

        }else if(msg.getType() == msg.TYPE_SEND){
            //显示右面，隐藏左面
            viewHoloder.rightLayout.setVisibility(View.VISIBLE);
            viewHoloder.leftLayout.setVisibility(View.GONE);
            viewHoloder.rightMsg.setText(msg.getContent());
        }
        return view;

    }
    class ViewHoloder{
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
    }
}
