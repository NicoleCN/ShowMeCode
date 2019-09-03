//
// Created by Nicole on 2019-08-29.
//

#include "../header/Audio.h"

Audio::Audio(int audioIndex, JNICallJava *jniCallJava, PlayerStatus *playerStatus) : Media(
        streamIndex, jniCallJava, playerStatus) {

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
