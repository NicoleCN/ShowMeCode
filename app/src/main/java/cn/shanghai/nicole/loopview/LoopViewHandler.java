package cn.shanghai.nicole.loopview;


import android.os.Handler;
import android.os.Message;

/***
 * @date 2019-08-20 12:52
 * @author BoXun.Zhao
 * @description
 */
public class LoopViewHandler extends Handler {

    public static final int WHAT_SCROLL_BY = 1;

    public static final int WHAT_SMOOTH_SCROLL_OFFSET = 1 << 1;

    private final LoopView mLoopView;

    LoopViewHandler(LoopView loopview) {
        mLoopView = loopview;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case WHAT_SCROLL_BY:
                scrollBy(msg);
                break;
            case WHAT_SMOOTH_SCROLL_OFFSET:
                smoothScrollOffset(msg);
                break;
            default:
                break;
        }
    }

    public static void sendSmoothScrollOffsetMsg(Handler handler, int offset, int delay) {
        Message obtain = Message.obtain(handler, WHAT_SMOOTH_SCROLL_OFFSET);
        obtain.arg1 = offset;
        handler.sendMessageDelayed(obtain, delay);
    }

    public static void sendScrollByMsg(Handler handler, float velocityY, int delay) {
        Message obtain = Message.obtain(handler, WHAT_SCROLL_BY);
        //加速度 px/s
        obtain.arg1 = (int) velocityY;
        handler.sendMessageDelayed(obtain, delay);
    }

    public static void clearAllMessage(Handler handler) {
        if (handler != null) {
            handler.removeMessages(WHAT_SCROLL_BY);
            handler.removeMessages(WHAT_SMOOTH_SCROLL_OFFSET);
        }
    }

    private void smoothScrollOffset(Message msg) {
        int totalOffset = msg.arg1;
        int perOffset = (int) (totalOffset * 0.1f);
        if (perOffset == 0) {
            if (totalOffset < 0) {
                perOffset = -1;
            } else {
                perOffset = 1;
            }
        }
        if (Math.abs(totalOffset) <= 0) {
            clearAllMessage(this);
            mLoopView.onItemSelected();
        } else {
            mLoopView.totalScrollY += perOffset;
            mLoopView.invalidate();
            sendSmoothScrollOffsetMsg(this, totalOffset - perOffset, 10);
        }
    }

    private void scrollBy(Message msg) {
        float acc = msg.arg1;
        //最大算2000px/s  最小20px/s
        if (Math.abs(acc) > 2000f) {
            if (acc > 0.0f) {
                acc = 2000f;
            } else {
                acc = -2000f;
            }
        }
        if (Math.abs(acc) >= 0 && Math.abs(acc) <= 20) {
            mLoopView.cancelHandler();
            mLoopView.smoothScroll(LoopUtil.TYPE_FLING);
            return;
        }
        //a*10毫秒/1000 转换单位
        int scrollPx = (int) (acc * 10 / 1000);
        mLoopView.totalScrollY = mLoopView.totalScrollY - scrollPx;

        float itemHeight = mLoopView.normalItemHeight;
        int initPosition = mLoopView.initPosition;

        //处理开始和末尾的情况 拉到最后面和最前面 加速度的情况
        if (mLoopView.totalScrollY <= (int) (-initPosition * itemHeight)) {
            acc = 40f;
            mLoopView.totalScrollY = (int) (-initPosition * itemHeight);
        } else if (mLoopView.totalScrollY >= (int) ((float) (mLoopView.mDataList.size() - 1 - initPosition) * itemHeight)) {
            mLoopView.totalScrollY = (int) ((float) (mLoopView.mDataList.size() - 1 - initPosition) * itemHeight);
            acc = -40f;
        }
        if (acc < 0) {
            //a<0 +20  趋近于0
            acc += 20f;
        } else {
            //a >0 -20 趋近于0
            acc -= 20f;
        }
        mLoopView.invalidate();
        sendScrollByMsg(this,acc,10);
    }
}
