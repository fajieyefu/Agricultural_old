package com.yiyun.sijianguan.common;

import android.app.Application;

public class App extends Application{ 
    @Override  
    public void onCreate() {   
        super.onCreate();   
        CrashHandler crashHandler = CrashHandler.getInstance();   
        //注册crashHandler   
        crashHandler.init(getApplicationContext());   
    }   
} 