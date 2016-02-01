package com.wzh.superclean.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TestService extends Service {
    public TestService() {
    }

    /**
     * 所有Service子类必须实现onBind()，应用程序可通过其onBind()发回的IBinder对象于该Srvice进行通信
     * 如果是Context.startService()来启动Service ，程序是不需要与Service通信的，onBind()不会被调用
     * 那么就让onBind()返回null
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.v("TestService","TestService is binded") ;
        return null ;
    }

    /**
     * 创建Service时调用，只调用一次
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("TestService","TestService  is created") ;
    }

    /**
     * 每次启动Service时调用(每次startService()时回调该方法)
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //这里的intent，可以传递数据给Service
        //该方法可在Service中让服务停止自己
//        stopSelf();

        Log.v("TestService","TestService is started") ;
        return super.onStartCommand(intent, flags, startId);
    }



    /**
     * 当Service被关闭之前，回调该方法
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("TestService","TestService is destroyed") ;
    }

}
