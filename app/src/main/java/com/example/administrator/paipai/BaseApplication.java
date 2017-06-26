package com.example.administrator.paipai;

import android.util.Log;

import com.lzy.okgo.OkGo;

import org.litepal.LitePalApplication;

/**
 * Created by Administrator on 2017/6/7.
 */
public class BaseApplication extends LitePalApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        OkGo.init(this);
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        Log.d("BaseApplication", "onTerminate");

        super.onTerminate();

    }
}
