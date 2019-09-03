//
// Created by Nicole on 2019-08-29.
//

#include "../header/FFmpeg.h"


FFmpeg::FFmpeg(JNICallJava *jniCallJava, const char *url) {
    this->jniCallJava = jniCallJava;
    //这里url需要复制一份 因为只有外面调用prepare的时候才会创建
    // 但是方法执行完之后url会被销毁 传的是地址 所以这边也会销毁
    //+1把\0也复制进去
    this->url = (char *) malloc(strlen(url) + 1);
    memcpy(this->url, url, strlen(url) + 1);
    playerStatus = new PlayerStatus();
}

FFmpeg::~FFmpeg() {
    release();
}

void FFmpeg::play() {

}

void FFmpeg::setSurface(jobject obj) {

}

void FFmpeg::prepare() {
    prepare(THREAD_MAIN);
}

/**
 * 如果FFmpeg::表示是外部调用的时候必须对象，而下面pthread_create是一个静态方法
 * 所以只能传一个非成员方法进去？？
 * @param context
 * @return
 */
void *threadPrepare(void *context) {
    FFmpeg *ffmpeg = (FFmpeg *) context;
    ffmpeg->prepare(THREAD_CHILD);
    return 0;
}

void FFmpeg::prepareAsync() {
    //创建一个线程去播放，多线程编解码边播放
    pthread_t prepareThreadT;
    //其实就是传了个对象上下文 this
    pthread_create(&prepareThreadT, NULL, threadPrepare, this);
    pthread_detach(prepareThreadT);
}

//真正prepare
void FFmpeg::prepare(ThreadMode threadMode) {
    av_register_all();
    avformat_network_init();
    int res_open_input = 0;
    //打开资源
    res_open_input = avformat_open_input(&avFormatContext, url, NULL, NULL);
    if (res_open_input != 0) {
        //av_err2str错误返回
        callPlayerJniError(threadMode, res_open_input, av_err2str(res_open_input));
        return;
    }
    //查找信息
    int res_find_stream_info = 0;
    res_find_stream_info = avformat_find_stream_info(avFormatContext, NULL);
    if (res_find_stream_info < 0) {
        callPlayerJniError(threadMode, res_find_stream_info, av_err2str(res_find_stream_info));
        return;
    }
    //查找音频流的index
    int audio_index = 0;
    audio_index = av_find_best_stream(avFormatContext, AVMediaType::AVMEDIA_TYPE_AUDIO, -1, -1,
                                      NULL, 0);
    if (audio_index < 0) {
        callPlayerJniError(threadMode, audio_index, av_err2str(audio_index));
        return;
    }
    audio = new Audio(audio_index, jniCallJava, playerStatus);
    audio->analysisStream(threadMode, avFormatContext);

    //查找视频流
    int video_index = 0;
    video_index = av_find_best_stream(avFormatContext, AVMediaType::AVMEDIA_TYPE_VIDEO, -1, -1,
                                      NULL, 0);
    //这里如果小于0 可能只有音频没有视频
    if (audio_index < 0) {
        callPlayerJniError(threadMode, video_index, av_err2str(video_index));
    }


}

void FFmpeg::release() {
    //关闭注意三个 因为前面初始了三个
    if (avFormatContext != NULL) {
        avformat_close_input(&avFormatContext);
        avformat_free_context(avFormatContext);
        avFormatContext = NULL;
    }

    avformat_network_deinit();

    if (url != NULL) {
        free(url);
        url = NULL;
    }
    if (playerStatus != NULL) {
        delete (playerStatus);
        playerStatus = NULL;
    }
    if (audio != NULL) {
        delete (audio);
        audio = NULL;
    }
    if (video != NULL) {
        delete (video);
        video = NULL;
    }
}


void FFmpeg::callPlayerJniError(ThreadMode threadMode, int code, char *msg) {
    release();
    jniCallJava->callPlayerError(threadMode, code, msg);
}


void *threadReadPacket(void *context) {
    return NULL;
}


