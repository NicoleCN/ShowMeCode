//
// Created by Nicole on 2019-08-29.
//

#ifndef SHOWMECODE_AUDIO_H
#define SHOWMECODE_AUDIO_H


#include "JNICallJava.h"
#include "PlayerStatus.h"
#include "Media.h"
#include <pthread.h>

//涉及到重采样
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

extern "C" {
#include <libavformat/avformat.h>
#include <libswresample/swresample.h>
};

class Audio : Media {
public:
    AVFormatContext *formatContext;
    SwrContext *swrContext;
    uint8_t *resampleOutBuffer;
public:
    Audio(int audioIndex, JNICallJava *jniCallJava, PlayerStatus *playerStatus);

    ~Audio();

private:
    void release() override;
};


#endif //SHOWMECODE_AUDIO_H
