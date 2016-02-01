package com.wzh.superclean.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.wzh.superclean.R;
import com.wzh.superclean.bean.AppProcessInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wzh on 2015/9/19.
 */
public class ClearMemoryAdapter extends BaseAdapter {

    private Context mContext ;
    private LayoutInflater mLInflater ;
    private List<AppProcessInfo> mList ;

    public ClearMemoryAdapter(Context context,List<AppProcessInfo> data) {
        mContext = context ;
        mLInflater = LayoutInflater.from(context) ;
        if(data != null){
            mList = data ;
        }else{
            mList = new ArrayList<>() ;
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //这样赋给mView，不够好；要使用convertView来接收才好
//        mView =  mLInflater.inflate(R.layout.clearmemory_adapter_layout,null) ;

        if(convertView == null){
            convertView = mLInflater.inflate(R.layout.clearmemory_adapter_layout,null) ;
        }
        //获取控件
        ImageView appIcon = (ImageView)convertView.findViewById(R.id.cm_app_icon_id) ;
        TextView appName = (TextView)convertView.findViewById(R.id.cm_appname_id) ;
        TextView memorySize = (TextView)convertView.findViewById(R.id.cm_memorysize_id) ;
        RadioButton rb = (RadioButton)convertView.findViewById(R.id.cm_rb_id) ;

        TextView appSystem = (TextView)convertView.findViewById(R.id.appSystem_id) ;
        TextView processUser = (TextView)convertView.findViewById(R.id.process_user_id) ;

        AppProcessInfo processInfo = mList.get(position) ;
        appIcon.setImageDrawable(processInfo.icon);
        appName.setText(processInfo.appName);
        memorySize.setText(processInfo.memory+"KB");
        rb.setChecked(processInfo.checked);

        if(processInfo.isSystem){
            appSystem.setText("系统进程");
        }else{
            appSystem.setText("未知");
        }
        processUser.setText(processInfo.processUser);

        return convertView;
    }
}
