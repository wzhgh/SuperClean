package com.wzh.superclean.activity;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.wzh.superclean.R;
import com.wzh.superclean.service.TestBindService;

/**
 * 测试Service的用法
 */
public class TestServiceActivity extends Activity {

    private Button startBtn ;
    private Button stopBtn ;
    private Button startBindBtn ;
    private Button stopBindBtn ;

    private TestBindService.MyBinder mBinder ;

    private ServiceConnection sconn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //拿到Service中的IBinder
            mBinder = (TestBindService.MyBinder)service ;
            Log.v("TestServiceActivity","service is connected") ;
        }

        /**
         * Service出现异常导致断开连接
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v("TestServiceActivity","service is disConnected") ;
        }
    } ;

    @Override
       protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_service);
        initView() ;
    }

    private void initView(){
        startBtn = (Button)findViewById(R.id.btn_start_id) ;
        stopBtn = (Button)findViewById(R.id.btn_stop_id) ;
        startBindBtn = (Button)findViewById(R.id.btn_start_bind_id) ;
        stopBindBtn = (Button)findViewById(R.id.btn_stop_bind_id) ;

        final Intent intent1 = new Intent() ;
        intent1.setAction("action_text_service") ;
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startService(intent1) ;
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(intent1);
            }
        });

        final Intent intent2 = new Intent(TestServiceActivity.this,TestBindService.class) ;
//        intent2.setAction("action_text_bind_service") ;
        startBindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindService(intent2,sconn,Service.BIND_AUTO_CREATE) ;
            }
        });
        stopBindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unbindService(sconn);
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
