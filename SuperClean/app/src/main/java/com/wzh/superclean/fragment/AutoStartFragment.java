package com.wzh.superclean.fragment;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.wzh.superclean.R;
import com.wzh.superclean.adapter.AutoStartLVAdapter;
import com.wzh.superclean.bean.AutoStartAppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AutoStartFragment extends Fragment {

    private View mView ;
    /**
     * 0:普通自启软件，1:系统自启软件
     */
    private int position  ;

    private ListView mListView ;
    private TextView mHint ;
    private Button mOptimizeBtn ;
    private AutoStartLVAdapter mAdapter ;
    private Context mContext ;
    private List<AutoStartAppInfo> mList = null ;
    /**
     * 自启的系统软件
     */
    private List<AutoStartAppInfo> isSystemAutoList = null ;
    /**
     * 自启的非系统软件，即普通的自启软件
     */
    private List<AutoStartAppInfo> noSystemAutoList = null ;

    public AutoStartFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments() ;
        position = arguments.getInt("position") ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_auto_start, container, false);
        mContext = getActivity() ;
        return mView ;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView() ;
        fillData() ;
    }

    /**
     * 初始化View
     */
    private void initView(){
        mListView = (ListView)mView.findViewById(R.id.auto_start_listview_id) ;
        mHint = (TextView)mView.findViewById(R.id.auto_start_hint_id) ;
        mOptimizeBtn = (Button)mView.findViewById(R.id.auto_start_btn_id) ;

//        mList = new ArrayList<>() ;
//        mAdapter = new AutoStartLVAdapter(mContext,mList) ;
//        mListView.setAdapter(mAdapter);

        mOptimizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.auto_start_btn_id){
                    //禁止自启

                }
            }
        });
        if (position == 0) {
            mHint.setText("禁止下列软件自启,可提升运行速度");
            mOptimizeBtn.setVisibility(View.VISIBLE);
        } else {
            mHint.setText("禁止系统核心软件自启,将会影响手机的正常使用,请谨慎操作");
            mOptimizeBtn.setVisibility(View.GONE);
        }
    }

    /**
     * 填充数据
     */
    private void fillData(){
        mList = fetchAutoStartApps(mContext);
        isSystemAutoList = new ArrayList<>() ;
        noSystemAutoList = new ArrayList<>() ;
        for(AutoStartAppInfo appInfo : mList){
            if(appInfo.getIsSystem()){
                isSystemAutoList.add(appInfo) ;
            }else{
                noSystemAutoList.add(appInfo) ;
            }
        }
        if(position == 0){
            mAdapter = new AutoStartLVAdapter(mContext,noSystemAutoList) ;
            mListView.setAdapter(mAdapter);
        }else{
            mAdapter = new AutoStartLVAdapter(mContext,isSystemAutoList) ;
            mListView.setAdapter(mAdapter);
        }
    }

    /**
     * 获取自启的app
     * @param context
     * @return
     */
    private List<AutoStartAppInfo> fetchAutoStartApps(Context context){
        List<AutoStartAppInfo> appList = new ArrayList<>() ;
        PackageManager pm = context.getPackageManager() ;
        Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED) ;
        //queryBroadcastReceivers()：检索响应指定意图的广播；我们这里是获取响应开机启动意图(Intent.ACTION_BOOT_COMPLETED)的广播
        List<ResolveInfo> resolveInfoList =  pm.queryBroadcastReceivers(intent, PackageManager.GET_DISABLED_COMPONENTS);
        for(ResolveInfo resolveInfo : resolveInfoList){
            AutoStartAppInfo appInfo = new AutoStartAppInfo() ;
            appInfo.setAppName(resolveInfo.loadLabel(pm).toString())  ;
            appInfo.setIcon(resolveInfo.loadIcon(pm));
            appInfo.setPackageName(resolveInfo.activityInfo.packageName);

            //判断app是否为系统app
            if((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0){
                appInfo.setIsSystem(true);
            }else{
                appInfo.setIsSystem(false);
            }

            //判断组件是什么状态
            ComponentName component = new ComponentName(resolveInfo.activityInfo.packageName,resolveInfo.activityInfo.name) ;
            if(pm.getComponentEnabledSetting(component) == PackageManager.COMPONENT_ENABLED_STATE_DISABLED){
                appInfo.setIsEnable(false);
            }else{
                appInfo.setIsEnable(true);
            }
            appList.add(appInfo) ;
        }
        return appList ;
    }
}
