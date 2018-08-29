package com.cq.dlm.appupdatedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final int DOWN_LOAD_APK_ID = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tvDownload = findViewById(R.id.tv_download);
        //需要判断一下是否有存储权限，这里就自己去写
        tvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String downloadUrl = "http://js.downcc.com/anzhuoyy/owxxt_downcc.apk";
                Intent intent = new Intent(MainActivity.this, AppUpdateService.class);
                Bundle bundle = new Bundle();
                bundle.putString("download_url", downloadUrl);
                bundle.putInt("download_id", DOWN_LOAD_APK_ID);
                bundle.putString("download_file", downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1));
                intent.putExtras(bundle);
                startService(intent);
            }
        });
    }
}
