package com.wzh.superclean.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.*;
import android.os.Process;

import com.wzh.superclean.R;
import com.wzh.superclean.bean.AppProcessInfo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 扫描和清理内存中的进程的服务
 */
public class CleanMemoryService extends Service {

    /**
     * 回调监听器
     */
    private OnServiceActionListener mListener ;

    private Context mContext ;

    private ActivityManager activityMgr ;

    private PackageManager packageMgr ;

    /**
     * 扫描到的正在运行的进程的app
     */
    private List<AppProcessInfo> mList ;

    private CMServiceBinder mBinder = new CMServiceBinder() ;

    public CleanMemoryService() {
    }



    /**
     * Context对象 bindService()时调用onBind()
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder ;
    }

    /**
     * 创建Service时调用
     */
    @Override
    public void onCreate() {
        //得到全局的Context，该Context的生命周期跟程序的进程相关
        mContext = getApplicationContext() ;

        //ActivityManager可以得到app进程信息：如processName, pid,uid等...
        activityMgr = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE) ;
        //PackageManager 可以到的某个应用程序的信息(即Application信息)，如程序的图标，程序名称等
        packageMgr = getApplicationContext().getPackageManager() ;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 启动扫描，开始运行扫描任务
     * 扫描正在运行的进程
     */
    public void launchScanTask(){
        new ScanTask().execute() ;
    }

    /**
     * 启动清理，开始运行清理任务
     * 清理正在运行的进程
     */
    public void launchCleanTask(){
        //清理正在运行的程序
        new CleanTask().execute() ;
    }

    /**
     * 设置回调监听器，需要外界提供回调监听器的具体实现
     * @param listener
     */
    public void setServiceListener(OnServiceActionListener listener){
        mListener = listener ;
    }

    /**
     * 扫描正在运行的进程，继承异步任务类(AsyncTask)
     * 是扫描模块的核心代码
     */
    private class ScanTask extends AsyncTask<Void,Integer,List<AppProcessInfo>>{
        /**
         * app进程的个数
         */
        private Integer mAppProcessCount = 0 ;

        @Override
        protected void onPreExecute() {
            if(mListener != null){
                mListener.onScanStart(CleanMemoryService.this);
            }
        }

