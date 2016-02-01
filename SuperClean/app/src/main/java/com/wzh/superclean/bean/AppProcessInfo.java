package com.wzh.superclean.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by wzh on 2015/9/17.
 */
public class AppProcessInfo {


    /**
     * The app name.
     */
    public String appName;

    /**
     * The name of the process that this object is associated with.
     */
    public String processName;

    /**
     * The pid of this process; 0 if none.
     */
    public int pid;

    /**
     * The user id of this process.
     */
    public int uid;

    /**
     * uid对应的用户，默认设置wzhPhone
     * 如uid == Process.ROOT_UID  表明该进程是ROOT用户的进程
     *   uid == Process.SYSTEM_UID  表明该进程是System用户的进程
     */
    public String processUser = "wzh用户";

    /**
     * The icon.
     */
    public Drawable icon;

    /**
     * 占用的内存(单位KB)
     */
    public long memory;

    /**
     * 占用的内存.
     */
    public String cpu;

    /**
     * 进程的状态，其中S表示休眠，R表示正在运行，Z表示僵死状态，N表示该进程优先值是负数.
     */
    public String status;

    /**
     * 当前使用的线程数.
     */
    public String threadsCount;


    public boolean checked = true;

    /**
     * 是否是系统进程.
     */
    public boolean isSystem = false;

    //符合bean的java类，必须有个无参的构造器，get和set方法获取属性
    public AppProcessInfo() {
    }

    public AppProcessInfo(String processName , int pid , int uid){
        //需要手动调用无参构造器
        this.processName = processName ;
        this.pid = pid ;
        this.uid = uid ;
    }

}
