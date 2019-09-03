package cn.shanghai.nicole.loopview;

import android.content.res.Resources;

/***
 * @date 2019-08-20 12:48
 * @author BoXun.Zhao
 * @description
 */
public class LoopUtil {

    public static final int DEFAULT_TEXT_SIZE = 14;
    public static final float DEFAULT_ITEM_FACTOR = 1f;
    public static final int DEFAULT_VISIBLE_ITEMS = 7;

    //action
    public static final short TYPE_CLICK = 1;
    public static final short TYPE_DRAG = 2;
    public static final short TYPE_FLING = 3;

    public static float getDisplayDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }
}
