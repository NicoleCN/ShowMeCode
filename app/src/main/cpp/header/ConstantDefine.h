//
// Created by Nicole on 2019-08-27.
//

#ifndef SHOWMECODE_CONSTANTDEFINE_H
#define SHOWMECODE_CONSTANTDEFINE_H

#include <android/log.h>

#define TAG "Jni_Zbx_MediaPlayer"
#define Log(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)

/**
 * 常量 一般android音视频都是44100
 */
#define AUDIO_SAMPLE_RATE 44100

/**
 * 解码找decode错误
 */
#define CODEC_FIND_DECODER_ERROR_CODE 1

/**
 * 获取codec上下文错误
 */
#define CODEC_ALLOC_CONTEXT_ERROR_CODE 1<<1

/**
 * 重采样opts错误
 */
#define SWR_ALLOC_SET_OPTS_ERROR_CODE  1<<2

/**
 * 重采样转换错误
 */
#define SWR_CONTEXT_INIT_ERROR_CODE 1<<3
#endif //SHOWMECODE_CONSTANTDEFINE_H
