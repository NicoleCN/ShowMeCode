//
// Created by Nicole on 2019-08-27.
//

#include "../header/MpPacketQueue.h"

/**
 * 执行的顺序就是
 * push 之后 pop
 * 如果队列是空 那么就等待 直到有新的push进来
 */
MpPacketQueue::MpPacketQueue() {
    packetQueue = new queue<AVPacket *>();
    //互斥锁
    pthread_mutex_init(&packetMutex, NULL);
    //初始化条件变量 条件锁
    pthread_cond_init(&packetCond, NULL);
}

MpPacketQueue::~MpPacketQueue() {
    if (packetQueue) {
        clear();
        delete (packetQueue);
        packetQueue = NULL;
    }
    pthread_mutex_destroy(&packetMutex);
    pthread_cond_destroy(&packetCond);
}

void MpPacketQueue::push(AVPacket *pPacket) {
    /*激活线程
    1。加锁（和等待线程用同一个锁）
    2。pthread_cond_signal发送信号
    3。解锁
    激活线程的上面三个操作在运行时间上都在等待线程的pthread_cond_wait函数内部。*/

    pthread_mutex_lock(&packetMutex);

    packetQueue->push(pPacket);
    //被阻塞的线程可以被pthread_cond_signal函数 唤醒
    //必须在互斥锁的保护下使用相应的条件变量。如果没有线程被阻塞在条件变量上
    pthread_cond_signal(&packetCond);

    pthread_mutex_unlock(&packetMutex);
}

AVPacket *MpPacketQueue::pop() {
    /*等待线程
    1。使用pthread_cond_wait前要先加锁
    2。pthread_cond_wait内部会解锁，然后等待条件变量被其它线程激活
    3。pthread_cond_wait被激活后会再自动加锁*/
    AVPacket *pPacket;
    pthread_mutex_lock(&packetMutex);

    while (packetQueue->empty()) {
        //一般配合pthread_mutex_lock 在之间的循环使用
        //pthread_mutex_unlock
        pthread_cond_wait(&packetCond, &packetMutex);
    }
    pPacket = packetQueue->front();
    packetQueue->pop();

    pthread_mutex_unlock(&packetMutex);
    return pPacket;
}

void MpPacketQueue::clear() {
}
