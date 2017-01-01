package com.example.dy.im_practice2.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by dy on 12/17/16.
 */

public class LoccalReceiver extends BroadcastReceiver {
    /*
    String from;
    String body;
    public LoccalReceiver(String from,String body){
        this.body = body;
        this.from = from;
    }*/
    public void onReceive(Context context, Intent intent){
        Toast.makeText(context,"asd",Toast.LENGTH_LONG).show();
    }
}
