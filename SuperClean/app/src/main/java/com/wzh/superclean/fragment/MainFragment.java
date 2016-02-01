package com.wzh.superclean.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wzh.superclean.R;
import com.wzh.superclean.activity.AutoStartMgrActivity;
import com.wzh.superclean.activity.CleanRubbishActivity;
import com.wzh.superclean.activity.MemoryCleanActivity;
import com.wzh.superclean.activity.SoftwareMgrActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private View mView ;
    private View memoryCleanView ;
    private View rubblishCleanView ;
    private View autoupView ;
    private View softwareMgrView ;

    private Intent intent ;

    public MainFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, container, false);
        initView();
        return mView ;
    }

    /**
     * 当Fragment的View视图结构完全创建完成后
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * 初始化View
     */
    private void initView(){
        memoryCleanView = mView.findViewById(R.id.card_memory_clean_id) ;
        rubblishCleanView = mView.findViewById(R.id.card_rubbish_clean_id) ;
        autoupView = mView.findViewById(R.id.card_autoup_id) ;
        softwareMgrView = mView.findViewById(R.id.card_software_mgr_id) ;

        MyClickListener listener = new MyClickListener() ;
        memoryCleanView.setOnClickListener(listener);
        rubblishCleanView.setOnClickListener(listener);
        autoupView.setOnClickListener(listener);
        softwareMgrView.setOnClickListener(listener);
    }

    class MyClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
                switch (v.getId()){
                    case R.id.card_memory_clean_id :
                        intent = new Intent(getActivity(), MemoryCleanActivity.class) ;
                        startActivity(intent);
                    break ;
                    case R.id.card_rubbish_clean_id :
                        intent = new Intent(getActivity(), CleanRubbishActivity.class) ;
                        startActivity(intent);
                        break ;
                    case R.id.card_autoup_id :
                        intent = new Intent(getActivity(), AutoStartMgrActivity.class) ;
                        startActivity(intent);
                        break ;
                    case R.id.card_software_mgr_id :
                        intent = new Intent(getActivity(), SoftwareMgrActivity.class) ;
                        startActivity(intent);
                        break ;
                    default:
                        break ;
            }
        }
    }

}
