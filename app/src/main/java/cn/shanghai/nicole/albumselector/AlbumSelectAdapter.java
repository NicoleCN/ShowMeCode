package cn.shanghai.nicole.albumselector;

import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import cn.shanghai.nicole.R;
import cn.shanghai.nicole.utils.ScreenUtil;

/***
 * @date 2019-08-27 15:09
 * @author BoXun.Zhao
 * @description
 */
public class AlbumSelectAdapter extends BaseAlbumAdapter<AlbumFileEntity, AlbumSelectAdapter.AlbumSelectHolder>{

    @NonNull
    @Override
    public AlbumSelectHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new AlbumSelectHolder(viewGroup, R.layout.holder_album_select);
    }

    public static class AlbumSelectHolder extends BaseAlbumHolder{
        private ImageView imageView;
        private ImageView checkImageView;
        private LinearLayout videoTypeLayout;
        private TextView videoDurationTextView;
        private TextView indexTextView;
        private ImageView maskView;

        public AlbumSelectHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
            imageView = itemView.findViewById(R.id.holder_album_select_ImageView);
            checkImageView = itemView.findViewById(R.id.holder_album_select_check_ImageView);
            videoTypeLayout = itemView.findViewById(R.id.holder_album_select_video_LinearLayout);
            videoDurationTextView = itemView.findViewById(R.id.holder_album_select_video_TextView);
            indexTextView = itemView.findViewById(R.id.holder_album_select_index_TextView);
            maskView = itemView.findViewById(R.id.holder_album_select_mask_View);
        }

        @Override
        public <T> void initUI(T t, int position) {
            AlbumFileEntity entity = (AlbumFileEntity) t;
            RequestOptions options = new RequestOptions();
            options.override(ScreenUtil.getScreenWidth(itemView.getContext()) / 4, ScreenUtil.getScreenWidth(itemView.getContext()) / 4)
                    .centerCrop();
            Glide.with(imageView.getContext()).load(entity.getPath())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(options).into(imageView);
            if (entity.getMediaType() == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                videoTypeLayout.setVisibility(View.VISIBLE);
               // videoDurationTextView.setText(formatVideoDuration(entity.getDuration()));
            } else {
                videoTypeLayout.setVisibility(View.GONE);
            }
        }
    }

}
