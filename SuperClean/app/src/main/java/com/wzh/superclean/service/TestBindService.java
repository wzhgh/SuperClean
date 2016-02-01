package com.wzh.superclean.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class TestBindService extends Service {

    private Integer state = 0 ;
    //通过该对象拿到Service中的数据
    private MyBinder binder = new MyBinder() ;

    public TestBindService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v("TestBindService", "TestBindService is binded") ;
        //intent参数可以应用程序传递过来的数据

        //将binder对象暴露出去
        return binder ;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("TestBindService","TestBindService  is created") ;
    }

    /**
     * 当Service上的所有的客户端都断开连接时，回调该方法
     * @param intent
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Log.v("TestBindService","TestBindService is unBinded") ;
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("TestBindService","TestBindService is destroyed") ;
    }

    public class MyBinder extends Binder{
        public Integer getState(){
            return  state;
        }
    }
}
