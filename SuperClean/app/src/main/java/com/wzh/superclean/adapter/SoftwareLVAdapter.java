package com.wzh.superclean.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wzh.superclean.R;
import com.wzh.superclean.bean.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wzh on 2015/10/7.
 */
public class SoftwareLVAdapter extends BaseAdapter {

    private Context mContext ;
    private List<AppInfo> mAppInfoList ;
    private LayoutInflater mInflater ;

    public SoftwareLVAdapter(Context context , List<AppInfo> list) {
        mContext = context ;
        mInflater = LayoutInflater.from(context) ;
        if(list != null){
            mAppInfoList = list ;
        }else{
            mAppInfoList = new ArrayList<>() ;
        }

    }

    @Override
    public int getCount() {
        return mAppInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAppInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null ;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.software_listview_layout,null) ;
            holder = new ViewHolder() ;
            holder.appIcon = (ImageView)convertView.findViewById(R.id.software_app_icon_id) ;
            holder.appName = (TextView)convertView.findViewById(R.id.software_app_name_id) ;
            holder.appSize = (TextView)convertView.findViewById(R.id.software_app_size_id) ;
            holder.uninstallBtn = (Button)convertView.findViewById(R.id.software_uninstall_id) ;

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag() ;
        }

        final AppInfo currentApp = mAppInfoList.get(position) ;
        holder.appIcon.setImageDrawable(currentApp.getAppIcon());
        holder.appName.setText(currentApp.getAppName());

        double pkgSize = convertByteToKB(currentApp.getPkgSize()) ;
        holder.appSize.setText(pkgSize+"KB");

        holder.uninstallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用系统的卸载程序的Activity
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setAction("android.intent.action.DELETE");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + currentApp.getPkgName()));
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    /**
     * 将byte(字节)转成KB(千字节)
     * @param value
     */
    private double convertByteToKB(long value){
        double temp = value / 1024.0 ;
        int dd = (int)(temp * 100) ;
        temp = dd / 100.0 ;
        return temp ;
    }

    class ViewHolder{
        ImageView appIcon;
        TextView appName;
        TextView appSize ;
        Button uninstallBtn ;
    }


}
