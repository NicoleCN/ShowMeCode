//
// Created by Nicole on 2019-08-27.
//

#ifndef SHOWMECODE_MPPACKETQUEUE_H
#define SHOWMECODE_MPPACKETQUEUE_H

#include <queue>
#include <pthread.h>

extern "C" {
#include <libavcodec/avcodec.h>
};
using namespace std;

class MpPacketQueue {
private:
    queue<AVPacket *> *packetQueue;
    pthread_cond_t packetCond;
    pthread_mutex_t packetMutex;

public:
    MpPacketQueue();

    ~MpPacketQueue();

public:
    void push(AVPacket *pPacket);

    AVPacket *pop();

    void clear();
};


#endif //SHOWMECODE_MPPACKETQUEUE_H
