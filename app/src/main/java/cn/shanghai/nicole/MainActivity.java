package cn.shanghai.nicole;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import cn.shanghai.nicole.media.MediaPlayerZ;
import cn.shanghai.nicole.media.MediaPreparedListener;

public class MainActivity extends AppCompatActivity implements MediaPreparedListener {

    private MediaPlayerZ mediaPlayerZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.tv);
        mediaPlayerZ = new MediaPlayerZ();
        mediaPlayerZ.setPreparedListener(this);
        textView.setText(mediaPlayerZ.getEncryptString("123"));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 1);*/

//                AlbumSelectorManager.getInstance().startAlbumSelect(MainActivity.this);
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.mp3";
                File file = new File(path);
                if (file.exists()) {
                    mediaPlayerZ.setDataSource(path);
                    mediaPlayerZ.prepareAsync();
                }
            }
        });


    }



    @Override
    public void onPrepared() {
        mediaPlayerZ.play();
    }
}
