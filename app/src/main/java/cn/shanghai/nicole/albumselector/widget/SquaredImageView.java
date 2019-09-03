package cn.shanghai.nicole.albumselector.widget;

import android.content.Context;
import android.util.AttributeSet;

/***
 *@date 创建时间 2019-08-27 15:15
 *@author 作者: BoXun.Zhao
 *@description  
 */
public class SquaredImageView extends android.support.v7.widget.AppCompatImageView {
    public SquaredImageView(Context context) {
        super(context);
    }

    public SquaredImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
