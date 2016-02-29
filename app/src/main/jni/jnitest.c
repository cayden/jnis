//
// Created by 崔冉 on 16/2/27.
//

#include "com_cayden_jnis_JniUtils.h"

/*
 * Class:     com_cayden_jnis_JniUtils
 * Method:    getCLanguageString
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_cayden_jnis_JniUtils_getCLanguageString
        (JNIEnv *env, jobject obj){

    return (*env)->NewStringUTF(env,"I'm jni");
}
