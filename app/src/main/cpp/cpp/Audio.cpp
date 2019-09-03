//
// Created by Nicole on 2019-08-29.
//

#include "../header/Audio.h"

//这里稍微注意下是audio传入类似于audioIndex是后面的media的参数 之前写成streamIndex是错的
Audio::Audio(int audioIndex, JNICallJava *jniCallJava, PlayerStatus *playerStatus) : Media(
        audioIndex, jniCallJava, playerStatus) {

}

void *threadAudioPlay(void *context) {
    Audio *audio = (Audio *) (context);
    audio->initCreateOpenSLES();
    return 0;
}

/**
 * 处理数据
 * @return
 */
int Audio::resampleAudio() {
    int dataSize = 0;
    AVPacket *avPacket = NULL;
    AVFrame *avFrame = av_frame_alloc();
    //一帧一帧去解析
    //Packet转换成pcm的过程
    while (playerStatus != NULL && !playerStatus->isExit) {
        avPacket = mpPacketQueue->pop();
        int packetCode = avcodec_send_packet(avCodecContext, avPacket);
        if (packetCode == 0) {
            //接受frame
            int codecReceiveFrameRes = avcodec_receive_frame(avCodecContext, avFrame);
            if (codecReceiveFrameRes == 0) {
                //表示没错误
                //然后每帧都要重采样
                //真正重采样 调用重采样的方法，返回值是返回重采样的个数，也就是 pFrame->nb_samples
                dataSize = swr_convert(swrContext, &resampleOutBuffer, avFrame->nb_samples,
                                       (const uint8_t **) (avFrame->data), avFrame->nb_samples);
                dataSize = dataSize * 2 * 2;
                // 设置当前的时间，方便回调进度给 Java ，方便视频同步音频
                // s
                double times = av_frame_get_best_effort_timestamp(avFrame) * av_q2d(timeBase);
                if (times > currentTime) {
                    currentTime = times;
                }
                // write 写到缓冲区 pFrame.data -> javabyte
                // size 是多大，装 pcm 的数据
                // 1s 44100 点  2通道 ，2字节    44100*2*2
                // 1帧不是一秒，pFrame->nb_samples点
                break;
            }

        }
        // 解引用
        av_packet_unref(avPacket);
        av_frame_unref(avFrame);
    }
    // 1. 解引用数据 data ， 2. 销毁 pPacket 结构体内存  3. pPacket = NULL
    av_packet_free(&avPacket);
    av_frame_free(&avFrame);
    return dataSize;
}

void Audio::play() {
    pthread_t pThread;
    pthread_create(&pThread, NULL, threadAudioPlay, this);
    pthread_detach(pThread);
}

void playerCallback(SLAndroidSimpleBufferQueueItf caller, void *pContext) {
    Audio *pAudio = (Audio *) pContext;
    int dataSize = pAudio->resampleAudio();
    // 这里为什么报错，留在后面再去解决
    (*caller)->Enqueue(caller, pAudio->resampleOutBuffer, dataSize);
}

/**
 * 固定格式
 */
