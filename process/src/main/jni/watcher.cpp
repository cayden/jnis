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


char* jstringTostr(JNIEnv* env, jstring jstr)
{
    char* pStr = NULL;

    jclass     jstrObj   = env->FindClass( "java/lang/String");
    jstring    encode    = env->NewStringUTF( "utf-8");
    jmethodID  methodId  = env->GetMethodID(jstrObj, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray byteArray = (jbyteArray)env->CallObjectMethod(jstr, methodId, encode);
    jsize      strLen    = env->GetArrayLength(byteArray);
    jbyte      *jBuf     = env->GetByteArrayElements(byteArray, JNI_FALSE);

    if (jBuf > 0)
    {
        pStr = (char*)malloc(strLen + 1);

        if (!pStr)
        {
            return NULL;
        }

        memcpy(pStr, jBuf, strLen);

        pStr[strLen] = 0;
    }

    env->ReleaseByteArrayElements( byteArray, jBuf, 0);

    return pStr;
}


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

JNIEXPORT jstring JNICALL Java_com_cayden_process_Watcher_getTestStr(JNIEnv* env, jobject thiz)
{

    return env->NewStringUTF("I'm jni process");   //c++
//  return (*env)->NewStringUTF(env,"I'm jni process");  //c

}



