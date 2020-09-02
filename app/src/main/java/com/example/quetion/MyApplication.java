package com.example.quetion;

import android.app.Application;

import com.xuexiang.xui.XUI;

/**
 * @author zlw
 * @description
 * @date 2020/8/28
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        XUI.init(this);
        XUI.debug(true);
    }
}
