package com.wzh.superclean.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by wzh on 2015/10/7.
 */
public class AppInfo {

    /**
     * 应用程序的包名
     */
    private String pkgName ;
    /**
     * 应用程序名称
     */
    private String appName ;
    /**
     * 应用程序图标
     */
    private Drawable appIcon ;
    /**
     * 应用程序版本
     */
    private String version ;
    /**
     * 应用程序包的大小
     * byte
     */
    private long pkgSize ;
    /**
     * 应用程序所属的用户id
     */
    private int uid ;

    /**
     * 应用程序可以被安装到不同的位置 , 手机内存 外部存储sd卡
     */
    private boolean inRom;

    /**
     * 应用是系统应用，还是用户应用
     * 默认是用户应用
     */
    private boolean userApp = true ;

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getPkgSize() {
        return pkgSize;
    }

    public void setPkgSize(long pkgSize) {
        this.pkgSize = pkgSize;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public boolean isInRom() {
        return inRom;
    }

    public void setInRom(boolean inRom) {
        this.inRom = inRom;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }
}
