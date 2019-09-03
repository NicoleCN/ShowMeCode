package cn.shanghai.nicole.albumselector;


import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/***
 *@date 创建时间 2019-08-22 13:23
 *@author 作者: BoXun.Zhao
 *@description
 */
public abstract class BaseAlbumAdapter<T, V extends BaseAlbumHolder> extends RecyclerView.Adapter<V> {

    protected List<T> mDataList;
    private OnItemClickListener mOnItemClickListener;

    public BaseAlbumAdapter() {
    }

    public void deleteItem(T t) {
        mDataList.remove(t);
        notifyDataSetChanged();
    }

    public List<T> getDataList() {
        return mDataList;
    }

    public void setDataList(List<T> list) {
        this.mDataList = list;
        notifyDataSetChanged();
    }

    public void addDataList(List<T> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(V holder, final int position) {
        if (holder == null) {
            return;
        }
        final T t = mDataList.get(position);
        holder.initUI(t, position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position, t);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        if (mDataList == null) {
            return 0;
        }
        return mDataList.size();
    }

    public interface OnItemClickListener<T> {
        void onItemClick(int position, T t);
    }
}
