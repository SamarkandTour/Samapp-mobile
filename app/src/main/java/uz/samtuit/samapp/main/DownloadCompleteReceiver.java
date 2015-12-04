package uz.samtuit.samapp.main;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

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

        int countOfDownRequest = pref.getInt("download_request_count", 0);
        int downloadUriIndex = pref.getInt("downloaded_uri_index", 0);
        for (int i = 0; i < countOfDownRequest; i++ ) {
            if (pref.getLong("download_request_id" + i, 0) == downloadedId) {
                // Store at SharedPreferences to upgrade when the App restarts
                editor.putString("downloaded_uri" + downloadUriIndex++, downloadManager.getUriForDownloadedFile(downloadedId).toString());
                editor.putInt("downloaded_uri_index", downloadUriIndex);
                editor.commit();
            }
        }

        // Notify download is completed and the upgrade will be applied when the App restarts
        if (downloadUriIndex == countOfDownRequest) {
            editor.putInt("downloaded_uri_index", 0).commit(); // Don't forget this

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
                    .setSmallIcon(R.drawable.ic_directions_walk_white_18dp)
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent)
                    .build();

            notificationManager.notify(1, notification);
        }
    }
}
