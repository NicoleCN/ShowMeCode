package cn.shanghai.nicole.loopview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


/***
 * @date 2019-08-20 12:40
 * @author BoXun.Zhao
 * @description 仿Ios的滚动View
 */
public class LoopView extends View {
    private static final String TAG = "zbx";
    //实验证明 textSize*dens!=文字的高度 实际是前者大一点 2dp 应该是上下各1dp
    protected Handler mHandler;
    private GestureDetector mGestureDetector;
    private int mDescSize = LoopUtil.DEFAULT_TEXT_SIZE;
    /**
     * 间距因子 需要大于1.0f
     */
    private float mItemFactor = LoopUtil.DEFAULT_ITEM_FACTOR;
    private int mLineColor = 0xffc5c5c5;
    private int mCenterColor = 0xff313131;
    private int mOuterColor = 0xffafafaf;

    private int mVisibleCount = LoopUtil.DEFAULT_VISIBLE_ITEMS;

    private Paint mOuterTextPaint;
    private Paint mCenterTextPaint;
    private Paint mLinePaint;

    protected List<IGetItemNameInterface> mDataList;
    private SparseArray<IGetItemNameInterface> mCachePool = new SparseArray<>();

    private int measuredWidth;
    private int measuredHeight;
    private int textHeight;
    private int maxItemHeight;
    protected int normalItemHeight;
    //半圆弧
    private int semicircle;
    private int radius;

    //中间的两条线
    private int mFirstLineY;
    private int mSecondLineY;

    private OnItemSelectListener onItemSelectListener;

    private long mStartTime;
    protected int totalScrollY;
    private float previousY;
    /**
     * 初始选中的位置
     */
    protected int initPosition;
    private int preSelectIndex;

    private Rect textStandRect;
    private int mOffset = 0;
    /**
     * 对外暴露的选中位置
     */
    private int currentSelectedIndex;

    public LoopView(Context context) {
        this(context, null);
    }

