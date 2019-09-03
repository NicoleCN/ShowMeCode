//
// Created by Nicole on 2019-08-29.
//

#include "../header/Media.h"

Media::Media(int streamIndex, JNICallJava *jniCallJava, PlayerStatus *playerStatus) {
    this->streamIndex = streamIndex;
    this->jniCallJava = jniCallJava;
    this->playerStatus = playerStatus;
    mpPacketQueue = new MpPacketQueue();
}

Media::~Media() {
    release();
}

void Media::analysisStream(ThreadMode threadMode, AVFormatContext *avFormatContext) {
    publicAnalysisStream(threadMode, avFormatContext);
    privateAnalysisStream(threadMode, avFormatContext);
}

void Media::publicAnalysisStream(ThreadMode threadMode, AVFormatContext *avFormatContext) {
    //从全局上下文里面拿到stream的相关信息
    AVCodecParameters *pParameters = avFormatContext->streams[streamIndex]->codecpar;
    //因为avcodec_find_decoder要先拿coderId
    AVCodec *pCodec = avcodec_find_decoder(pParameters->codec_id);
    if (pCodec == NULL) {
        callPlayerJniError(threadMode, CODEC_FIND_DECODER_ERROR_CODE, "avcodec_find_decoder error");
        return;
    }
    avCodecContext = avcodec_alloc_context3(pCodec);
    if (avCodecContext == NULL) {
        callPlayerJniError(threadMode, CODEC_ALLOC_CONTEXT_ERROR_CODE, "codec alloc context error");
        return;
    }
    /**
     * 注意！！！！！！！！！
     * 这里avCodecContext是上下文 但是有一些信息是没有的 这些信息在哪？？在avFormatContext全局上下文的pParameters
     */
    int res_avCodec_parameters_to_context = avcodec_parameters_to_context(avCodecContext,
                                                                          pParameters);
    if (res_avCodec_parameters_to_context < 0) {
        callPlayerJniError(threadMode, res_avCodec_parameters_to_context,
                           av_err2str(res_avCodec_parameters_to_context));
        return;
    }
    //打开解码器 因为少两个参数所以要先获取
    int res_avCodec_open2 = avcodec_open2(avCodecContext, pCodec, NULL);
    if (res_avCodec_open2 != 0) {
        callPlayerJniError(threadMode, res_avCodec_open2, av_err2str(res_avCodec_open2));
        return;
    }
    duration = avFormatContext->duration;
//    avFormatContext->start_time
}

void Media::callPlayerJniError(ThreadMode threadMode, int code, char *msg) {
    release();
    jniCallJava->callPlayerError(threadMode, code, msg);
}

void Media::release() {
    if (mpPacketQueue) {
        delete (mpPacketQueue);
        mpPacketQueue = NULL;
    }
    if (avCodecContext != NULL) {
        avcodec_close(avCodecContext);
        avcodec_free_context(&avCodecContext);
        avCodecContext = NULL;
    }
}


