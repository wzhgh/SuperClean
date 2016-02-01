package com.wzh.superclean.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.wzh.superclean.R;
import com.wzh.superclean.adapter.CleanRubbishLVAdapter;
import com.wzh.superclean.bean.AppCacheInfo;
import com.wzh.superclean.service.CleanRubbishService;

import java.util.ArrayList;
import java.util.List;

public class CleanRubbishActivity extends Activity implements CleanRubbishService.OnServiceActionListener {
    private CleanRubbishService mCRService ;
    private ListView mListView ;
    private CleanRubbishLVAdapter mLVAdapter ;
    private List<AppCacheInfo> mAppCacheList = new ArrayList<>();
    private Button scanBtn , cleanBtn ;

    private View pbContainer ;
    private TextView pbHint ;

    private TextView cacheSizeTV ;

    private ServiceConnection sconn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            CleanRubbishService.CRServiceBinder crBinder = (CleanRubbishService.CRServiceBinder)service ;
            //得到CleanRubbishService实例
            mCRService = crBinder.getServiceInstance() ;
            //设置监听器
            mCRService.setOnServiceListener(CleanRubbishActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mCRService.setOnServiceListener(null) ;
            mCRService = null ;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_rubbish);
        initView() ;
        //绑定服务
        Intent intent = new Intent(CleanRubbishActivity.this, CleanRubbishService.class) ;
        bindService(intent,sconn, Context.BIND_AUTO_CREATE) ;
    }

    /**
     * 初始化View
      */
    private void initView(){
        scanBtn = (Button)findViewById(R.id.scan_rubbish_btn_id) ;
        cleanBtn = (Button)findViewById(R.id.clean_rubbish_btn_id) ;
        MyClickListener  clickListener = new MyClickListener() ;
        scanBtn.setOnClickListener(clickListener);
        cleanBtn.setOnClickListener(clickListener);

        mListView = (ListView)findViewById(R.id.cr_listView_id) ;
        mLVAdapter = new CleanRubbishLVAdapter(CleanRubbishActivity.this,mAppCacheList) ;
        mListView.setAdapter(mLVAdapter);

        pbContainer = findViewById(R.id.processbar_container_id) ;
        pbHint = (TextView)findViewById(R.id.processbar_hint_id) ;

        cacheSizeTV = (TextView)findViewById(R.id.cache_size_id) ;
        cacheSizeTV.setText("0");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_clean_rubbish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解绑服务
        unbindService(sconn);
    }

    @Override
    public void onScanStart(Context context) {
        pbContainer.setVisibility(View.VISIBLE);
        pbHint.setText("准备扫描....");
    }

    @Override
    public void onScanUpdate(Context context, Integer current, Integer max) {
        pbHint.setText("已扫描："+current+"/"+max);
    }

    @Override
    public void onScanComplete(Context context, List<AppCacheInfo> infoList) {
        pbContainer.setVisibility(View.GONE);
        //mCRService.getTotalCacheSize() 发回的数据的单位是Byte，将其转成MB
        double temp = (mCRService.getTotalCacheSize() / 1024.0) / 1024.0 ;
        int intTemp = (int)(temp * 100) ;
        temp = intTemp / 100.0 ;
        cacheSizeTV.setText(temp+"");

        mAppCacheList.clear();
        for(AppCacheInfo cacheInfo : infoList ){
            mAppCacheList.add(cacheInfo) ;
        }
        //刷新适配器数据
        mLVAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCleanStart(Context context) {
        pbContainer.setVisibility(View.VISIBLE);
        pbHint.setText("准备清理....");
    }

    @Override
    public void onCleanComplete(Context context, Long cacheSize) {
        pbHint.setText("清理完成");
        pbContainer.setVisibility(View.GONE);
        cacheSizeTV.setText("0");

        mAppCacheList.clear();
        mLVAdapter.notifyDataSetChanged();
    }

    private class MyClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.scan_rubbish_btn_id :
                    mCRService.launchScanTask();
                    break ;
                case R.id.clean_rubbish_btn_id:
                    mCRService.launchCleanTask();
                    break ;
                default:
                    break ;
            }
        }
    }
}
