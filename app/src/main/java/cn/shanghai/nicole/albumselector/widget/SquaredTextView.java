package cn.shanghai.nicole.albumselector.widget;

import android.content.Context;
import android.util.AttributeSet;

/***
 *@date 创建时间 2019-08-27 15:37
 *@author 作者: BoXun.Zhao
 *@description  
 */
public class SquaredTextView extends android.support.v7.widget.AppCompatTextView {
    public SquaredTextView(Context context) {
        super(context);
    }

    public SquaredTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
