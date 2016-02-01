package com.wzh.superclean.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.wzh.superclean.R;
import com.wzh.superclean.bean.AppCacheInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wzh on 2015/9/24.
 */
public class CleanRubbishLVAdapter extends BaseAdapter {

    private Context mContext ;
    private LayoutInflater mLInflater ;
    private List<AppCacheInfo> mList ;

    public CleanRubbishLVAdapter(Context context,List<AppCacheInfo> data) {
        mContext = context ;
        if(data != null){
            mList = data ;
        }else{
            mList = new ArrayList<>() ;
        }
        mLInflater = LayoutInflater.from(mContext) ;
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

        if(convertView == null){
            convertView = mLInflater.inflate(R.layout.cleanrubbish_adapter_layout,null) ;
        }

        //获取控件
        ImageView appIcon = (ImageView)convertView.findViewById(R.id.cr_appicon_id) ;
        TextView appName = (TextView)convertView.findViewById(R.id.cr_appname_id) ;
        TextView appCacheSize = (TextView)convertView.findViewById(R.id.cr_appcache_id) ;
        CheckBox cb = (CheckBox)convertView.findViewById(R.id.cr_cb_id) ;
        //设置控件数据
        AppCacheInfo currentApp = mList.get(position) ;
        appIcon.setImageDrawable(currentApp.getIcon());
        appName.setText(currentApp.getApplicationName());
        //将byte(字节)转成KB(千字节)
        double temp = currentApp.getCacheSize() / 1024.0 ;
        int dd = (int)(temp * 100) ;
        temp = dd / 100.0 ;
        appCacheSize.setText(temp+"KB");
        cb.setChecked(currentApp.getCheckOr());

        return convertView;
    }
}
