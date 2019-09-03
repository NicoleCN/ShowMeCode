//package cn.shanghai.nicole.utils;
//
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Matrix;
//import android.graphics.Paint;
//import android.graphics.PorterDuff;
//import android.graphics.PorterDuffXfermode;
//import android.graphics.Rect;
//import android.graphics.RectF;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import io.reactivex.Observable;
//import io.reactivex.ObservableEmitter;
//import io.reactivex.ObservableOnSubscribe;
//import io.reactivex.Observer;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.functions.Function;
//import io.reactivex.schedulers.Schedulers;
//
///***
// * @date 创建时间 2018/9/18 14:40
// * @author 作者: W.YuLong
// * @description 将图片聚合成9宫格
// */
//public class NineCellBitmapUtil {
//    private Builder builder;
//
//    private NineCellBitmapUtil(Builder builder) {
//        this.builder = builder;
//    }
//
//    public int getBitmapSize() {
//        return builder.bitmapSize;
//    }
//
//    public static Builder with() {
//        return new Builder();
//    }
//
//    public <T> void collectBitmap(List<T> dataList, BitmapCallBack callBack) {
//        Observable.create(new ObservableOnSubscribe() {
//            @Override
//            public void subscribe(ObservableEmitter e) throws Exception {
//                for (T t : dataList) {
//                    e.onNext(t);
//                }
//                e.onComplete();
//
//            }
//        }).map(new Function<T, Bitmap>() {
//            @Override
//            public Bitmap apply(T ts) throws Exception {
//                return ImageLoadTool.transferBitmap(ts);
//            }
//        }).observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.newThread())
//                .subscribe(new Observer<Bitmap>() {
//                    private List<Bitmap> resultList;
//
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        if (resultList == null) {
//                            resultList = new ArrayList<>();
//                        }
//                    }
//
//                    @Override
//                    public void onNext(Bitmap bitmap) {
//                        resultList.add(bitmap);
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        callBack.onLoadingFinish(formatNineCellBitmap(resultList));
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                    }
//                });
//    }
//
//
//    //计算九宫格的图片
//    public Bitmap formatNineCellBitmap(List<Bitmap> bitmapList) {
//        if (bitmapList == null || bitmapList.size() == 0) {
//            return null;
//        }
//
//        int length = bitmapList.size();
//        //最多显示9张
//        if (length > 9) {
//            length = 9;
//        }
//
//        int bitmapSize = builder.bitmapSize;
//        //图片画板的内间距
//        int paddingSize = builder.paddingSize;
//        //每张图片之间的间距
//        int itemMargin = builder.itemMargin;
//
//        //每张需要绘制图片的宽高
//        int cellSize;
//        switch (length) {
//            case 1:
//                cellSize = bitmapSize - paddingSize * 2;
//                break;
//            case 2:
//            case 3:
//            case 4:
//                cellSize = (bitmapSize - paddingSize * 2 - itemMargin) / 2;
//                break;
//            default: //默认是三列的图标展示
//                cellSize = (bitmapSize - paddingSize * 2 - itemMargin * 2) / 3;
//        }
//
//        //画布
//        Bitmap outBitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(outBitmap);
//        //先画合成之后的背景颜色，默认是白色
//        canvas.drawColor(builder.backgroundColor);
//        //这个主要是用来计算绘制图片的起始位置
//        int left = paddingSize, top = paddingSize;
//
//        int moveSize = cellSize + itemMargin;
//
//        for (int i = 0; i < length; i++) {
//            Bitmap dealBitmap = scaleAndCenterInsideBitmap(bitmapList.get(i), cellSize);
//            if (dealBitmap != null) {
//                switch (length) {
//                    case 1:
//                        left = paddingSize;
//                        top = paddingSize;
//                        break;
//                    case 2:
//                        left = paddingSize + moveSize * i;
//                        top = (bitmapSize - cellSize) / 2;
//                        break;
//                    case 3:
//                        if (i == 0) {
//                            left = (bitmapSize - cellSize) / 2;
//                        } else {
//                            left = paddingSize + moveSize * (i % 2);
//                        }
//                        top = paddingSize + moveSize * ((i + 1) / 2);
//                        break;
//                    case 4:
//                        left = paddingSize + moveSize * (i % 2);
//                        top = paddingSize + moveSize * (i / 2);
//                        break;
//                    case 5:
//
//                        if (i <= 1) {
//                            left = (bitmapSize - cellSize * 2 - paddingSize * 2) / 2 + moveSize * (i % 2);
//                        } else {
//                            left = paddingSize + moveSize * (i % 3);
//                        }
//
//                        top = paddingSize + (bitmapSize - cellSize * 2) / 2 + moveSize * ((i + 1) / 3);
//                        break;
//                    case 6:
//                        left = paddingSize + moveSize * (i % 3);
//                        top = paddingSize + (bitmapSize - cellSize * 2) / 2 + moveSize * (i / 3);
//
//                        break;
//                    case 7:
//                        if (i == 0) {
//                            left = (bitmapSize - cellSize - paddingSize * 2) / 2;
//                        } else if (i <= 3) {
//                            left = paddingSize + moveSize * ((i - 1) % 3);
//                        } else {
//                            left = paddingSize + moveSize * ((i - 1) % 3);
//                        }
//
//                        top = paddingSize + moveSize * ((i + 2) / 3);
//
//                        break;
//                    case 8:
//                        if (i <= 1) {
//                            left = (bitmapSize - cellSize * 2 - paddingSize * 2) / 2 + moveSize * (i % 3);
//                        } else if (i <= 4) {
//                            left = paddingSize + moveSize * ((i - 2) % 3);
//                        } else {
//                            left = paddingSize + moveSize * ((i - 2) % 3);
//                        }
//                        top = paddingSize + moveSize * ((i + 1) / 3);
//                        break;
//                    case 9:
//                        left = paddingSize + moveSize * (i % 3);
//                        top = paddingSize + moveSize * (i / 3);
//                        break;
//                    default:
//                        break;
//                }
//                canvas.drawBitmap(dealBitmap, left, top, null);
//            }
//        }
//        return outBitmap;
//    }
//
//
//    //将图片缩放换成指定宽高，并且CenterInside模式
//    private Bitmap scaleAndCenterInsideBitmap(Bitmap sourceBitmap, int size) {
//        float sourceWidth = sourceBitmap.getWidth();
//        float sourceHeight = sourceBitmap.getHeight();
//
//        float rate = sourceWidth / sourceHeight;
//        float destRate = 1;
//
//        Bitmap outBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(outBitmap);
//
//        Bitmap resizeBitmap;
//        Matrix matrix = new Matrix();
//        Paint paint = new Paint();
//        paint.setAntiAlias(true);
//        if (rate < destRate) {
//            //图片过高，需要裁掉部分高度
//            float scale = size / sourceWidth;
//            matrix.setScale(scale, scale);
//            resizeBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, (int) sourceWidth, (int) sourceHeight, matrix, true);
//            float cropHeight = (sourceHeight - sourceWidth) * scale;
//            if (builder.hasRoundAngle) {
//                Rect rect = new Rect(0, 0, resizeBitmap.getWidth(), resizeBitmap.getHeight() - (int) cropHeight);
//                RectF rectF = new RectF(rect);
//                canvas.drawRoundRect(rectF, builder.roundSize, builder.roundSize, paint);
//                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//                canvas.drawBitmap(resizeBitmap, rect, rect, paint);
//            } else {
//                canvas.drawBitmap(resizeBitmap, 0, -cropHeight / 2, null);
//            }
//
//        } else {
//            //图片过宽，需要裁掉部分宽度
//            float scale = size / sourceHeight;
//            matrix.setScale(scale, scale);
//            resizeBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, (int) sourceWidth, (int) sourceHeight, matrix, true);
//            float cropWidth = (sourceWidth - sourceHeight) * scale;
//            if (builder.hasRoundAngle) {
//                Rect rect = new Rect(0, 0, resizeBitmap.getWidth() - (int) cropWidth, resizeBitmap.getHeight());
//                RectF rectF = new RectF(rect);
//                canvas.drawRoundRect(rectF, builder.roundSize, builder.roundSize, paint);
//                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//                canvas.drawBitmap(resizeBitmap, rect, rect, paint);
//            } else {
//                canvas.drawBitmap(resizeBitmap, -cropWidth / 2, 0, null);
//            }
//        }
//
//        return outBitmap;
//    }
//
//
//    public static class Builder {
//        //画布宽度和高度
//        private int bitmapSize = 300;
//        //聚合后的图片内间距
//        private int paddingSize = 10;
//        //每张图片的间距
//        private int itemMargin = 15;
//
//        private float roundSize;
//
//        private boolean hasRoundAngle;
//
//        //聚合后图片的背景色
//        private int backgroundColor = Color.LTGRAY;
//
//        public Builder() {
//        }
//
//        public NineCellBitmapUtil build() {
//            return new NineCellBitmapUtil(this);
//        }
//
//        public int getBackgroundColor() {
//            return backgroundColor;
//        }
//
//        public Builder setBackgroundColor(int backgroundColor) {
//            this.backgroundColor = backgroundColor;
//            return this;
//        }
//
//        public int getBitmapSize() {
//            return bitmapSize;
//        }
//
//        public Builder setBitmapSize(int bitmapSize) {
//            this.bitmapSize = bitmapSize;
//            this.roundSize = bitmapSize * 0.05f;
//            return this;
//        }
//
//        public Builder setRoundSize(float roundSize) {
//            this.roundSize = roundSize;
//            return this;
//        }
//
//        public int getPaddingSize() {
//            return paddingSize;
//        }
//
//        public Builder setPaddingSize(int paddingSize) {
//            this.paddingSize = paddingSize;
//            return this;
//        }
//
//        public int getItemMargin() {
//            return itemMargin;
//        }
//
//        public Builder setItemMargin(int itemMargin) {
//            this.itemMargin = itemMargin;
//            return this;
//        }
//
//        public boolean hasRoundAngle() {
//            return hasRoundAngle;
//        }
//
//        public Builder setHasRoundAngle(boolean hasRoundAngle) {
//            this.hasRoundAngle = hasRoundAngle;
//            return this;
//        }
//    }
//
//    /***
//     *@date 创建时间 2018/9/18 11:50
//     *@author 作者: W.YuLong
//     *@description 图片聚合完成之后的回调
//     */
//    public interface BitmapCallBack {
//        /*处理完成*/
//        void onLoadingFinish(Bitmap bitmap);
//    }
//}