    public LoopView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoopView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParams(context, attrs);
    }

    private void initParams(Context context, AttributeSet attrs) {
        mHandler = new LoopViewHandler(this);
        mGestureDetector = new GestureDetector(context, new LoopGestureListener(this));
        //TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoopView);
        //typedArray.recycle();
        initPaints();
    }

    private void initPaints() {
        mOuterTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterTextPaint.setColor(mOuterColor);
        mOuterTextPaint.setTypeface(Typeface.MONOSPACE);
        mOuterTextPaint.setTextSize(mDescSize * LoopUtil.getDisplayDensity());
        mOuterTextPaint.setTextAlign(Paint.Align.CENTER);


        mCenterTextPaint = new Paint(mOuterTextPaint);
        mCenterTextPaint.setColor(mCenterColor);
        mCenterTextPaint.setTextAlign(Paint.Align.CENTER);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(mLineColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        remeasure();
    }

    private void remeasure() {
        if (mDataList == null || mDataList.isEmpty()) {
            return;
        }
        //这个值不包含 margin和padding
        measuredWidth = getMeasuredWidth();
        measuredHeight = getMeasuredHeight();

        if (measuredWidth == 0 || measuredHeight == 0) {
            return;
        }
        //因为圆弧 切线上弧度文字是最大的 其余的时候都需要缩放
        textStandRect = new Rect();
        mOuterTextPaint.getTextBounds("伯勋", 0, 2, textStandRect);
        textHeight = textStandRect.height();

        //半径
        radius = measuredHeight >> 1;
        //半圆弧长
        semicircle = (int) (measuredHeight * Math.PI / 2);

        //默认7个的话 间距就是6
        maxItemHeight = (semicircle / (mVisibleCount - 1));

        normalItemHeight = (int) (mItemFactor * maxItemHeight);

        mFirstLineY = (int) ((measuredHeight - normalItemHeight) / 2f);
        mSecondLineY = (int) ((measuredHeight + normalItemHeight) / 2f);

        initPosition = 0;
        preSelectIndex = initPosition;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDataList == null || mDataList.isEmpty()) {
            return;
        }
        int paddingLeft = getPaddingLeft();

        canvas.drawLine(paddingLeft, mFirstLineY, measuredWidth - paddingLeft, mFirstLineY, mLinePaint);
        canvas.drawLine(paddingLeft, mSecondLineY, measuredWidth - paddingLeft, mSecondLineY, mLinePaint);

        int changeItem = totalScrollY / maxItemHeight;

        preSelectIndex = initPosition + changeItem % mDataList.size();

        if (preSelectIndex < 0) {
            preSelectIndex = 0;
        }
        if (preSelectIndex > mDataList.size() - 1) {
            preSelectIndex = mDataList.size() - 1;
        }

        calculateVisData();

        int mod = totalScrollY % maxItemHeight;
        int halfTextSize = textHeight >> 1;
        String drawString;
        int i = 0;
        while (i < mVisibleCount) {
            canvas.save();
            drawString = mCachePool.get(i).getShowString();

            //等于半径长的圆弧所对的圆心角
            double radian = (i * maxItemHeight - mod) * Math.PI / semicircle;

            if (radian >= Math.PI || radian <= 0) {
                canvas.restore();
            } else {
                //其实就是文字中心y
                float translateY = (float) ((1 - Math.cos(radian)) * radius);
                canvas.translate(0f, translateY);
                canvas.scale(1f, (float) Math.sin(radian));

                if (translateY - halfTextSize <= mFirstLineY && halfTextSize + translateY >= mFirstLineY) {
                    //第一条线为分界的  注意分界线有个距离translateY - halfTextSize <= mFirstLineY
                    //不是translateY<= mFirstLineY
                    canvas.save();
                    canvas.clipRect(0, -halfTextSize, measuredWidth, mFirstLineY - translateY);
                    drawText(canvas, drawString, (measuredWidth) >> 1, 0, mOuterTextPaint, 1f);
                    canvas.restore();

                    canvas.save();
                    canvas.clipRect(0, mFirstLineY - translateY, measuredWidth, halfTextSize);
                    drawText(canvas, drawString, (measuredWidth) >> 1, 0, mCenterTextPaint, 1f);
                    canvas.restore();
                } else if (translateY - halfTextSize <= mSecondLineY && halfTextSize + translateY >= mSecondLineY) {
                    //第二条线为分解的
                    canvas.save();
                    canvas.clipRect(0, -halfTextSize, measuredWidth, mSecondLineY - translateY);
                    drawText(canvas, drawString, (measuredWidth) >> 1, 0, mCenterTextPaint, 1f);
                    canvas.restore();

                    canvas.save();
                    canvas.clipRect(0, mSecondLineY - translateY, measuredWidth, halfTextSize);
                    drawText(canvas, drawString, (measuredWidth) >> 1, 0, mOuterTextPaint, 1f);
                    canvas.restore();
                } else if (translateY - halfTextSize >= mFirstLineY && halfTextSize + translateY <= mSecondLineY) {
                    //中间的
                    canvas.clipRect(0, -halfTextSize, measuredWidth, halfTextSize);
                    drawText(canvas, drawString, (measuredWidth) >> 1, 0, mCenterTextPaint, 1f);
                    currentSelectedIndex = mDataList.indexOf(mCachePool.get(i));
                    Log.e(TAG, "选中了" + currentSelectedIndex + "-->" + mDataList.get(currentSelectedIndex).getShowString());
                } else {
                    //外部的
                    canvas.clipRect(0, -halfTextSize, measuredWidth, halfTextSize);
                    drawText(canvas, drawString, (measuredWidth) >> 1, 0, mOuterTextPaint, 1f);
                }
                canvas.restore();
            }
            i++;
        }
    }

    private void calculateVisData() {
        int i = 0;
        while (i < mVisibleCount) {
            int l1 = preSelectIndex - (mVisibleCount / 2 - i);
            if (l1 < 0) {
                mCachePool.put(i, new GetItemNameImpl());
            } else if (l1 > mDataList.size() - 1) {
                mCachePool.put(i, new GetItemNameImpl());
            } else {
                mCachePool.put(i, mDataList.get(l1));
            }
            i++;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean eventConsumed = mGestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartTime = System.currentTimeMillis();
                previousY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                //totalScrollY 向下滑动 对应数值要减少 所以要previousY-event.getRawY()
                float dy = previousY - event.getRawY();
                previousY = event.getRawY();
                totalScrollY += dy;

                float top = -initPosition * normalItemHeight;
                float bottom = (mDataList.size() - 1 - initPosition) * normalItemHeight;

                if (totalScrollY < top) {
                    totalScrollY = (int) top;
                } else if (totalScrollY > bottom) {
                    totalScrollY = (int) bottom;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            default:
                if (!eventConsumed) {
                    //抬起的y
                    float y = event.getY();
                    double l = Math.acos((radius - y) / radius) * radius;
                    int circlePosition = (int) ((l + normalItemHeight / 2) / normalItemHeight);
                    float extraOffset = totalScrollY % normalItemHeight;
                    mOffset = (int) ((circlePosition - mVisibleCount / 2) * normalItemHeight - extraOffset);
                    if ((System.currentTimeMillis() - mStartTime) > 120) {
                        smoothScroll(LoopUtil.TYPE_DRAG);
                    } else {
                        smoothScroll(LoopUtil.TYPE_CLICK);
                    }
                }
                break;
        }
        invalidate();
        return true;
    }

    /**
     * 滑动到临近的一个
     *
     * @param type
     */
    protected void smoothScroll(short type) {
        cancelHandler();
        if (type == LoopUtil.TYPE_FLING || type == LoopUtil.TYPE_DRAG) {
            mOffset = totalScrollY % normalItemHeight;
            //超过办个自动转到下个
            if (mOffset > normalItemHeight / 2f) {
                mOffset = (int) (normalItemHeight - (float) mOffset);
            } else {
                mOffset = -mOffset;
            }
        }
        LoopViewHandler.sendSmoothScrollOffsetMsg(mHandler, mOffset, 0);
    }

    /**
     * 滑动一定距离
     *
     * @param velocityY
     */
    protected void scrollBy(float velocityY) {
        cancelHandler();
        LoopViewHandler.sendScrollByMsg(mHandler, velocityY, 0);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelHandler();
    }

    public void cancelHandler() {
        LoopViewHandler.clearAllMessage(mHandler);
    }


    public List<IGetItemNameInterface> getDataList() {
        return mDataList;
    }

    public void setDataItems(List<IGetItemNameInterface> items) {
        mDataList = items;
        remeasure();
        invalidate();
    }

    public void setStringItems(List<String> items) {
        List<IGetItemNameInterface> list = new ArrayList<>();
        for (String str : items) {
            GetItemNameImpl im = new GetItemNameImpl(str);
            list.add(im);
        }
        setDataItems(list);
    }

    public IGetItemNameInterface getSelectObj() {
        return mDataList.get(currentSelectedIndex);
    }

    public final int getSelectIndex() {
        return currentSelectedIndex;
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    /**
     * 设置当前选中的index
     */
    public void setCurrentPosition(int position) {
        if (mDataList == null || mDataList.isEmpty()) {
            return;
        }
        int size = mDataList.size();
        if (position >= 0 && position < size && position != currentSelectedIndex) {
            initPosition = position;
            totalScrollY = 0;
            mOffset = 0;
            invalidate();
        }
    }

    /**
     * 回调
     */
    public void onItemSelected() {
        if (onItemSelectListener != null) {
            onItemSelectListener.onItemSelected(currentSelectedIndex);
        }
    }

    private void drawText(Canvas canvas, String text, float centerX, float centerY, Paint paint, float scaleX) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        float baseline = centerY + distance;
        paint.setTextScaleX(scaleX);
        canvas.drawText(text, centerX, baseline, paint);
    }
}
