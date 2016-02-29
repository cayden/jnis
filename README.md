最近准备研究一下android双进程守护，由于此前用eclipse 写jni习惯了，现在主要用as 工具。在此也试着写个demo 然后在对双进程守护进行研究
###1、所需工具
　android studio 1.4
　ndk :android-ndk-r9d
    ....其它必备在此就不叙述
###2、示例介绍
####1)、项目创建
打开as 创建一个project 输入包名 **com.cayden.jnis**
在此如果采用as 生成的布局和activity ，后续运行会出现问题。
具体后续在问题部分会进行说明
####2)、创建jni java类
java 调用c/c++ 需要jni中间桥梁
具体代码如下
```
package com.cayden.jnis;

/**
 * Created by cuiran on 16/2/27.
 */
public class JniUtils {

    public native String getCLanguageString();

    static {
        System.loadLibrary("JniUtils");
    }
}

```
####3)、生成h 创建c文件
需要编译将class文件生成对应h文件
具体看图片中的命令
进入build/intermediates/classes/debug下
 <img src="http://img.blog.csdn.net/20160227155618586" width = "480" height = "800" alt="生成对应的h文件" align=center />

 然后在src/main下创建jni目录,将此前生成的文件com_cayden_jnis_JniUtils.h 剪切到此目录下，并新建文件 jnitest.c
```
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

```
####4)、修改activity并进行调用
具体参考代码
```
package com.cayden.jnis;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JniUtils jniUtils=new JniUtils();

        TextView tv=(TextView)findViewById(R.id.textView);
        tv.setText("jni result:"+jniUtils.getCLanguageString());
    }


}

```
####5)、 配置ndk及其生成so文件名称
在local.properties文件添加**ndk.dir=/Users/cuiran/tool/android-ndk-r9d**
修改build.gradle 文件
```
apply plugin: 'com.android.application'

android {
    compileSdkVersion 19
    buildToolsVersion '19.1.0'

    defaultConfig {
        applicationId "com.cayden.jnis"
        minSdkVersion 14
        targetSdkVersion 23
        versionCode 1
        versionName "1.0"


    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            ndk{
                moduleName "JniUtils"             //生成的so名字
                abiFilters "armeabi", "armeabi-v7a", "x86"  //输出指定三种abi体系结构下的so库。
            }
        }

        debug{
            ndk{
                moduleName "JniUtils"             //生成的so名字
                abiFilters "armeabi", "armeabi-v7a", "x86"  //输出指定三种abi体系结构下的so库。
            }
        }
    }
}

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])

}
```
###3、运行效果
运行后，显示了在c里面写的内容*I'm jni*
<img src="http://img.blog.csdn.net/20160227160646604" width = "500" height = "880" alt="运行效果" align=center />
但是回到项目目录下 发现没有看到生成的so文件 但是我们将apk反编译就可以看到，貌似as还是蛮强大的。
可以看一下 反编译后的截图
<img src="http://img.blog.csdn.net/20160227161042447" width = "500" height = "880" alt="反编译apk后" align=center />
###4、问题整理
####1) 报错：“NDK integration is deprecated in the current plugin”的
解决办法：打开工程目录下的gradle.properties文件，并在该文件中写入下面这行：
android.useDeprecatedNdk=true
不出意外的话，再次编译工程，NDK环境这块就OK了。
####2) 报错：error: jni.h: No such file or directory
解决办法：将compileSdkVersion和targetSdkVersion改成19就编译通过了