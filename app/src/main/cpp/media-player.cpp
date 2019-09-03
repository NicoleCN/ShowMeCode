#include <jni.h>
#include <string>
#include "header/FFmpeg.h"
#include "header/JNICallJava.h"
#include "header/ConstantDefine.h"

using namespace std;

/**
 * 为了之后拿到线程而用
 */
JavaVM *javaVM = NULL;
FFmpeg *ffmpeg;
/**
 * 为什么jniEnv不在里面new 可能方便全局管理 因为是传递的指针
 */
JNICallJava *jniCallJava;

extern "C"
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    javaVM = vm;
    JNIEnv *env;
    if (javaVM->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }
    Log("JNI加载");
    return JNI_VERSION_1_4;
}

extern "C"
JNIEXPORT void JNICALL
Java_cn_shanghai_nicole_media_MediaPlayerZ_nativePrepare(JNIEnv *env, jobject instance,
                                                         jstring url_) {
    const char *url = env->GetStringUTFChars(url_, 0);
    if (ffmpeg == NULL) {
        jniCallJava = new JNICallJava(javaVM, env, instance);
        ffmpeg = new FFmpeg(jniCallJava, url);
        ffmpeg->prepare();
    }
    env->ReleaseStringUTFChars(url_, url);
}

extern "C"
JNIEXPORT void JNICALL
Java_cn_shanghai_nicole_media_MediaPlayerZ_nativePrepareAsync(JNIEnv *env, jobject instance,
                                                              jstring url_) {
    const char *url = env->GetStringUTFChars(url_, 0);

    if (ffmpeg == NULL) {
        jniCallJava = new JNICallJava(javaVM, env, instance);
        ffmpeg = new FFmpeg(jniCallJava, url);
        Log("创建ffmpeg");
        ffmpeg->prepareAsync();
    }
    env->ReleaseStringUTFChars(url_, url);
}

extern "C"
JNIEXPORT void JNICALL
Java_cn_shanghai_nicole_media_MediaPlayerZ_nativePlay(JNIEnv *env, jobject instance) {
    if (ffmpeg != NULL) {
        Log("ffmpeg->play()");
        ffmpeg->play();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_cn_shanghai_nicole_media_MediaPlayerZ_setSurface(JNIEnv *env, jobject instance,
                                                      jobject surface) {
    if (ffmpeg != NULL) {
        ffmpeg->setSurface(surface);
    }
}