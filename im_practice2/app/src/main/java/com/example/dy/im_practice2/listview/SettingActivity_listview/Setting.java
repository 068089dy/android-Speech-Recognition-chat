package com.example.dy.im_practice2.listview.SettingActivity_listview;

/**
 * Created by dy on 17-2-2.
 */

public class Setting {
    private String Setting_option;
    private Boolean Setting_Checkbox;
    public Setting(String Setting_option,Boolean SettingCheckbox){
        this.Setting_Checkbox = SettingCheckbox;
        this.Setting_option = Setting_option;
    }

    public String getSetting_option(){
        return Setting_option;
    }
    public Boolean getSetting_Checkbox(){
        return Setting_Checkbox;
    }
}
