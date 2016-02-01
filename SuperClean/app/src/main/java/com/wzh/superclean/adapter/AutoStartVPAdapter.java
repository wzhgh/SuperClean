package com.wzh.superclean.adapter;


import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wzh on 2015/9/25.
 * 自启管理的 ViewPager的适配器
 */
public class AutoStartVPAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

    private List<Fragment> mList ;

    public AutoStartVPAdapter(FragmentManager fm,List<Fragment> list) {
        super(fm);
        if(list != null){
            mList = list ;
        }else{
            mList = new ArrayList<>() ;
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Fragment getItem(int i) {
        Fragment currentFragment = mList.get(i) ;
        Bundle bundle = new Bundle() ;
        bundle.putInt("position",i);
        currentFragment.setArguments(bundle);

        return currentFragment;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
