package com.cayden.process;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

/**
 * Created by cuiran on 16/2/29.
 */
public class Watcher {
    //TODO Fix this according to your service
    private static final String PACKAGE = "com.cayden.process/";

    private String mMonitoredService = "";
    private volatile boolean bHeartBreak = false;
    private Context mContext;
    private boolean mRunning = true;

    public void createAppMonitor(String userId)
    {
        if( !createWatcher(userId) )
        {
            Log.e("Watcher", "<<Monitor created failed>>");
        }
    }

    public Watcher( Context context)
    {
        mContext = context;
    }



    /**
     * Native方法，创建一个监视子进程.
     * @param userId 当前进程的用户ID,子进程重启当前进程时需要用到当前进程的用户ID.
     * @return 如果子进程创建成功返回true，否则返回false
     */
    private native boolean createWatcher(String userId);

    /**
     * Native方法，让当前进程连接到监视进程.
     * @return 连接成功返回true，否则返回false
     */
    private native boolean connectToMonitor();

    /**
     * Native方法，向监视进程发送任意信息
     * @param 发给monitor的信息
     * @return 实际发送的字节
     */
    private native int sendMsgToMonitor(String msg);

    public native String getTestStr();

    static
    {
        System.loadLibrary("monitor");
    }
}
