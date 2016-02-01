package com.wzh.superclean.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.wzh.superclean.R;
import com.wzh.superclean.fragment.ImageFragment;
import com.wzh.superclean.fragment.MainFragment;
import com.wzh.superclean.fragment.NavigateDrawerFragment;
import com.wzh.superclean.fragment.VideoFragment;

/**
 *  wzh
 */
public class MainActivity extends Activity implements NavigateDrawerFragment.NavigateDrawerCallBack {

    private ActionBar mActionBar ;

    /**
     * 帧布局，存放多个内容fragment的view容器
     */
    private FrameLayout fragmentContainer ;

    /**
     * 抽屉布局
     */
    private DrawerLayout mDrawerLayout ;

    /**
     * 抽屉，该抽屉是个Fragment
     */
    private Fragment drawerFragment ;

    /**
     * 抽屉视图
     */
    private View drawerView ;

    /**
     * 抽屉监听器
     */
    private ActionBarDrawerToggle mDrawerToggle ;

    private FragmentManager mFragmentManager ;

    private FragmentTransaction mFragTransaction ;

    private MainFragment mainFragment ;

    private ImageFragment imageFragment ;

    private VideoFragment videoFragment ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentContainer = (FrameLayout)findViewById(R.id.frame_context_id) ;
        mFragmentManager = getFragmentManager() ;

        drawerFragment = this.getFragmentManager().findFragmentById(R.id.navigation_drawer_id) ;
        drawerView = drawerFragment.getView() ;
        //也可以通过findViewById()获取Fragment的视图View，结果跟drawerFragment.getView()是一样的
//      drawerView = (View)findViewById(R.id.navigation_drawer_id) ;
        initDrawer() ;
        //设置默认显示MainFragment
        onNavigateDrawerItemSelected(0);
    }

    /**
     * 初始化ActionBar
     */
    private void initActionBar(){
        //getActionBar() 需要api 11 +
        mActionBar = this.getActionBar() ;
        mActionBar.setDisplayHomeAsUpEnabled(true);
        //setHomeButtonEnabled():该方法在 api 14 +运行
        mActionBar.setHomeButtonEnabled(true);
        //不显示home图标
//        mActionBar.setDisplayShowHomeEnabled(false);
    }

    /**
     * 初始化抽屉
     */
    private void initDrawer(){

        //使用ActionBarDrawerToggle需要对getActionBar进行一些处理setDisplayHomeAsUpEnabled(true)，.setHomeButtonEnabled(true)
        initActionBar() ;

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout_id) ;
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.drawable.ic_drawer,R.string.drawer_open,R.string.drawer_close){
                    @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //刷新选项菜单(重新创建选项菜单，onCreateOptionsMenu()会被调用)
                //creates call to onPrepareOptionsMenu()，用于控制Menu的变化
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }

            /**
             * onOptionsItemSelected 处理ActionBar上的MenuItem被点击的事件，需要手动被activity的onOptionsItemSelected调用
             */
            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                if(item != null && item.getItemId() == android.R.id.home){
                    //是否左边的抽屉处于visible；isDrawerVisible():抽屉局部可见，isDrawerOpen():抽屉完全展开
                    if(mDrawerLayout.isDrawerVisible(Gravity.LEFT)){
                        //关闭左边的抽屉；还有closeDrawer(View drawView)：关闭指定的抽屉视图(我们这里抽屉用的是是fragment，视图不好搞)
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                    }else{
                        //打开左边抽屉
                        mDrawerLayout.openDrawer(Gravity.LEFT);
                    }

                    /*
                    //关闭和打开指定的抽屉视图
                    if(mDrawerLayout.isDrawerVisible(drawerView)){
                        mDrawerLayout.closeDrawer(drawerView);
                    }else{
                        mDrawerLayout.openDrawer(drawerView);
                    }
                    */
                    return true ;
                }
                return false;
            }
        } ;
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     *  Called whenever we call invalidateOptionsMenu()
     *  每当我们调用invalidateOptionsMenu()，就会回调onPrepareOptionsMenu()
     *  在这个方法里控制MenuItem的变化，
     *  详细看api 范例file:///D:/AndroidDevelop/AndroidSdk/docs/training/implementing-navigation/nav-drawer.html
     *  */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
//        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //将MenuItem传给mDrawerToggle的onOptionsItemSelected()，可处理home_icon图标被点击的情况
        //这个是按照ActionBarDrawerToggle的 api的建议来做的
        if(mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //syncState() ：同步抽屉指示器与DrawerLayout的状态(具体看api 解释)
        //这是按照 ActionBarDrawerToggle 的api建议来做的
        mDrawerToggle.syncState();
    }

    /**
     * 需要在配AndroidManifest.xml中为Activity设置android:configChanges=""
     * 它会拦截指定的事件，直接回调onConfigurationChanged()，而不是重启Activity(onCreate())
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //按照ActionBarDrawerToggle 的api建议来做的
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onNavigateDrawerItemSelected(int position) {
        //每次都要开启一个新的事务
        mFragTransaction = mFragmentManager.beginTransaction() ;
        hideAllFragments() ;
        closeDrawer();
        switch (position){
            case 0 :
                if (mainFragment == null){
                    mainFragment = new MainFragment() ;
                    mFragTransaction.add(R.id.frame_context_id,mainFragment) ;
                }else{
                    mFragTransaction.show(mainFragment);
                }
                mFragTransaction.commit() ;
                break ;
            case 1 :
                if (imageFragment == null){
                    imageFragment = new ImageFragment() ;
                    mFragTransaction.add(R.id.frame_context_id,imageFragment) ;
                }else{
                    mFragTransaction.show(imageFragment);
                }
                mFragTransaction.commit() ;
                break ;
            case 2 :
                if (videoFragment == null){
                    videoFragment = new VideoFragment() ;
                    mFragTransaction.add(R.id.frame_context_id,videoFragment) ;
                }else{
                    mFragTransaction.show(videoFragment);
                }
                mFragTransaction.commit() ;
                break ;
            case 3 :
                //跳转到设置的SetingActivity
                Intent intent = new Intent(MainActivity.this,SettingActivity.class) ;
                startActivity(intent);
                break;
            default:
                break ;
        }
    }

    /**
     * 关闭抽屉
     */
    private void closeDrawer(){
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    /**
     * 隐藏所有的Fragment
     */
    private void hideAllFragments(){
        if(mainFragment != null){
            mFragTransaction.hide(mainFragment) ;
        }
        if(imageFragment != null){
            mFragTransaction.hide(imageFragment);
        }
        if(videoFragment != null){
            mFragTransaction.hide(videoFragment) ;
        }
    }
}
