package cn.shanghai.nicole.albumselector;

import android.app.Activity;
import android.content.Intent;

/***
 * @date 2019-08-21 19:01
 * @author BoXun.Zhao
 * @description 相册选择公开类
 */
public class AlbumSelectorManager {

    public static final String TAG = "AlbumSelector";
    /**
     * 单选
     */
    public static final short MODE_SINGLE = 2;

    /**
     * 多选
     */
    public static final short MODE_MULTI = 1;

    /**
     * 图片
     */
    public static final short TYPE_PICTURE = 10;

    /**
     * 视频
     */
    public static final short TYPE_VIDEO = 11;
    /**
     * 图片和视频
     */
    public static final short TYPE_PICTURE_VIDEO = 12;

    public static final int REQUEST_CODE = 0x100;

    public static final int RESULT_CODE = 0x200;

    public static final String SELECT_MODE = "select_mode";

    public static final String SELECT_MAX_NUMBER = "select_max_number";

    public static final String SELECT_TYPE = "select_type";

    /**
     * 最大选择数目
     */
    private int mMaxNumber = 1;

    /**
     * 选择模式(单选,多选)
     */
    private int mMode = MODE_SINGLE;

    /**
     * 选择
     */
    private int mType = TYPE_PICTURE;


    private AlbumSelectorManager() {
    }

    public static AlbumSelectorManager getInstance() {
        return AlbumSelectorManagerHolder.albumSelectorManager;
    }

    public void startAlbumSelect( Activity activity) {
        /*PermissionManager.getInstance().requestPermissionsIfNecessary(activity,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new PermissionResultCallback() {
                    @Override
                    public void onGranted() {
                        startAlbumSelectActivity(activity);
                    }

                    @Override
                    public void onDenied() {
                        UIUtils.createToast(activity.getString(R.string.tt_permission_denied));
                    }
                });*/
        startAlbumSelectActivity(activity);
    }

    private void startAlbumSelectActivity(Activity activity) {
        Intent intent = new Intent(activity, AlbumSelectorActivity.class);
        intent.putExtra(SELECT_MODE, mMode);
        intent.putExtra(SELECT_TYPE, mType);
        intent.putExtra(SELECT_MAX_NUMBER, mMaxNumber);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    private static class AlbumSelectorManagerHolder {
        private static AlbumSelectorManager albumSelectorManager = new AlbumSelectorManager();
    }
}
