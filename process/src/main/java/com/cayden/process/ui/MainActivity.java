package com.cayden.process.ui;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.cayden.process.R;
import com.cayden.process.Watcher;

public class MainActivity extends Activity {

    private static final String TAG=MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            PackageManager pm= getPackageManager();
            ApplicationInfo ai=pm.getApplicationInfo("com.cayden.process", PackageManager.GET_ACTIVITIES);
            String uid=String.valueOf(ai.uid);
            Log.d(TAG, "uid＝" + ai.uid);
            Watcher watcher=new Watcher(this);
            Log.e(TAG,"jni return str:"+watcher.getTestStr());
            watcher.createAppMonitor(uid);

        }catch (Exception e){
            Log.e(TAG,"onCreate error",e);
        }
    }
}
