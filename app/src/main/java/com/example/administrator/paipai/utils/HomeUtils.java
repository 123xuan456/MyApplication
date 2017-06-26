package com.example.administrator.paipai.utils;

/**
 * Created by Administrator on 2017/6/15.
 * 单例模式
 */
public class HomeUtils {
    public static HomeUtils getInstance(){
        return HomeUtilHolder.INSTANCE;
    }
    private static class HomeUtilHolder{
        static final HomeUtils INSTANCE=new HomeUtils();
    }
    private HomeUtils(){
    }

}
