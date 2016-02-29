package com.cayden.process.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cayden.process.service.NotificationService;


/**
 * 接受系统广播事件 包含系统重启、网络状态改变等
 * Created by cuiran on 16/2/26.
 */
public class SystemEventReceiver extends BroadcastReceiver{

    private static final String TAG=SystemEventReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG,"***(๑Ő௰Ő๑)(๑Ő௰Ő๑)****onReceive method  and action:"+intent.getAction());

        Intent intentAdd = new Intent(context, NotificationService.class);
        intentAdd.setAction(intent.getAction());
        context.startService(intentAdd);
    }
}
