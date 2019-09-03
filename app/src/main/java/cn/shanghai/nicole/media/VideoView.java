package cn.shanghai.nicole.media;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/***
 * @date 2019-08-28 09:29
 * @author BoXun.Zhao
 * @description
 */
public class VideoView extends SurfaceView implements MediaPreparedListener {

    private MediaPlayerZ mPlayer;

    public VideoView(Context context) {
        this(context, null);
    }

    public VideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        SurfaceHolder holder = getHolder();
        // 设置显示的像素格式
        holder.setFormat(PixelFormat.RGBA_8888);
        mPlayer = new MediaPlayerZ();
        mPlayer.setPreparedListener(this);
    }

    public void stop() {
        mPlayer.stop();
    }


    @Override
    public void onPrepared() {
        mPlayer.setSurface(getHolder().getSurface());
        mPlayer.play();
    }
}
