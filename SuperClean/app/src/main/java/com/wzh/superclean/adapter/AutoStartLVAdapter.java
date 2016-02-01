package com.wzh.superclean.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wzh.superclean.R;
import com.wzh.superclean.bean.AutoStartAppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wzh on 2015/9/26.
 */
public class AutoStartLVAdapter extends BaseAdapter {

    private Context mContext ;
    public List<AutoStartAppInfo> mAppInfoList ;
    private LayoutInflater mInflater ;

    public AutoStartLVAdapter(Context context , List<AutoStartAppInfo> list) {
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
            convertView = mInflater.inflate(R.layout.autostart_listview_layout,null) ;
            holder = new ViewHolder() ;
            holder.appIcon = (ImageView)convertView.findViewById(R.id.autostart_app_icon_id) ;
            holder.appName = (TextView)convertView.findViewById(R.id.autostart_app_name_id) ;
            holder.appDes = (TextView)convertView.findViewById(R.id.autostart_app_des_id) ;
            holder.tvswitch = (TextView)convertView.findViewById(R.id.autostart_switch_id) ;

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag() ;
        }

        final AutoStartAppInfo appInfo = (AutoStartAppInfo)getItem(position) ;
        holder.appIcon.setImageDrawable(appInfo.getIcon());
        holder.appName.setText(appInfo.getAppName());
        if(appInfo.getIsEnable()){
            holder.tvswitch.setBackgroundResource(R.drawable.switch_on);
            holder.tvswitch.setText("已开启");
        }else{
            holder.tvswitch.setBackgroundResource(R.drawable.switch_off);
            holder.tvswitch.setText("已禁止");
        }

        holder.appDes.setText("开机自启");

        holder.tvswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查是否有root权限
                if(checkRootPermission()){
                    //检查是否已经启动还是已经禁止
                    if(appInfo.getIsEnable()){
                        //禁止app自启
                    }else{
                        //开启app自启
                    }
                }else{
                    Toast.makeText(mContext,"你没有root权限，无法进行此操作",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView ;
    }

    class ViewHolder{
        ImageView appIcon;
        TextView appName;
        TextView appDes ;
        TextView tvswitch;

        String packageName;
    }

    /**
     * 检查是该android手机是否有root权限
     * @return
     */
    private boolean checkRootPermission(){
        return false ;
    }
}
