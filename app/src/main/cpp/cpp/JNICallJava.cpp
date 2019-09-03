//
// Created by Nicole on 2019-08-28.
//

#include "../header/JNICallJava.h"


JNICallJava::JNICallJava(JavaVM *javaVM, JNIEnv *jniEnv, jobject jPlayerObj) {
    this->javaVM = javaVM;
    this->jniEnv = jniEnv;
    this->jPlayer = jniEnv->NewGlobalRef(jPlayerObj);
    jclass objectClass = jniEnv->GetObjectClass(jPlayerObj);
    //private void onError(int code, String msg)
    //I-->int
    //Ljava/lang/String-->String
    playerErrorMid = jniEnv->GetMethodID(objectClass, "onError", "(ILjava/lang/String;)V");
    playerPrepareMid = jniEnv->GetMethodID(objectClass, "onPrepared", "()V");
}

JNICallJava::~JNICallJava() {
    //所有类似env通过NewXxx方法创建的都需要delete
    jniEnv->DeleteGlobalRef(jPlayer);
}

void JNICallJava::callPlayerError(ThreadMode threadMode, int code, char *msg) {
    // 子线程用不了主线程 jniEnv （native 线程）
    // 子线程是不共享 jniEnv ，他们有自己所独有的
    if (threadMode == THREAD_MAIN) {
        jstring jMsg = jniEnv->NewStringUTF(msg);
        jniEnv->CallVoidMethod(jPlayer, playerErrorMid, code, jMsg);
        jniEnv->DeleteLocalRef(jMsg);
    } else if (threadMode == THREAD_CHILD) {
        JNIEnv *env;
        if (javaVM->AttachCurrentThread(&env, 0) != JNI_OK) {
            return;
        }
        jstring jMsg = jniEnv->NewStringUTF(msg);
        jniEnv->CallVoidMethod(jPlayer, playerErrorMid, code, jMsg);
        jniEnv->DeleteLocalRef(jMsg);
        javaVM->DetachCurrentThread();
    }
}

void JNICallJava::callPlayerPrepared(ThreadMode threadMode) {
    // 子线程用不了主线程 jniEnv （native 线程）
    // 子线程是不共享 jniEnv ，他们有自己所独有的
    if (threadMode == THREAD_MAIN) {
        jniEnv->CallVoidMethod(jPlayer, playerPrepareMid);
    } else if (threadMode == THREAD_CHILD) {
        // 获取当前线程的 JNIEnv， 通过 JavaVM
        JNIEnv *env;
        if (javaVM->AttachCurrentThread(&env, 0) != JNI_OK) {
            return;
        }
        env->CallVoidMethod(jPlayer, playerPrepareMid);
        javaVM->DetachCurrentThread();
    }
}


