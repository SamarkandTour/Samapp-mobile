package uz.samtuit.samapp.util;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import uz.samtuit.samapp.main.R;

/**
 *  For event of DownloadComplete, Broadcast Receiver
 */
public class DownloadCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Bundle extras = intent.getExtras();
        long downloadedId = extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID);

        SharedPreferences pref = context.getSharedPreferences("SamTour_Pref", 0);
        SharedPreferences.Editor editor = pref.edit();

        int downRequestCnt = pref.getInt(Downloader.DOWNLOAD_REQUEST_CNT, 0);
        int downloadedCnt = pref.getInt(Downloader.DOWNLOADED_CNT, 0);
        for (int i = 0; i < downRequestCnt; i++ ) {
            if (pref.getLong(Downloader.DOWNLOAD_REQUEST_ID + i, 0) == downloadedId) {
                editor.putInt(Downloader.DOWNLOADED_CNT, ++downloadedCnt).commit();

                // Store at SharedPreferences to upgrade when the App restarts
                if (downloadManager.getUriForDownloadedFile(downloadedId) == null) { // download failed
                    Log.e("DownloadCompReceiver", "Failed to download because network is cut off");
                    return;
                } else {
                    Log.d("DownloadCompReceiver", "Download Succeeded, FileUri:" + downloadManager.getUriForDownloadedFile(downloadedId).toString());
                    editor.putString(Downloader.DOWNLOADED_URI + i, downloadManager.getUriForDownloadedFile(downloadedId).toString()).commit();
                }
            }
        }

        // Notify download is completed and the upgrade will be applied when the App restarts
        if (downloadedCnt == downRequestCnt) {
            PendingIntent contentIntent = PendingIntent.getActivity(
                   context,
                    0,
                    new Intent(), // add this
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new Notification.Builder(context)
                    .setTicker(context.getString(R.string.notify_download_complete))
                    .setContentTitle(context.getString(R.string.notify_download_complete))
                    .setContentText(context.getString(R.string.notify_updatable))
                    .setSmallIcon(R.drawable.ic_done_white_24dp)
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent)
                    .build();

            notificationManager.notify(1, notification);
        }
    }
}
