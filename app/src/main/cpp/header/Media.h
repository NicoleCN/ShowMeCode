//
// Created by Nicole on 2019-08-29.
//

#ifndef SHOWMECODE_MEDIA_H
#define SHOWMECODE_MEDIA_H

#include "JNICallJava.h"
#include "PlayerStatus.h"
#include "MpPacketQueue.h"
#include "ConstantDefine.h"

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
};

/**
 * video,audio的父类
 */
class Media {
public:
    int streamIndex = -1;
    JNICallJava *jniCallJava=NULL;
    PlayerStatus *playerStatus=NULL;
    MpPacketQueue *mpPacketQueue=NULL;
    /**
     * 因为要获取解码器信息 所以需要codecContext
     */
    AVCodecContext *avCodecContext=NULL;
    /**
     * 视频时长
     */
    int duration = 0;

    /**
     * 上次更新的时间（回调到 java 层）
     */
    double lastUpdateTime = 0;

    /**
     * 时间基
     */
    AVRational timeBase;

    /**
     * 当前播放时间
     */
    double currentTime;

public:
    Media(int streamIndex, JNICallJava *jniCallJava, PlayerStatus *playerStatus);

    ~Media();

    void analysisStream(ThreadMode threadMode, AVFormatContext *avFormatContext);

    virtual void release();

    void callPlayerJniError(ThreadMode threadMode, int code, char *msg);

    /**
     * 公共分析流的方法
     * @param threadMode
     * @param avFormatContext
     */
    void publicAnalysisStream(ThreadMode threadMode, AVFormatContext *avFormatContext);

    /**
     * 子类需要重写分析流的方法 video需要
     * @param threadMode
     * @param avFormatContext
     */
    virtual void privateAnalysisStream(ThreadMode threadMode, AVFormatContext *avFormatContext)=0;
};


#endif //SHOWMECODE_MEDIA_H
