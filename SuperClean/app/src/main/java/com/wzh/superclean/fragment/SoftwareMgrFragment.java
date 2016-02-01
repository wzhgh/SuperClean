package com.wzh.superclean.fragment;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.wzh.superclean.R;
import com.wzh.superclean.adapter.SoftwareLVAdapter;
import com.wzh.superclean.bean.AppInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * A simple {@link Fragment} subclass.
 */
public class SoftwareMgrFragment extends Fragment {
    private View mView ;
    private int position = 0 ;
    private ListView mListView ;
    private TextView mHint ;
    private Context mContext ;
    private PackageManager mPkgMgr ;

    AsyncTask<Void,Integer,List<AppInfo>> mTask ;
    private List<AppInfo> userAppList = null;
    private List<AppInfo> systemAppList = null ;
    private SoftwareLVAdapter softwareLVAdapter ;

    /**
     * 使用反射，调用PackageManager 的  getPkgSizeInfo()得到包的大小
     */
    private Method getPkgSizeInfoMethod ;

    public SoftwareMgrFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bd = getArguments() ;
        position = bd.getInt("position") ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_software_mgr, container, false);
        mContext = getActivity() ;
        mPkgMgr= mContext.getPackageManager() ;
        try {
            getPkgSizeInfoMethod = mPkgMgr.getClass().getMethod("getPackageSizeInfo",String.class, IPackageStatsObserver.class) ;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return mView ;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView() ;
        fillData() ;
    }

    /**
     * 初始化view
     */
    private void initView(){
        mListView = (ListView)mView.findViewById(R.id.software_mgr_lv_id) ;
        mHint = (TextView)mView.findViewById(R.id.software_mgr_hint_id) ;

        if (position == 0) {
            mHint.setText("可卸载这些软件");
        } else {
            mHint.setText("这些是系统软件，要卸载需要ROOT权限");
        }
    }

    /**
     * 填充数据
     */
    private void fillData(){
        initTask() ;
        mTask.execute() ;
    }

    /**
     * 初始化任务
     * 扫描已安装的软件，包括系统软件
     */
    private void initTask(){
        mTask = new AsyncTask<Void,Integer,List<AppInfo>>(){

            private int mAppCount = 0;

            @Override
             protected void onPreExecute() {
                super.onPreExecute();
                mHint.setText("开始扫描！！");
            }

            @Override
            protected List<AppInfo> doInBackground(Void... params) {
                List<PackageInfo> packInfoList = mPkgMgr.getInstalledPackages(0) ;
                List<AppInfo> appInfoList = new ArrayList<>() ;
                //更新信息
                publishProgress(0,packInfoList.size());

                //计数锁
                final CountDownLatch countDownLatch = new CountDownLatch(packInfoList.size());

                try {
                    for(PackageInfo packageInfo : packInfoList){
                        //更新信息
                        publishProgress(++mAppCount,packInfoList.size());

                        final AppInfo appInfo = new AppInfo() ;

                        String packageName = packageInfo.packageName ;
                        appInfo.setPkgName(packageName);

                        String appName = packageInfo.applicationInfo.loadLabel(mPkgMgr).toString() ;
                        appInfo.setAppName(appName);

                        Drawable appIcon = packageInfo.applicationInfo.loadIcon(mPkgMgr) ;
                        appInfo.setAppIcon(appIcon);

                        String version = packageInfo.versionName ;
                        appInfo.setVersion(version);

                        int uid = packageInfo.applicationInfo.uid ;
                        appInfo.setUid(uid);

                        int flags = packageInfo.applicationInfo.flags ;
                        if((flags & ApplicationInfo.FLAG_SYSTEM) != 0){
                            //系统应用
                            appInfo.setUserApp(false);
                        }else{
                            //用户应用
                            appInfo.setUserApp(true);
                        }
                        if((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0){
                            //安装在扩展卡(sd卡)里
                            appInfo.setInRom(false);
                        }else{
                            //安装在手机储存里
                            appInfo.setInRom(true);
                        }

                        //得到包的大小
                        getPkgSizeInfoMethod.invoke(mPkgMgr,packageName,new IPackageStatsObserver.Stub() {
                            @Override
                            public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                                synchronized(appInfo){
                                    appInfo.setPkgSize(pStats.cacheSize+pStats.codeSize+pStats.dataSize);
                                    //计数减1
                                    countDownLatch.countDown();
                                }
                            }
                        }) ;

                        appInfoList.add(appInfo) ;
                    }
                    //阻塞线程
                    countDownLatch.await();
                } catch (IllegalAccessException | InvocationTargetException | InterruptedException e) {
                    e.printStackTrace();
                }

                return appInfoList;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                mHint.setText("已扫描到："+values[0]+"/"+values[1]);
            }

            @Override
            protected void onPostExecute(List<AppInfo> result) {
                userAppList = new ArrayList<>() ;
                systemAppList = new ArrayList<>() ;
                for(AppInfo appInfo : result){
                      if(appInfo.isUserApp()){
                          userAppList.add(appInfo) ;
                      }else{
                          systemAppList.add(appInfo) ;
                      }
                }

                if(position == 0){
                    mHint.setText("已安装"+userAppList.size()+"款软件");
                    softwareLVAdapter = new SoftwareLVAdapter(mContext,userAppList) ;
                }else{
                    softwareLVAdapter = new SoftwareLVAdapter(mContext,systemAppList) ;
                }
                mListView.setAdapter(softwareLVAdapter);
            }
        } ;
    }

    @Override
    public void onResume() {
        super.onResume();
        fillData() ;
    }

    @Override
    public void onPause() {
        super.onPause();
        mTask.cancel(true) ;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
