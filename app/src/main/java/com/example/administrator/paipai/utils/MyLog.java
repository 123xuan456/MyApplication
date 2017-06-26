package com.example.administrator.paipai.utils;

import android.util.Log;

/**
 * Created by liguo on 2016/12/29.
 */

public class MyLog {
    //打印开关 为了程序运行速度，发布时关闭Log打印
    private static boolean isEnableLog = true;
    //封装Log
    public static void i(String tag, String msg){
        if (isEnableLog) Log.i(tag,msg);
    }
}
