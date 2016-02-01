package com.wzh.superclean.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class TestReceiver extends BroadcastReceiver {
    public TestReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //得到上一个广播传递过来的数据
        Bundle temp = getResultExtras(true) ;

        //可给下一个广播传递数据
        Bundle bd = new Bundle();
        bd.putString("msg","hhah");
        setResultExtras(bd);
        //终止广播传递
   //     abortBroadcast();
    }
}
