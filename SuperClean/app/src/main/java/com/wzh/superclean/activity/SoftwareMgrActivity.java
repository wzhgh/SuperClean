package com.wzh.superclean.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.wzh.superclean.R;
import com.wzh.superclean.adapter.SoftwareMgrVPAdapter;
import com.wzh.superclean.fragment.SoftwareMgrFragment;

import java.util.ArrayList;
import java.util.List;

public class SoftwareMgrActivity extends Activity {

    private ViewPager mViewPager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_software_mgr);
        initView() ;
    }


    private void initView(){
        mViewPager = (ViewPager)findViewById(R.id.software_mgr_vp_id) ;
        List<Fragment> fragList = new ArrayList<>() ;
        fragList.add(new SoftwareMgrFragment()) ;
        fragList.add(new SoftwareMgrFragment()) ;
        FragmentPagerAdapter vpAdapter = new SoftwareMgrVPAdapter(this.getFragmentManager(),fragList) ;
        mViewPager.setAdapter(vpAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_software_mgr, menu);
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
}
