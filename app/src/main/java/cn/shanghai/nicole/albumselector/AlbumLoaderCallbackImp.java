package cn.shanghai.nicole.albumselector;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/***
 * @date 2019-08-21 19:33
 * @author BoXun.Zhao
 * @description
 */
public class AlbumLoaderCallbackImp implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String[] mFileParams = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.MIME_TYPE,
    };

    private Context mContext;

    private OnLoadFinishListener mOnLoadFinishListener;

    public AlbumLoaderCallbackImp(Context context, OnLoadFinishListener onLoadFinishListener) {
        mContext = context;
        mOnLoadFinishListener = onLoadFinishListener;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        StringBuilder sqlFilter = new StringBuilder();
        sqlFilter.append(String.format(" %s > 0 AND ", MediaStore.Files.FileColumns.SIZE));
        switch (id) {
            case AlbumSelectorManager.TYPE_VIDEO:
                sqlFilter.append(String.format(" %s = %s ", MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO));
                break;
            case AlbumSelectorManager.TYPE_PICTURE_VIDEO:
                sqlFilter.append(String.format(" %s = %s OR %s = %s ",
                        MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
                        MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO));
                break;
            default:
                sqlFilter.append(String.format(" %s = %s ", MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE));
                break;
        }
        CursorLoader loader = new CursorLoader(mContext,
                MediaStore.Files.getContentUri("external"),
                mFileParams, sqlFilter.toString(), null, MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC");
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            ArrayList<AlbumFolderEntity> folderList = new ArrayList<>();
            if (cursor.getCount() > 0) {
                ArrayList<AlbumFileEntity> allFileList = new ArrayList<>(cursor.getCount());
                cursor.moveToFirst();
                do {
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME));
                    long dateTime = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED));
                    long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE));
                    int mediaType = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE));

                    File file = new File(path);
                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(path) || !file.exists()) {
                        Log.e(AlbumSelectorManager.TAG, String.format("文件错误：name = %s,time= %d, path = %s", name, dateTime, path));
                        continue;
                    }

                    AlbumFileEntity fileEntity = new AlbumFileEntity(path, name, dateTime);
                    fileEntity.setSize(size);
                    fileEntity.setMediaType(mediaType);

                    if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                        long duration = getVideoDuration(fileEntity);
                        fileEntity.setDuration(duration);
                    }
                    allFileList.add(fileEntity);

                    //读取文件夹信息
                    File folderFile = file.getParentFile();
                    if (folderFile != null && folderFile.exists()) {
                        AlbumFolderEntity folder = getExistFolder(folderList, folderFile);
                        if (folder == null) {
                            folder = initFolderEntity(folderFile, fileEntity);
                            folderList.add(folder);
                        } else {
                            folder.addFileEntity(fileEntity);
                        }
                    }
                } while (cursor.moveToNext());

                if (mOnLoadFinishListener != null) {
                    if (allFileList.size() > 0) {
                        AlbumFileEntity entity = allFileList.get(0);
                        AlbumFolderEntity firstAllFolder = new AlbumFolderEntity();
                        File firstParentFile = new File(entity.getPath()).getParentFile();
                        firstAllFolder.setFolderName("全部相册");
                        firstAllFolder.setFolderPath(firstParentFile.getAbsolutePath());
                        firstAllFolder.setCoverEntity(entity);
                        firstAllFolder.getFileEntityList().addAll(allFileList);
                        folderList.add(0, firstAllFolder);
                    }
                    mOnLoadFinishListener.onLoadFinish(folderList);
                }

            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private long getVideoDuration(AlbumFileEntity fileEntity) {
        Cursor cursor = mContext.getContentResolver().query(MediaStore.Video.Media
                        .EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Video.Media.DURATION},
                String.format("%s = %s and %s = '%s' and %s = '%s'",
                        MediaStore.Video.Media.SIZE, fileEntity.getSize(),
                        MediaStore.Video.Media.DATA, fileEntity.getPath(),
                        MediaStore.Video.Media.DISPLAY_NAME, fileEntity.getName()), null, null);
        long duration = -1;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
            cursor.close();
        }
        return duration;
    }

    /**
     * 获取存在的文件夹
     */
    private AlbumFolderEntity getExistFolder(ArrayList<AlbumFolderEntity> folderList, File folderFile) {
        if (folderList != null && folderList.size() > 0) {
            for (AlbumFolderEntity entity : folderList) {
                if (entity.getFolderPath().equals(folderFile.getAbsolutePath())) {
                    return entity;
                }
            }
        }
        return null;
    }

    /**
     * 初始化文件夹的相关信息
     */
    private AlbumFolderEntity initFolderEntity(File folderFile, AlbumFileEntity fileEntity) {
        AlbumFolderEntity folderEntity = new AlbumFolderEntity();
        folderEntity.setFolderName(folderFile.getName());
        folderEntity.setFolderPath(folderFile.getAbsolutePath());
        folderEntity.setCoverEntity(fileEntity);
        folderEntity.addFileEntity(fileEntity);
        return folderEntity;

    }
}
