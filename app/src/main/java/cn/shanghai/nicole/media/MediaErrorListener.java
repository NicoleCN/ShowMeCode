package cn.shanghai.nicole.media;

/***
 *@date 创建时间 2019-08-28 09:28
 *@author 作者: BoXun.Zhao
 *@description 错误回调
 */
public interface MediaErrorListener {
    void onError(int code, String msg);
}
