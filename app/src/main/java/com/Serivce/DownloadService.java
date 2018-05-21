package com.Serivce;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.File;

public class DownloadService extends Service {

    private DownloadCompleteReceiver completeReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = intent.getExtras().getString("url");

        completeReceiver = new DownloadCompleteReceiver();
        registerReceiver(completeReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        new Thread(new DownloadRunnable(url, intent)).start();

        return super.onStartCommand(intent, flags, startId);
    }

    class DownloadRunnable implements Runnable {
        private String url;

        private Intent intent;

        public DownloadRunnable(String dUrl, Intent intent) {
            this.url = dUrl;
            this.intent = intent;
        }

        @Override
        public void run() {
            startDownload();
        }

        private void startDownload() {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            request.setTitle(intent.getExtras().getString("fileName"));
            request.setMimeType(intent.getExtras().getString("mime"));
            request.setVisibleInDownloadsUi(true);

            File saveFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), intent.getExtras().getString("fileName"));
            request.setDestinationUri(Uri.fromFile(saveFile));

            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        }
    }

    class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                checkStatus(context, completeDownloadId);
            }
        }

        private void checkStatus(Context context, Long completeDownloadId) {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Service.DOWNLOAD_SERVICE);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(completeDownloadId);
            Cursor cursor = downloadManager.query(query);
            if (cursor.moveToFirst()) {
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

                switch (status) {
                    case (DownloadManager.STATUS_RUNNING): {
                        break;
                    }
                    case (DownloadManager.STATUS_SUCCESSFUL): {
                        Toast.makeText(context, "下载成功, 文件已被保存在\n" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), Toast.LENGTH_LONG).show();
                        DownloadService.this.stopSelf();
                        break;
                    }
                    case (DownloadManager.STATUS_FAILED): {
                        Toast.makeText(context, "下载失败，请重新下载", Toast.LENGTH_LONG).show();
                        break;
                    }
                    default:
                        break;
                }
                cursor.close();
            }
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(completeReceiver);
        super.onDestroy();
    }
}
