package com.example.dy.im_practice2.listview.SettingActivity_listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.dy.im_practice2.R;

import java.util.List;

/**
 * Created by dy on 17-2-2.
 */

public class Setting_adapter extends ArrayAdapter<Setting>{
    int resourceId;
    public Setting_adapter(Context context, int textResourceId, List<Setting> objects){
        super(context,textResourceId,objects);
        resourceId = textResourceId;
    }
    public View getView(int position, View converview, ViewGroup parent){
        Setting setting = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView setting_option = (TextView) view.findViewById(R.id.setting_option);
        CheckBox Setting_chexkbox = (CheckBox) view.findViewById(R.id.setting_checkbox);
        setting_option.setText(setting.getSetting_option());
        Setting_chexkbox.setChecked(setting.getSetting_Checkbox());
        return view;
    }
}
