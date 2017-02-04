package com.example.dy.im_practice2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.dy.im_practice2.common.Storage;
import com.example.dy.im_practice2.listview.SettingActivity_listview.Setting;
import com.example.dy.im_practice2.listview.SettingActivity_listview.Setting_adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static com.example.dy.im_practice2.common.Const.KEY_AUTO_LOGIN;
import static com.example.dy.im_practice2.common.Const.KEY_LOGIN_SERVER;
import static com.example.dy.im_practice2.common.Const.KEY_PASSWD;
import static com.example.dy.im_practice2.common.Const.KEY_SAVE_PASSWORD;
import static com.example.dy.im_practice2.common.Const.KEY_USERNAME;
import static com.example.dy.im_practice2.loginactivity.ctx;



public class SettingActivity extends AppCompatActivity {
    private ListView listView_Setting;
    private Setting_adapter adapter;
    private List<Setting> settingList = new ArrayList<Setting>();
    private Button btn_setting_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initdata();
        adapter.notifyDataSetChanged();
        btn_setting_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MainActivity._instance.finish();
                //存储全局变量
                /*
                    Storage.putString(loginactivity.ctx, KEY_USERNAME, "");
                    Storage.putString(ctx, KEY_PASSWD, "");
                    Storage.putString(ctx, KEY_LOGIN_SERVER, "");

                    Storage.putBoolean(ctx, KEY_SAVE_PASSWORD, false);
               */
                Storage.putBoolean(ctx, KEY_AUTO_LOGIN, false);
                Intent intent = new Intent(SettingActivity.this,loginactivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void initView(){
        listView_Setting = (ListView) findViewById(R.id.lv_setting);
        adapter = new Setting_adapter(this,R.layout.setting_item,settingList);
        listView_Setting.setAdapter(adapter);
        btn_setting_save = (Button) findViewById(R.id.btn_Setting_save);


    }
    private void initdata(){
        Boolean isAutoLogin = Storage.getBoolean(this,KEY_AUTO_LOGIN);
        Boolean isSavePasswd = Storage.getBoolean(this,KEY_SAVE_PASSWORD);
        Setting setting = new Setting("自动登录",isAutoLogin);
        settingList.add(setting);
        Setting setting1 = new Setting("保存密码",isSavePasswd);
        settingList.add(setting1);
    }
}
