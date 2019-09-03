package cn.shanghai.nicole.albumselector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import cn.shanghai.nicole.R;

/***
 * @date 2019-08-21 18:50
 * @author BoXun.Zhao
 * @description
 */
public class AlbumSelectorActivity extends AppCompatActivity implements OnLoadFinishListener {
    private RecyclerView mRv;
    private AlbumSelectAdapter mAlbumSelectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_selector);
        initView();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        intent.getIntExtra(AlbumSelectorManager.SELECT_MODE, AlbumSelectorManager.MODE_SINGLE);
        int type = intent.getIntExtra(AlbumSelectorManager.SELECT_TYPE, AlbumSelectorManager.TYPE_PICTURE);
        intent.getIntExtra(AlbumSelectorManager.SELECT_MAX_NUMBER, 1);
        AlbumLoaderCallbackImp imageLoadCallBack = new AlbumLoaderCallbackImp(this, this);
        LoaderManager.getInstance(this).initLoader(type, null, imageLoadCallBack);
    }

    private void initView() {
        mRv = findViewById(R.id.album_selector_RecyclerView);
        mRv.setLayoutManager(new GridLayoutManager(this, 4));
        mAlbumSelectAdapter = new AlbumSelectAdapter();
        mRv.setAdapter(mAlbumSelectAdapter);
    }

    @Override
    public void onLoadFinish(ArrayList<AlbumFolderEntity> folderList) {
        if (folderList == null || folderList.isEmpty()) {
            return;
        }
        mAlbumSelectAdapter.setDataList(folderList.get(0).getFileEntityList());
    }
}