void Audio::initCreateOpenSLES() {
    /*OpenSLES OpenGLES 都是自带的
    XXXES 与 XXX 之间可以说是基本没有区别，区别就是 XXXES 是 XXX 的精简
    而且他们都有一定规则，命名规则 slXXX() , glXXX3f*/
    // 3.1 创建引擎接口对象
    SLObjectItf engineObject = NULL;
    SLEngineItf engineEngine;
    slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
    // realize the engine
    (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
    // get the engine interface, which is needed in order to create other objects
    (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineEngine);
    // 3.2 设置混音器
    static SLObjectItf outputMixObject = NULL;
    const SLInterfaceID ids[1] = {SL_IID_ENVIRONMENTALREVERB};
    const SLboolean req[1] = {SL_BOOLEAN_FALSE};
    (*engineEngine)->CreateOutputMix(engineEngine, &outputMixObject, 1, ids, req);
    (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);
    SLEnvironmentalReverbItf outputMixEnvironmentalReverb = NULL;
    (*outputMixObject)->GetInterface(outputMixObject, SL_IID_ENVIRONMENTALREVERB,
                                     &outputMixEnvironmentalReverb);
    SLEnvironmentalReverbSettings reverbSettings = SL_I3DL2_ENVIRONMENT_PRESET_STONECORRIDOR;
    (*outputMixEnvironmentalReverb)->SetEnvironmentalReverbProperties(outputMixEnvironmentalReverb,
                                                                      &reverbSettings);
    // 3.3 创建播放器
    SLObjectItf pPlayer = NULL;
    SLPlayItf pPlayItf = NULL;
    SLDataLocator_AndroidSimpleBufferQueue simpleBufferQueue = {
            SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 2};
    SLDataFormat_PCM formatPcm = {
            SL_DATAFORMAT_PCM,
            2,
            SL_SAMPLINGRATE_44_1,
            SL_PCMSAMPLEFORMAT_FIXED_16,
            SL_PCMSAMPLEFORMAT_FIXED_16,
            SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT,
            SL_BYTEORDER_LITTLEENDIAN};
    SLDataSource audioSrc = {&simpleBufferQueue, &formatPcm};
    SLDataLocator_OutputMix outputMix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
    SLDataSink audioSnk = {&outputMix, NULL};
    SLInterfaceID interfaceIds[3] = {SL_IID_BUFFERQUEUE, SL_IID_VOLUME, SL_IID_PLAYBACKRATE};
    SLboolean interfaceRequired[3] = {SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE};
    (*engineEngine)->CreateAudioPlayer(engineEngine, &pPlayer, &audioSrc, &audioSnk, 3,
                                       interfaceIds, interfaceRequired);
    (*pPlayer)->Realize(pPlayer, SL_BOOLEAN_FALSE);
    (*pPlayer)->GetInterface(pPlayer, SL_IID_PLAY, &pPlayItf);
    // 3.4 设置缓存队列和回调函数
    SLAndroidSimpleBufferQueueItf playerBufferQueue;
    (*pPlayer)->GetInterface(pPlayer, SL_IID_BUFFERQUEUE, &playerBufferQueue);
    // 每次回调 this 会被带给 playerCallback 里面的 context
    (*playerBufferQueue)->RegisterCallback(playerBufferQueue, playerCallback, this);
    // 3.5 设置播放状态
    (*pPlayItf)->SetPlayState(pPlayItf, SL_PLAYSTATE_PLAYING);
    // 3.6 调用回调函数
    playerCallback(playerBufferQueue, this);
}

Audio::~Audio() {
    release();
}

void Audio::release() {
    Media::release();
    if (resampleOutBuffer) {
        free(resampleOutBuffer);
        resampleOutBuffer = NULL;
    }
    if (swrContext != NULL) {
        swr_free(&swrContext);
        free(swrContext);
        swrContext = NULL;
    }
}

void Audio::privateAnalysisStream(ThreadMode threadMode, AVFormatContext *avFormatContext) {
    //重采样start
    //输出的
    int64_t out_ch_layout = AV_CH_LAYOUT_STEREO;
    enum AVSampleFormat out_sample_format = AVSampleFormat::AV_SAMPLE_FMT_S16;
    int out_sample_rate = AUDIO_SAMPLE_RATE;
    //输入 从已经拿到的解码器里面拿
    int64_t in_ch_layout = avCodecContext->channel_layout;
    enum AVSampleFormat in_sample_fmt = avCodecContext->sample_fmt;
    int in_sample_rate = avCodecContext->sample_rate;

    //设置重采样opts 信息放入上下文
    swrContext = swr_alloc_set_opts(NULL, out_ch_layout, out_sample_format, out_sample_rate,
                                    in_ch_layout, in_sample_fmt, in_sample_rate, 0, NULL);
    if (swrContext == NULL) {
        callPlayerJniError(threadMode, SWR_ALLOC_SET_OPTS_ERROR_CODE, "swr alloc set opts error");
        return;
    }
    //重采样初始化
    int initCode = swr_init(swrContext);
    if (initCode < 0) {
        callPlayerJniError(threadMode, SWR_CONTEXT_INIT_ERROR_CODE, "swr context swr init error");
        return;
    }
    //为什么乘以4 2通道 2字节
    resampleOutBuffer = static_cast<uint8_t *>(malloc(avCodecContext->frame_size * 2 * 2));
    //重采样end
}




