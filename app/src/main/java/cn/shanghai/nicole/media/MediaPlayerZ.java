package cn.shanghai.nicole.media;

import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;

/***
 * @date 2019-08-28 09:31
 * @author BoXun.Zhao
 * @description
 */
public class MediaPlayerZ {
    static {
        System.loadLibrary("media-player");
    }

    /**
     * url 可以是本地文件路径，也可以是 http 链接
     */
    private String url;

    private MediaErrorListener mErrorListener;

    private MediaPreparedListener mPreparedListener;

    public void setErrorListener(MediaErrorListener mediaErrorListener) {
        mErrorListener = mediaErrorListener;
    }

    public void setPreparedListener(MediaPreparedListener preparedListener) {
        mPreparedListener = preparedListener;
    }

    // called from jni
    private void onError(int code, String msg) {
        if (mErrorListener != null) {
            mErrorListener.onError(code, msg);
        }
    }

    // called from jni
    private void onPrepared() {
        if (mPreparedListener != null) {
            mPreparedListener.onPrepared();
        }
    }

    public void setDataSource(String url) {
        this.url = url;
        Log.e("jni", "url->>" + url);
    }


    public void play() {
        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("url is null, please call method setDataSource");
        }
        nativePlay();
    }

    /**
     * 同步准备
     */
    public void prepare() {
        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("url is null, please call method setDataSource");
        }
        nativePrepare(url);
    }

    /**
     * 异步准备
     */
    public void prepareAsync() {
        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("url is null, please call method setDataSource");
        }
        nativePrepareAsync(url);
    }


    public void stop() {

    }

    private native void nativePrepare(String url);

    private native void nativePrepareAsync(String url);

    private native void nativePlay();

    public native void setSurface(Surface surface);
}
