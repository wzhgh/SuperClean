package com.wzh.superclean.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.wzh.superclean.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {


    private Preference pCreateShortCut ;
    /**
     * 强力清理
     */
    private Preference pPowerClear ;
    private Preference pShare ;
    private Preference pEvaluate ;
    private Preference pVersionInfo ;
    private Preference pUpdate ;
    private Preference pAbout ;

    public SettingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载preferences.xml文件
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initPreference() ;
    }

    private void initPreference(){
        pCreateShortCut = findPreference("pCreateShortCut") ;
        pCreateShortCut.setOnPreferenceClickListener(this);
        pPowerClear = findPreference("pPowerClear") ;
        pPowerClear.setOnPreferenceClickListener(this);
        pShare = findPreference("pShare") ;
        pShare.setOnPreferenceClickListener(this);
        pEvaluate = findPreference("pEvaluate") ;
        pEvaluate.setOnPreferenceClickListener(this);
        pVersionInfo = findPreference("pVersionInfo") ;
        pVersionInfo.setOnPreferenceClickListener(this);
        pUpdate = findPreference("pUpdate") ;
        pUpdate.setOnPreferenceClickListener(this);
        pAbout = findPreference("pAbout") ;
        pAbout.setOnPreferenceClickListener(this);
    }

    private void initData(){

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String pKey = preference.getKey() ;
        if("pCreateShortCut".equals(pKey)){
            createShortCut() ;
        }else if ("pPowerClear".equals(pKey)){

        }else if ("pShare".equals(pKey)){

        }else if ("pEvaluate".equals(pKey)){
            startMarket();
        }else if ("pVersionInfo".equals(pKey)){

        }else if ("pUpdate".equals(pKey)){

        }else if ("pAbout".equals(pKey)){

        }
        return false;
    }

    /**
     * 创建应用程序的快捷方式
     */
    private void createShortCut(){
        Intent shortcutIntent = new Intent() ;
        shortcutIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT") ;
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,"一键加速") ;        //设置快捷方式名称
        shortcutIntent.putExtra("duplicate",false) ;                            //设置快捷方式是否可重复
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.short_cut_icon) ;
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON,bm) ;                //设置快捷方式图标
        //设置点击快捷方式所要启动的Intent
        Intent tempIntent = new Intent() ;
        tempIntent.setAction("com.wzh.quickclear") ;
        tempIntent.addCategory("android.intent.category.DEFAULT");
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,tempIntent) ;
        getActivity().sendBroadcast(shortcutIntent);
        Toast.makeText(getActivity(),"一键加速  图标创建完成",Toast.LENGTH_SHORT).show();
    }

    public  void startMarket() {
        Uri uri = Uri.parse(String.format("market://details?id=%s", getPackageInfo(getActivity()).packageName));
        if (isIntentSafe(getActivity(), uri)) {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivity().startActivity(intent);
        }
        // 没有安装市场
        else {
            Toast.makeText(getActivity(),"无法打开应用市场",Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isIntentSafe(Activity activity, Uri uri) {
        Intent mapCall = new Intent(Intent.ACTION_VIEW, uri);
        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(mapCall, 0);
        return activities.size() > 0;
    }

    public  PackageInfo getPackageInfo(Context context) {
        PackageInfo info = null;
        try {
            String packageName = context.getPackageName();
            info = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_ACTIVITIES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

}
