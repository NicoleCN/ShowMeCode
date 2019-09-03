//
// Created by Nicole on 2019-08-27.
//

#ifndef SHOWMECODE_CONSTANTDEFINE_H
#define SHOWMECODE_CONSTANTDEFINE_H

#include <android/log.h>

#define TAG "Jni_Zbx_MediaPlayer"
#define Log(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)

//常量 一般android音视频都是44100
#define AUDIO_SAMPLE_RATE 44100
#define CODEC_FIND_DECODER_ERROR_CODE 1
#define CODEC_ALLOC_CONTEXT_ERROR_CODE 1<<1

#endif //SHOWMECODE_CONSTANTDEFINE_H
