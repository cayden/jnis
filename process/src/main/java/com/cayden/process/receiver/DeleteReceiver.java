package com.cayden.process.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cayden.process.service.NotificationService;


/**
 * 通知栏被清除
 * Created by cuiran on 16/2/25.
 */
public class DeleteReceiver extends BroadcastReceiver {

    private static final String TAG=DeleteReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG,"通知栏被清除");
        //收到通知 重新调用显示通知栏
        Intent intentService=new Intent(context,NotificationService.class);
        context.startService(intentService);

    }
}
