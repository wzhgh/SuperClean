package com.wzh.superclean.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StatFs;

import com.wzh.superclean.bean.AppCacheInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class CleanRubbishService extends Service {

    private PackageManager mPkgMgr ;
    private Method getPkgSizeInfoMethod , freeStorageAndNotifyMethod ;
    private CRServiceBinder mBinder = new CRServiceBinder() ;
    private OnServiceActionListener mListener ;
    private long mCacheSize ;
    private Integer appUpdateCount = 0 ;

    public CleanRubbishService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder ;
    }

    @Override
    public void onCreate() {
        mPkgMgr = getApplicationContext().getPackageManager() ;
        try {
            //需要调用PackageManager重点2个hide方法：getPackageSizeInfo[得到包的大小]、freeStorageAndNotify[释放包的缓存]
            //用反射方法得到这2个方法
            getPkgSizeInfoMethod = mPkgMgr.getClass().getMethod("getPackageSizeInfo",String.class, IPackageStatsObserver.class) ;
            freeStorageAndNotifyMethod = mPkgMgr.getClass().getMethod("freeStorageAndNotify",long.class, IPackageDataObserver.class) ;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动扫描
     * 扫描程序垃圾(缓存)
     */
    public void launchScanTask(){
        new ScanTask().execute() ;
    }

    /**
     * 启动清理
     * 清理程序垃圾(缓存)
     */
    public void launchCleanTask(){
        new CleanTask().execute() ;
    }

    /**
     * 扫描app的缓存
     */
    private class ScanTask extends AsyncTask<Void,Integer,List<AppCacheInfo>>{

        @Override
        protected void onPreExecute() {
            if(mListener != null){
                mListener.onScanStart(CleanRubbishService.this);
            }
        }

        @Override
        protected List<AppCacheInfo> doInBackground(Void... params) {
            mCacheSize = 0 ;
            appUpdateCount = 0 ;
            final List<ApplicationInfo> appList = mPkgMgr.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES) ;
            //更新数据
            publishProgress(appUpdateCount,appList.size());
            final List<AppCacheInfo> appCacheList = new ArrayList<>() ;
            //使用计数锁CountDownLatch，根据需求来阻塞线程
            final CountDownLatch countDownLatch = new CountDownLatch(appList.size());
            try {
                for(ApplicationInfo appInfo : appList){
                    getPkgSizeInfoMethod.invoke(mPkgMgr,appInfo.packageName,
                            new IPackageStatsObserver.Stub(){
                                @Override
                                public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                                    //这里要确保单线程，所以要上锁
                                    synchronized (appList) {
                                        //更新数据
                                        publishProgress(++appUpdateCount, appList.size());

                                        if (succeeded && pStats.cacheSize > 0) {
                                            try {
                                                AppCacheInfo appCache = new AppCacheInfo();
                                                appCache.setPackageName(pStats.packageName);
                                                appCache.setCacheSize(pStats.cacheSize);

                                                String appLabel = mPkgMgr.getApplicationLabel(mPkgMgr.getApplicationInfo(pStats.packageName, 0)).toString();
                                                appCache.setApplicationName(appLabel);

                                                Drawable appIcon = mPkgMgr.getApplicationIcon(pStats.packageName);
                                                appCache.setIcon(appIcon);

                                                appCacheList.add(appCache);

                                                mCacheSize += pStats.cacheSize;
                                            } catch (PackageManager.NameNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        //计数减1
                                        countDownLatch.countDown();
                                    }
                                }
                            }) ;
                }
                //阻塞线程，因为回调IPackageStatsObserver的方法是异步的，有多线程操作；
                //确保方法回调后，主程序才能继续往下执行，否则主程序就一直等待
                countDownLatch.await();
            } catch (IllegalAccessException | InvocationTargetException | InterruptedException e) {
                e.printStackTrace();
            }
            return appCacheList;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if(mListener != null){
                mListener.onScanUpdate(CleanRubbishService.this,values[0],values[1]);
            }
        }

        @Override
        protected void onPostExecute(List<AppCacheInfo> infoList) {
            if(mListener != null){
                mListener.onScanComplete(CleanRubbishService.this,infoList);
            }
        }
    }

    private class CleanTask extends AsyncTask<Void,Void,Long>{
        @Override
        protected void onPreExecute() {
            if(mListener != null){
                mListener.onCleanStart(CleanRubbishService.this);
            }
        }

        @Override
        protected Long doInBackground(Void... params) {
            //StatFs ,封装文件系统空间的信息，需要一个path
            //Environment.getDataDirectory()： 得到用户数据目录 （File对象）
            StatFs sf = new StatFs(Environment.getDataDirectory().getAbsolutePath()) ;
            //getBlockCountLong()  得到块的个数 (api 18用sf.getBlockCountLong())
            int blockCount = sf.getBlockCount() ;
            //getBlockSizeLong() ：得到块的大小(单位是byte(字节))    (api 18用sf.getBlockSizeLong())
            long blockSize = (long)sf.getBlockSize() ;

            long userDataSize = blockCount * blockSize ;
            //用CountDownLatch来阻塞线程，初始化计数为1
            //因为回调IPackageDataObserver类是异步的，有多线程操作，主线程要必须等到起onRemoveCompleted()方法回调后，才能继续往下执行
            final CountDownLatch countLatch = new CountDownLatch(1) ;
            try {
                //释放缓存，释放的不是mCacheSize
                freeStorageAndNotifyMethod.invoke(mPkgMgr,userDataSize,new IPackageDataObserver.Stub(){
                    @Override
                    public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                        //计数减1
                        countLatch.countDown();
                    }
                }) ;
             //阻塞线程，直到计数为0时，线程才往下执行
             //这个意思就是，直到onRemoveCompleted()方法被回调后，代码才能继续往下执行，否则就一直线程就一直等待
             countLatch.wait();
            } catch (IllegalAccessException | InvocationTargetException |InterruptedException e) {
                e.printStackTrace();
            }
            return 0L ;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            if(mListener != null){
                mListener.onCleanComplete(CleanRubbishService.this,aLong);
            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class CRServiceBinder extends Binder{
        public CleanRubbishService getServiceInstance(){
            return CleanRubbishService.this ;
        }
    }

    /**
     * 返回的数据的单位是Byte(字节)
     * @return
     */
    public long getTotalCacheSize(){
        return mCacheSize ;
    }

    public void setOnServiceListener(OnServiceActionListener listener){
        mListener = listener ;
    }

    /**
     * Service的回调监听器
     */
    public static interface OnServiceActionListener{
        /**
         * 扫描准备开始
         */
        public void onScanStart(Context context) ;

        /**
         * 扫描更新
         */
        public void onScanUpdate(Context context,Integer current,Integer max) ;

        /**
         * 扫描完成
         */
        public void onScanComplete(Context context,List<AppCacheInfo> info) ;

        /**
         * 清理准备开始
         */
        public void onCleanStart(Context context);

        /**
         * 清理完成
         */
        public void onCleanComplete(Context context,Long cacheSize) ;
    }
}
