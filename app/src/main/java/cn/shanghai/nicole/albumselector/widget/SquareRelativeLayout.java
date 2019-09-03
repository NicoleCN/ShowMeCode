package cn.shanghai.nicole.albumselector.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/***
 *@date 创建时间 2019-08-27 15:15
 *@author 作者: BoXun.Zhao
 *@description
 */
public class SquareRelativeLayout extends RelativeLayout {
    public SquareRelativeLayout(Context context) {
        super(context);
    }

    public SquareRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
