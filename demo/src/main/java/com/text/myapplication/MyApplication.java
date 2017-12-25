package com.text.myapplication;

import android.app.Application;

import com.text.updatademo.utils.CrashHandler;

/**
 * 类名：com.text.myapplication
 * 时间：2017/12/6 21:08
 * 描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 * @author Liu_xg
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        CrashHandler crashHandler = CrashHandler.getsInstance();
        crashHandler.init(getApplicationContext(),
                Constants.APPID,
                Constants.COMMON_URL,
                "XXX");


    }
}
