package cn.shanghai.nicole;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.shanghai.nicole.albumselector.AlbumSelectorManager;
import cn.shanghai.nicole.loopview.LoopView;

public class MainActivity extends AppCompatActivity {
    static {
        System.loadLibrary("media-player");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LoopView loopView = findViewById(R.id.loopView);
        TextView textView = findViewById(R.id.tv);
        loopView.setStringItems(getDataList());

        textView.setText("123");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 1);*/

                AlbumSelectorManager.getInstance().startAlbumSelect(MainActivity.this);
            }
        });


    }

    private List<String> getDataList() {
        List<String> dataList=new ArrayList<>();
        for (int i = 10; i <= 100; i++) {
            dataList.add(String.valueOf(i).concat("公斤"));
        }
        return dataList;
    }
}