        @Override
        protected List<AppProcessInfo> doInBackground(Void... params) {
            mList = new ArrayList<>() ;
            ApplicationInfo appInfo = null ;
            AppProcessInfo myAppProcessInfo = null ;
            //获取正在运行的所有的app进程
            List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityMgr.getRunningAppProcesses() ;

            //调用AsyncTask的publishProgress()，实时更新数据，该方法会回调onProgressUpdate()方法
            publishProgress(0,appProcessInfoList.size());

            for(ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfoList){
                publishProgress(++mAppProcessCount,appProcessInfoList.size());
                myAppProcessInfo = new AppProcessInfo(appProcessInfo.processName , appProcessInfo.pid,appProcessInfo.uid) ; ;

                try {
                    //我们要想得到应用程序的信息(app的信息)，就要用到PackageManager，调用getApplicationInfo()，返回ApplicationInfo对象
                    //该对象封装应用程序的各种信息，如：程序图标，程序名称等
                    //getApplicationInfo()的第一个参数的packagename,包名默认就是进程名称：即appProcessInfo.processName  可得到
                    appInfo = packageMgr.getApplicationInfo(appProcessInfo.processName,0) ;
                    //判断程序是否为系统内置程序(ApplicationInfo.FLAG_SYSTEM是否安装在系统镜像里)
                    if(appInfo.flags == ApplicationInfo.FLAG_SYSTEM){
                        myAppProcessInfo.isSystem = true ;
                    }else{
                        myAppProcessInfo.isSystem = false ;
                    }
                    //得到程序的图标和名称
                    Drawable appIcon = appInfo.loadIcon(packageMgr) ;
                    String appName = appInfo.loadLabel(packageMgr).toString() ;
                    myAppProcessInfo.icon = appIcon ;
                    myAppProcessInfo.appName = appName ;

                } catch (PackageManager.NameNotFoundException e) {
                    //可能会进程是这样的：com.wzh.superclean:nnn (这是一个私有进程，可独立于进程com.wzh.superclean)
                    //使用属性android:process=":nnn"指定
                    //他确实是一个新进程，但对进程com.wzh.superclean是私有的，其它的进程不能使用它；进程com.wzh.superclean被干掉，它不会受影响；
                    //我们需要得到的是该进程对应的应用程序信息 ：即com.wzh.superclean对应的应用程序信息
                    //所以判断processName是否包含":"
                    if(appProcessInfo.processName.indexOf(":") != -1){
                        //使用Context的getApplicationInfo()
                        appInfo =  getInstalledApplicationInfo(appProcessInfo.processName.split(":")[0]) ;
                        if(appInfo != null){
                            //可能是这样的com.qq.ztone:push
                            myAppProcessInfo.icon = appInfo.loadIcon(packageMgr) ;
                        }else{
                            myAppProcessInfo.icon = getResources().getDrawable(R.drawable.ic_launcher) ;
                        }
                    }else{      //有可能随便定义的进程名称：com.jjjj.ddd，这样是找不到的
                        myAppProcessInfo.icon = getResources().getDrawable(R.drawable.ic_launcher) ;
                    }
                    myAppProcessInfo.appName = appProcessInfo.processName ;
                }

                //判断进程的uid，判断是否为系统进程(Process.SYSTEM_UID，Process.ROOT_UID(0,hide))
                myAppProcessInfo.uid = appProcessInfo.uid ;
                if(appProcessInfo.uid == Process.SYSTEM_UID){
                    myAppProcessInfo.processUser = "System用户" ;
                }else if(appProcessInfo.uid == 0){
                    myAppProcessInfo.processUser = "ROOT用户" ;
                }else{

                }

                //ActivityManager的getProcessMemoryInfo()方法可以得到进程占用的内存信息
                Debug.MemoryInfo[] memoryInfos = activityMgr.getProcessMemoryInfo(new int[]{appProcessInfo.pid});
                //获取进程占用的内存大小，getTotalPrivateDirty()返回的KB
                // (进程占用的内存分为多种,具体可查看Debug.MemoryInfo类)
                long memorySize = memoryInfos[0].getTotalPrivateDirty() ;
                myAppProcessInfo.memory = memorySize ;
                mList.add(myAppProcessInfo) ;
            }
            return mList;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if(mListener != null){
                 mListener.onScanUpdate(CleanMemoryService.this,values[0],values[1]);
            }
        }

        @Override
        protected void onPostExecute(List<AppProcessInfo> result) {
            if(mListener != null){
                mListener.onScanComplete(CleanMemoryService.this,result);
            }
        }
    }

    /**
     * 清理正在运行的进程，继承异步任务类(AsyncTask)
     * 是清理进程模块的核心代码
     */
    private class CleanTask extends AsyncTask<Void,Void,Long>{

        @Override
        protected void onPreExecute() {
            if(mListener != null){
                mListener.onCleanStart(CleanMemoryService.this);
            }
        }

        @Override
        protected Long doInBackground(Void... params) {
            long beforeMemory = 0 ;
            //清理所有进程后的可用内存的大小
            long endMemory = 0 ;
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo() ;
            //getMemoryInfo()方法：获取android当前的内存信息
            activityMgr.getMemoryInfo(memoryInfo);
            //availMem 字段 得到当前可用内存的大小
            beforeMemory = memoryInfo.availMem ;

            List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityMgr.getRunningAppProcesses() ;
            for(ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfoList){
                //杀死进程
                forecKillBackgroundProcesses(appProcessInfo.processName);
            }
            activityMgr.getMemoryInfo(memoryInfo);
            endMemory = memoryInfo.availMem ;
            return endMemory - beforeMemory;
        }

