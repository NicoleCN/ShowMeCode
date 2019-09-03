//
// Created by Nicole on 2019-08-29.
//

#ifndef SHOWMECODE_FFMPEG_H
#define SHOWMECODE_FFMPEG_H

#include "Audio.h"
#include "Video.h"
#include "JNICallJava.h"
#include "PlayerStatus.h"
#include <pthread.h>

extern "C" {
#include "libavformat/avformat.h"
#include "libswresample/swresample.h"
};

/**
 * 所有操作通过ffmeg向下分发
 * ffmpeg里面先判断是audio和video
 */
class FFmpeg {
public:
    //全局的ffmpeg上下文
    AVFormatContext *avFormatContext = NULL;
    JNICallJava *jniCallJava = NULL;
    char *url = NULL;
    Audio *audio = NULL;
    Video *video=NULL;
    PlayerStatus *playerStatus=NULL;
public:
    FFmpeg(JNICallJava *jniCallJava, const char *url);

    virtual ~FFmpeg();

public:
    void play();

    void setSurface(jobject obj);

    void prepare();

    void prepareAsync();

    void release();

    /**
     * 当然这些errCode 和 msg可以自定义
     * @param threadMode
     * @param code
     * @param msg
     */
    void callPlayerJniError(ThreadMode threadMode, int code, char *msg);

    void prepare(ThreadMode threadMode);

};


#endif //SHOWMECODE_FFMPEG_H
