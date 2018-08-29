package com.cq.dlm.appupdatedemo;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * App更新Service
 */
public class AppUpdateService extends IntentService {

    private static final String TAG = "AppUpdateService";
    private NotificationManager mNotifyManager;
    private String mDownloadFileName;
    private Notification mNotification;
    public static final String PRIMARY_CHANNEL = "default";
    NotificationCompat.Builder mBuilder;

    public AppUpdateService() {
        super("InitializeService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String downloadUrl = intent.getExtras().getString("download_url");
        final int downloadId = intent.getExtras().getInt("download_id");
        mDownloadFileName = intent.getExtras().getString("download_file");
        final File file = new File(Constants.APP_ROOT_PATH + Constants.DOWNLOAD_DIR + mDownloadFileName);
        long range = 0;
        int progress = 0;
        if (file.exists()) {
            range = SPDownloadUtil.getInstance().get(downloadUrl, 0);//断点续传进度
            if (file.length() > 0) {
                progress = (int) (range * 100 / file.length());
                if (range == file.length()) {
                    installApp(file);
                    return;
                }
            }
        }
        mNotifyManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//适配8.0,自行查看8.0的通知，主要是NotificationChannel
            NotificationChannel chan1 = new NotificationChannel(PRIMARY_CHANNEL,
                    "Primary Channel", NotificationManager.IMPORTANCE_DEFAULT);
            chan1.setLightColor(Color.GREEN);
            chan1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            mNotifyManager.createNotificationChannel(chan1);
            mBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL);
        } else {
            mBuilder = new NotificationCompat.Builder(this, null);
        }
        mBuilder.setContentText(mDownloadFileName)//notification的一些设置，具体的可以去官网查看
                .setContentTitle(this.getString(R.string.app_name))
                .setTicker("正在下载")
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setOnlyAlertOnce(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setProgress(100, progress, false);//显示下载进度
        mNotification = mBuilder.build();
        mNotifyManager.notify(downloadId, mNotification);

        RetrofitHttp.getInstance().downloadFile(range, downloadUrl, mDownloadFileName, new DownloadCallBack() {
            @Override
            public void onProgress(int progress) {
                String contentText = new StringBuffer().append(mDownloadFileName + "  ")
                        .append(progress).append("%").toString();
                mBuilder.setContentText(contentText);
                mBuilder.setProgress(100, progress, false);//更新进度
                mNotification = mBuilder.build();
                mNotifyManager.notify(downloadId, mNotification);
            }

            @Override
            public void onCompleted() {
                mNotifyManager.cancel(downloadId);
                installApp(file);
            }

            @Override
            public void onError(String msg) {
                mNotifyManager.cancel(downloadId);
            }
        });
    }

    /**
     * apk下载完成，进行安装
     *
     * @param file
     */
    private void installApp(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
//判断是否是AndroidN以及更高的版本
        String authority = getPackageName() + ".provider";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(this, authority, file);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }

}
