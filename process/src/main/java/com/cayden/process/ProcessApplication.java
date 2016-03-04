package com.cayden.process;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.cayden.process.receiver.SystemEventReceiver;

/**
 * Created by cuiran on 16/2/29.
 */
public class ProcessApplication extends Application {

    private static final String TAG=ProcessApplication.class.getSimpleName();


    private SystemEventReceiver systemEventReceiver=null;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");


        systemEventReceiver=new SystemEventReceiver();
        IntentFilter localIntentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        localIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(systemEventReceiver, localIntentFilter);



    }
}
