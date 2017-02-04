package com.example.dy.im_practice2.data;

import android.os.FileObserver;
import android.util.Log;

/**
 * Created by dy on 12/18/16.
 */

public class FileListener extends FileObserver {

    public FileListener(String path) {
        /*
         * 这种构造方法是默认监听所有事件的,如果使用 super(String,int)这种构造方法，
         * 则int参数是要监听的事件类型.
         */
        super(path);
    }

    @Override
    public void onEvent(int event, String path) {
        switch (event) {
            case FileObserver.ALL_EVENTS:
                Log.d("all", "path:" + path);
                break;
            case FileObserver.CREATE:
                Log.d("Create", "path:" + path);
                break;
            case FileObserver.MODIFY:
                Log.d("modify","path"+path);
                break;
        }
    }
}
