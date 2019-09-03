//
// Created by Nicole on 2019-08-28.
//

#ifndef SHOWMECODE_JNICALLJAVA_H
#define SHOWMECODE_JNICALLJAVA_H

#include <jni.h>

enum ThreadMode {
    THREAD_CHILD, THREAD_MAIN
};


class JNICallJava {
public:
    JavaVM *javaVM;
    JNIEnv *jniEnv;
    jmethodID playerErrorMid;
    jmethodID playerPrepareMid;
    jobject jPlayer;
public:
    JNICallJava(JavaVM *javaVM, JNIEnv *jniEnv, jobject jPlayerObj);

    ~JNICallJava();

public:
    void callPlayerError(ThreadMode threadMode, int code, char *msg);

    void callPlayerPrepared(ThreadMode threadMode);
};


#endif //SHOWMECODE_JNICALLJAVA_H
