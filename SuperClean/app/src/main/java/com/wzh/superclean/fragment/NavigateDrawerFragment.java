package com.wzh.superclean.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.wzh.superclean.R;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigateDrawerFragment extends Fragment {

    private View mView ;
    private NavigateDrawerCallBack mCallBack ;
    private RadioGroup mRadioGroup ;
    final int radioBtnIds[] = {
            R.id.rb_main_id,
            R.id.rb_img_id,
            R.id.rb_video_id,
            R.id.rb_set_id
    } ;

    RadioButton[] radioBtns = new RadioButton[radioBtnIds.length] ;

    Map<Integer,Integer> idMap = new HashMap() ;

    public NavigateDrawerFragment() {
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mCallBack = (NavigateDrawerCallBack)activity ;
        }catch (ClassCastException e){
            Log.d("NavigateDrawerFragment","Activity don't implement NavigationDrawerCallback");
            //这个是运行时异常，不是编译时异常；
            //这里最好给它抛出这个异常，让开发者开发时必须implement NavigationDrawerCallbacks，否则调试的时候会报错
            throw new ClassCastException("Activity must implement NavigationDrawerCallback！！") ;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_navigate_drawer, container, false);
        initView() ;
        return mView ;
    }

    /**
     * 初始化View组件
     */
    private void initView(){
        mRadioGroup = (RadioGroup)mView.findViewById(R.id.drawer_rg_id) ;

//        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                for (int i = 0 ; i < radioBtnIds.length ; i++){
//                    if (checkedId == radioBtnIds[i]){
//                        if(mCallBack != null){
//                            mCallBack.onNavigateDrawerItemSelected(i);
//                        }
//                    }
//                }
//            }
//        });

        for(int i = 0 ; i < radioBtnIds.length ; i++){
            radioBtns[i] = (RadioButton)mView.findViewById(radioBtnIds[i]) ;
            radioBtns[i].setOnClickListener(radioBtnClick);
        }
    }

    View.OnClickListener radioBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for(int i= 0 ; i < radioBtnIds.length ; i++){
                if(v.getId() == radioBtnIds[i]){
                     if(mCallBack != null){
                          mCallBack.onNavigateDrawerItemSelected(i);
                     }
                }else{

                }
            }
        }
    } ;

    /**
     *在onCreateView()后调用，表明该Fragment的视图层次已经完全建立
     * (在这里可以Fragment.getView()获取该Fragment的完全的层次视图，不能在onCreateView()中调用getView())
     * mView只是布局文件的视图层次，还没有完全融入到Fragment中
     *详细看api
     *其实在这里面写代码跟onCreateView()里写差不多
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //在这里写自定义代码，可以使用getView()获取该Fragment的视图
        //getView().findViewById(....) ;
    }

    /**
     * onActivityCreated  在Activity.onCreate()  后调用
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setHasOptionsMenu():具体看api解释，大致意思是可以拥有action bar 的Menu，可影响action bar的actions
        // Indicate(表明) that this fragment would like to influence(影响) the set of actions in the action bar.
        setHasOptionsMenu(true);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mCallBack = null ;
    }

    /**
     * 回调接口
     */
    public static interface NavigateDrawerCallBack{
        void onNavigateDrawerItemSelected(int position) ;
    }
}
