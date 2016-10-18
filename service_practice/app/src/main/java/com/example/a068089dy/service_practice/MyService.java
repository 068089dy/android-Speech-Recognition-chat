package com.example.a068089dy.service_practice;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class MyService extends Service {
    private DownloadBinder mBinder = new DownloadBinder();

    class DownloadBinder extends Binder{
        public void startDownload(){
            Log.d("Myservice", "startDownload");
        }
        public int getProgress(){
            Log.d("MyService","getProgress");
            return 0;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    public void onCreate(){
        super.onCreate();
        //在API11之后构建Notification的方式
        //获取一个Notification构造器
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext());
        Intent nfIntent = new Intent(this, MainActivity.class);
        // 设置PendingIntent
        // 设置下拉列表中的图标(大图标)
        // 设置下拉列表里的标题
        // 设置状态栏内的小图标
        // 设置上下文内容
        // 设置该通知发生的时间
        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setContentTitle("下拉列表中的Title")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("要显示的内容")
                .setWhen(System.currentTimeMillis());
        // 获取构建好的Notification
        Notification notification = builder.build();
        //设置为默认的声音
        notification.defaults = Notification.DEFAULT_SOUND;
        startForeground(1,notification);
        Log.d("myserivce","asd");
    }




    public int onStartCommand(Intent intent,int flags,int startId){
        return super.onStartCommand(intent,flags,startId);
    }
    public void onDestroy(){
        super.onDestroy();
        Log.d("myservice","stop");
    }
}
