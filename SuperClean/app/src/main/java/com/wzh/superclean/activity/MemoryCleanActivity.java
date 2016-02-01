package com.wzh.superclean.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.wzh.superclean.R;
import com.wzh.superclean.adapter.ClearMemoryAdapter;
import com.wzh.superclean.bean.AppProcessInfo;
import com.wzh.superclean.service.CleanMemoryService;

import java.util.ArrayList;
import java.util.List;

/**
 * 内存清理的Activity
 */
public class MemoryCleanActivity extends Activity implements CleanMemoryService.OnServiceActionListener{

    private ActionBar mActionBar ;
    private ListView mListView ;
    /**
     * 一件清理按钮
     */
    private Button clearBtn ;

    private Button scanBtn ;

    private List<AppProcessInfo> mAppInfosList = new ArrayList<>() ;

    private View hintContainter ;
    private TextView hintTV ;

    /**
     * 内存数，单位KB
     */
    private long Allmemory ;

    private TextView momorySizeTV ;

    /**
     * ListView的适配器
     */
    private ClearMemoryAdapter lvAdapter ;

    /**
     * Service对象
     */
    private CleanMemoryService cmService ;

    private ServiceConnection sconn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //拿到Service实例对象
            cmService = ((CleanMemoryService.CMServiceBinder)service).getServiceInstance() ;
            //设置监听器
            cmService.setServiceListener(MemoryCleanActivity.this);
            //启动扫描任务
//            cmService.launchScanTask();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            cmService.setServiceListener(null);
            cmService = null ;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_clean);
        initView() ;
        //绑定服务
        Intent intent = new Intent(MemoryCleanActivity.this, CleanMemoryService.class) ;
        bindService(intent,sconn, Context.BIND_AUTO_CREATE) ;
    }

    /**
     * 初始化View
     */
    private void initView(){
        //设置ActionBar
        mActionBar = getActionBar() ;
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        //为ListView设置适配器
        mListView = (ListView)findViewById(R.id.listView_runapp_id) ;
        lvAdapter = new ClearMemoryAdapter(MemoryCleanActivity.this,mAppInfosList);
        mListView.setAdapter(lvAdapter);

        clearBtn = (Button)findViewById(R.id.btn_clear_allprocess_id) ;
        scanBtn = (Button)findViewById(R.id.btn_scanprocess_id) ;
        MyClickListener clickListener = new MyClickListener() ;
        scanBtn.setOnClickListener(clickListener);
        clearBtn.setOnClickListener(clickListener);

        hintContainter = findViewById(R.id.hint_containter_id) ;
        hintTV = (TextView)findViewById(R.id.hint_text_id) ;
        hintContainter.setVisibility(View.GONE);

        momorySizeTV = (TextView)findViewById(R.id.memorysize_tv_id) ;
    }

    public class MyClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_scanprocess_id :
                    //清空ListView中的数据
                    mAppInfosList.clear();
                    lvAdapter.notifyDataSetChanged();
                    //启动扫描
                    cmService.launchScanTask();
                    break ;
                case R.id.btn_clear_allprocess_id :
                    //启动清理
                    cmService.launchCleanTask();
                    break ;
                default:
                    break ;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_memory_clean, menu);
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
        unbindService(sconn);
    }

      @Override
    public void onScanStart(Context context) {
        hintContainter.setVisibility(View.VISIBLE);
        hintTV.setText("开始扫描...");
    }



    @Override
    public void onScanUpdate(Context context, Integer current, Integer max) {
        hintTV.setText("已经扫描到："+current +"/"+max);
    }

    @Override
    public void onScanComplete(Context context, List<AppProcessInfo> infoList) {
//        mAppProcessInfos.clear();
//
//        Allmemory = 0;
//        for (AppProcessInfo appInfo : apps) {
//            if (!appInfo.isSystem) {
//                mAppProcessInfos.add(appInfo);
//                Allmemory += appInfo.memory;
//            }
//        }
        hintTV.setText("扫描完成");
        hintContainter.setVisibility(View.GONE);

        Allmemory = 0;
        mAppInfosList.clear();
        for(AppProcessInfo info : infoList){
            mAppInfosList.add(info) ;
            //计算内存
            Allmemory += info.memory;
        }
        //提通知配器数据改变，刷新ListView视图
        lvAdapter.notifyDataSetChanged();

        double temp = Allmemory / 1024.0 ;
        int dd = (int)(temp * 100 ) ;
        temp = dd / 100.0 ;

        momorySizeTV.setText(temp+"");
    }

    @Override
    public void onCleanStart(Context context) {
        hintContainter.setVisibility(View.VISIBLE);
        hintTV.setText("准备清理...");
    }

    @Override
    public void onCleanComplete(Context context, Long cacheSize) {
        hintTV.setText("清理完成！.");
        hintContainter.setVisibility(View.GONE);

        mAppInfosList.clear();
        lvAdapter.notifyDataSetChanged();

        long msize = cacheSize.longValue() ;
        double temp = msize / 1024.0 ;
        int dd = (int)(temp * 100 ) ;
        temp = dd / 100.0 ;

        momorySizeTV.setText(temp+"");
    }
}
