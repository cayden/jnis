package com.cayden.process.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;

import com.cayden.process.R;
import com.cayden.process.Watcher;
import com.cayden.process.service.NotificationService;

import java.lang.reflect.Method;

public class MainActivity extends Activity {

    private static final String TAG=MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent intent=new Intent(this,NotificationService.class);
        startService(intent);

        try{
            PackageManager pm= getPackageManager();
            ApplicationInfo ai=pm.getApplicationInfo("com.cayden.process", PackageManager.GET_ACTIVITIES);
            String uid=getUid();
//            String uid=String.valueOf(ai.uid) ;
            Log.d(TAG, "uid＝" + uid);
            Watcher watcher=new Watcher(this);
            Log.e(TAG,"jni return str:"+watcher.getTestStr());
            watcher.createAppMonitor(uid);

        }catch (Exception e){
            Log.e(TAG,"onCreate error",e);
        }
    }

    private String getUid(){
        Object userManager = getSystemService("user");
        if (userManager == null)
        {
            Log.e("", "userManager not exsit !!!");
            return null;
        }

        UserManager um = (UserManager)userManager;
        try
        {
            Method myUserHandleMethod = android.os.Process.class.getMethod("myUserHandle", (Class<?>[]) null);
            Object myUserHandle = myUserHandleMethod.invoke(android.os.Process.class, (Object[]) null);

            UserHandle uh = android.os.Process.myUserHandle();
            Long l =um.getSerialNumberForUser(uh);

            Method getSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", myUserHandle.getClass());
            long userSerial = (Long) getSerialNumberForUser.invoke(userManager, myUserHandle);
            return String.valueOf(userSerial);
        }
        catch (Exception e)
        {

            Log.e("", "", e);
        }

        return null;
    }
}
