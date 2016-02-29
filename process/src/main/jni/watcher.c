//
// Created by 崔冉 on 16/2/29.
//

#include "com_cayden_process_Watcher.h"
#include "process.h"
#include "Utils.h"


/**
* 全局变量，代表应用程序进程.
*/
ProcessBase *g_process = NULL;

/**
* 应用进程的UID.
*/
const char* g_userId = NULL;

/**
* 全局的JNIEnv，子进程有时会用到它.
*/
JNIEnv* g_env = NULL;

extern "C"
{
JNIEXPORT jboolean JNICALL Java_com_cayden_process_Watcher_createWatcher( JNIEnv*, jobject, jstring);

JNIEXPORT jboolean JNICALL Java_com_cayden_process_Watcher_connectToMonitor( JNIEnv*, jobject );

JNIEXPORT jint JNICALL Java_com_cayden_process_Watcher_sendMsgToMonitor( JNIEnv*, jobject, jstring );

JNIEXPORT jint JNICALL JNI_OnLoad( JavaVM* , void* );
};

JNIEXPORT jboolean JNICALL Java_com_cayden_process_Watcher_createWatcher( JNIEnv* env, jobject thiz, jstring user )
{
    g_process = new Parent( env, thiz );

    g_userId  = (const char*)jstringTostr(env, user);

    g_process->catch_child_dead_signal();

    if( !g_process->create_child() )
    {
        LOGE("<<create child error!>>");

        return JNI_FALSE;
    }

    return JNI_TRUE;
}


JNIEXPORT jboolean JNICALL Java_com_cayden_process_Watcher_connectToMonitor( JNIEnv* env, jobject thiz )
{
    if( g_process != NULL )
    {
        if( g_process->create_channel() )
        {
            return JNI_TRUE;
        }

        return JNI_FALSE;
    }
}

JNIEXPORT jstring JNICALL Java_com_cayden_process_Watcher_getTestStr
  (JNIEnv *, jobject)
  {


  return (*env)->NewStringUTF(env,"I'm jni process");
  }



