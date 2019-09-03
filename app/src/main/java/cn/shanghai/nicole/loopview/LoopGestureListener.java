package cn.shanghai.nicole.loopview;

import android.view.GestureDetector;
import android.view.MotionEvent;

/***
 * @date 2019-08-20 12:58
 * @author BoXun.Zhao
 * @description
 */
public class LoopGestureListener extends GestureDetector.SimpleOnGestureListener {

    private final LoopView mLoopView;

    public LoopGestureListener(LoopView loopView) {
        mLoopView = loopView;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        mLoopView.scrollBy(velocityY);
        return true;
    }
}