        @Override
        protected void onPostExecute(Long result) {
            if(mListener != null){
                mListener.onCleanComplete(CleanMemoryService.this,result);
            }
        }
    }

    /**
     * 得到系统当前的可用内存
     * @return 可用内存  单位为KB
     */
    public long getSystemCurrentAvailMemory(){
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo() ;
        activityMgr.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem ;
    }

    /**
     * 得到安装的应用程序的app info
     * 如果该进程是私有进程（如这个样子：appProcessInfo.processName=com.baidu.map.location:bbb）
     * 而进程com.baidu.map.location确被干掉了，那么使用packageMgr.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES)
     * 然后遍历，得到该该进程对应的ApplicationInfo
     * @param processName
     * @return
     */
    public ApplicationInfo getInstalledApplicationInfo( String processName) {
        if (processName == null) {
            return null;
        }
        //getInstalledApplications() ，得到安装的应用程序信息
        //flages 如果是GET_UNINSTALLED_PACKAGES，那么将返回所有安装的应用程序的信息
        List<ApplicationInfo> appList = packageMgr
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (ApplicationInfo appInfo : appList) {
            if (processName.equals(appInfo.processName)) {
                return appInfo;
            }
        }
        return null;
    }

    /**
     * 强力杀死后台进程
     * 反射调用
     * @param processName
     */
    public void forecKillBackgroundProcesses(String processName) {
        // mIsScanning = true;

        String packageName = null;
        try {
            //包名不一定等于进程名，如：processName为 com.baidu.map.location:remote (这是个服务进程，其包名为com.baidu.map.location)
            if (processName.indexOf(":") == -1) {
                packageName = processName;
            } else {
                packageName = processName.split(":")[0];
            }
            //ActivityManager的killBackgroundProcesses()：根据api解释，杀死进程后系统有可能会重启该进程，有可能没有完全杀死
            //这时就需要调用ActivityManager的forceStopPackage() 方法  他是终极杀器
            activityMgr.killBackgroundProcesses(packageName);

            //ActivityManager的forceStopPackage(String packageName)方法可以杀死packageName进程相关的所有东西
            //可以杀死包括所有相关的Acitivy 、Service、notifications、alarms(警报)，简直是万能杀进程方法
            //但是需要一定的权限android.permission.FORCE_STOP_PACKAGES和清单<manifest /> 上添加android:sharedUserId="android.uid.system"
            Method forceStopPackage = activityMgr.getClass()
                    .getDeclaredMethod("forceStopPackage", String.class);
            forceStopPackage.setAccessible(true);
            forceStopPackage.invoke(activityMgr, packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 扫描和清理进程的监听器
     * Service的回调接口
     */
    public static interface OnServiceActionListener{
        /**
         * 扫描准备开始
         * @param context
         */
        public void onScanStart(Context context) ;

        /**
         * 扫描更新
         * @param context
         * @param current   当前扫描到的进程
         * @param max       总共的进程数
         */
        public void onScanUpdate(Context context,Integer current,Integer max) ;

        /**
         * 扫描完成
         * @param context
         */
        public void onScanComplete(Context context,List<AppProcessInfo> info) ;

        /**
         * 清理准备开始
         * @param context
         */
        public void onCleanStart(Context context);

        /**
         * 清理完成
         */
        public void onCleanComplete(Context context,Long cacheSize) ;
    }

    public class CMServiceBinder extends Binder{
        //将CleanMemoryService 对象暴露出去
        public CleanMemoryService getServiceInstance(){
            return CleanMemoryService.this ;
        }
    }

    private void testMethod(){
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("",null) ;
        ContentValues cv = new ContentValues() ;

        db.insert("UserTable",null,null) ;

        Cursor cs = db.query("note", null, null, null, null, null, null) ;
        cs.close();

        Looper.prepare();
        Handler hd = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        } ;

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                //执行代码
            }
        }) ;
        t1.start();


    }


}
