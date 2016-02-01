package com.wzh.superclean.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by wzh on 2015/9/22.
 */
public class AppCacheInfo {

    private String packageName , applicationName ;
    private long cacheSize ;
    private Drawable icon ;
    private boolean checkOr = true ;



    public AppCacheInfo() {

    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String mPackageName) {
        this.packageName = mPackageName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean getCheckOr() {
        return checkOr;
    }

    public void setCheckOr(boolean checkOr) {
        this.checkOr = checkOr;
    }
}
