package com.wzh.superclean.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.wzh.superclean.R;
import com.wzh.superclean.adapter.AutoStartVPAdapter;
import com.wzh.superclean.fragment.AutoStartFragment;
import com.wzh.superclean.receive.TestReceiver;

import java.util.ArrayList;
import java.util.List;

/**
 * 自启管理的Activity
 */
public class AutoStartMgrActivity extends Activity {

    private ViewPager mViewPager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_start);
        initView() ;
        //这个是代码注册广播
        TestReceiver tReceiver = new TestReceiver() ;
        IntentFilter ifilter = new IntentFilter("com.wzh.superclean.action.textbroad") ;
        this.registerReceiver(tReceiver,ifilter) ;

        Intent it = new Intent();
        it.setAction("com.wzh.superclean.action.textbroad") ;
        sendBroadcast(it);
        //可发送有序广播
        //       sendOrderedBroadcast(it,null);
    }

    private void initView(){
        mViewPager = (ViewPager)findViewById(R.id.autostart_vp_id) ;
        List<Fragment> fragList = new ArrayList<>() ;
        fragList.add(new AutoStartFragment()) ;
        fragList.add(new AutoStartFragment()) ;
        FragmentPagerAdapter vpAdapter = new AutoStartVPAdapter(this.getFragmentManager(),fragList) ;
        mViewPager.setAdapter(vpAdapter);
    }

    @Override
      public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_auto_start, menu);
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
    protected void onSaveInstanceState(Bundle outState) {

    }
}
