package cn.shanghai.nicole.albumselector;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/***
 *@date 创建时间 2019-08-22 13:24
 *@author 作者: BoXun.Zhao
 *@description
 */
public abstract class BaseAlbumHolder extends RecyclerView.ViewHolder {

    public BaseAlbumHolder(View itemView) {
        super(itemView);
    }

    public BaseAlbumHolder(ViewGroup viewGroup, int layoutId) {
        super(LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false));
    }


    public abstract <T> void initUI(T t, int position);
}
