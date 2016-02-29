package com.cayden.process.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.cayden.process.R;
import com.cayden.process.receiver.DeleteReceiver;
import com.cayden.process.ui.MainActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by cuiran on 16/2/25.
 */
public class NotificationService extends Service {

    private static final String TAG=NotificationService.class.getSimpleName();

    private boolean mReflectFlg = false;

    private static final int NOTIFICATION_ID = 100; // 如果id设置为0,会导致不能设置为前台service
    private static final Class<?>[] mSetForegroundSignature = new Class[] {
            boolean.class};
    private static final Class<?>[] mStartForegroundSignature = new Class[] {
            int.class, Notification.class};
    private static final Class<?>[] mStopForegroundSignature = new Class[] {
            boolean.class};
    private NotificationManager mNM;
    private Method mSetForeground;
    private Method mStartForeground;
    private Method mStopForeground;
    private Object[] mSetForegroundArgs = new Object[1];
    private Object[] mStartForegroundArgs = new Object[2];
    private Object[] mStopForegroundArgs = new Object[1];


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "NotificationService onCreate");

        mNM = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            mStartForeground = NotificationService.class.getMethod("startForeground", mStartForegroundSignature);
            mStopForeground = NotificationService.class.getMethod("stopForeground", mStopForegroundSignature);
        } catch (NoSuchMethodException e) {
            mStartForeground = mStopForeground = null;
        }

        try {
            mSetForeground = getClass().getMethod("setForeground",
                    mSetForegroundSignature);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(
                    "OS doesn't have Service.startForeground OR Service.setForeground!");
        }


    }

    private void showNotification(){

        //点击的意图ACTION是跳转到Intent
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        //点击通知栏的清除，通过广播来接收通知
        Intent receiverIntent=new Intent(this,DeleteReceiver.class);
        PendingIntent pendingReceiver=PendingIntent.getBroadcast(this, 0, receiverIntent, 0);


        /**
         * 显示在通知栏
         */
        Notification.Builder builder = new Notification.Builder(this)
            /*设置small icon*/
                .setSmallIcon(R.mipmap.ic_launcher)
            /*设置title*/
                .setContentTitle(this.getString(R.string.app_name))
            /*设置详细文本*/
                .setContentText(getString(R.string.enter))
           /*设置发出通知的时间为发出通知时的系统时间*/
                .setWhen(System.currentTimeMillis())
             /*设置发出通知时在status bar进行提醒*/
                .setTicker(this.getString(R.string.app_name))
            /*setOngoing(boolean)设为true,notification将无法通过左右滑动的方式清除
            * 可用于添加常驻通知，必须调用cancel方法来清除
            */
                .setOngoing(true)
             /*设置点击后通知消失*/
                .setAutoCancel(true)
             /*设置通知数量的显示类似于QQ那种，用于同志的合并*/
                .setNumber(2)
             /*点击跳转到MainActivity*/
                .setContentIntent(contentIntent)
                .setDeleteIntent(pendingReceiver);


        Notification notification = builder.getNotification();
        //第一种启动方法
//        mNM.notify(NOTIFICATION_ID, builder.build());

        //第二种启动方法
        startForegroundCompat(12314, notification);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.d(TAG, "onBind");
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG, "onStart");
        showNotification();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "～～～～～～～onDestroy～～～～～～～");

        stopForegroundCompat(NOTIFICATION_ID);
    }


    void invokeMethod(Method method, Object[] args) {
        try {
            method.invoke(this, args);
        } catch (InvocationTargetException e) {
            // Should not happen.
            Log.w(TAG, "Unable to invoke method", e);
        } catch (IllegalAccessException e) {
            // Should not happen.
            Log.w(TAG, "Unable to invoke method", e);
        }
    }

    /**
     * This is a wrapper around the new startForeground method, using the older
     * APIs if it is not available.
     */
    void startForegroundCompat(int id, Notification notification) {
        if (mReflectFlg) {
            // If we have the new startForeground API, then use it.
            if (mStartForeground != null) {
                mStartForegroundArgs[0] = Integer.valueOf(id);
                mStartForegroundArgs[1] = notification;
                invokeMethod(mStartForeground, mStartForegroundArgs);
                return;
            }

            // Fall back on the old API.
            mSetForegroundArgs[0] = Boolean.TRUE;
            invokeMethod(mSetForeground, mSetForegroundArgs);
            mNM.notify(id, notification);
        } else {
            /* 还可以使用以下方法，当sdk大于等于5时，调用sdk现有的方法startForeground设置前台运行，
             * 否则调用反射取得的sdk level 5（对应Android 2.0）以下才有的旧方法setForeground设置前台运行 */

            if(Build.VERSION.SDK_INT >= 5) {
                startForeground(id, notification);
            } else {
                // Fall back on the old API.
                mSetForegroundArgs[0] = Boolean.TRUE;
                invokeMethod(mSetForeground, mSetForegroundArgs);
                mNM.notify(id, notification);
            }
        }
    }

    /**
     * This is a wrapper around the new stopForeground method, using the older
     * APIs if it is not available.
     */
    void stopForegroundCompat(int id) {
        if (mReflectFlg) {
            // If we have the new stopForeground API, then use it.
            if (mStopForeground != null) {
                mStopForegroundArgs[0] = Boolean.TRUE;
                invokeMethod(mStopForeground, mStopForegroundArgs);
                return;
            }

            // Fall back on the old API.  Note to cancel BEFORE changing the
            // foreground state, since we could be killed at that point.
            mNM.cancel(id);
            mSetForegroundArgs[0] = Boolean.FALSE;
            invokeMethod(mSetForeground, mSetForegroundArgs);
        } else {
            /* 还可以使用以下方法，当sdk大于等于5时，调用sdk现有的方法stopForeground停止前台运行，
             * 否则调用反射取得的sdk level 5（对应Android 2.0）以下才有的旧方法setForeground停止前台运行 */

            if(Build.VERSION.SDK_INT >= 5) {
                stopForeground(true);
            } else {
                // Fall back on the old API.  Note to cancel BEFORE changing the
                // foreground state, since we could be killed at that point.
                mNM.cancel(id);
                mSetForegroundArgs[0] = Boolean.FALSE;
                invokeMethod(mSetForeground, mSetForegroundArgs);
            }
        }
    }
}
