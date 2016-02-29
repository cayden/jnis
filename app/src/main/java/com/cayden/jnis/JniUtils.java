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
