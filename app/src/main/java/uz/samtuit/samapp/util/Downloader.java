package uz.samtuit.samapp.util;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import uz.samtuit.samapp.main.R;

/**
 * Download Manager
 */
public class Downloader {
    private DownloadManager downloadManager;
    private DownloadManager.Request[] downloadRequests;
    private URL[] serverURLs;
    private String[] fileNames;
    private final String DOWNLOAD_MANAGER_PACKAGE_NAME = "com.android.providers.downloads";

    public Downloader(ArrayList<String> urls) {
        try {
            serverURLs = new URL[urls.size()];
            fileNames = new String[urls.size()];
            int i = 0;
            for (String url : urls) {
                serverURLs[i] = new URL(url);
                fileNames[i] = url.substring(url.lastIndexOf('/'));
                i++;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public boolean startDownload(Context context, String title, String desc) {
        boolean enable = resolveEnable(context);
        if (!enable) {
            Toast.makeText(context, R.string.toast_downmanager_disabled, Toast.LENGTH_LONG).show();
            return false;
        }

        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        try {
            downloadRequests = new DownloadManager.Request[serverURLs.length];
            int i = 0;
            for (URL url : serverURLs) {
                downloadRequests[i++] = new DownloadManager.Request(Uri.parse(url.toURI().toString()));
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor editor = context.getSharedPreferences("SamTour_Pref", 0).edit();
        int index = 0;
        for (DownloadManager.Request request : downloadRequests) {
            request.setTitle(title);
            request.setDescription(desc);
            request.setDestinationInExternalFilesDir(context, "download", fileNames[index]);
            long id = downloadManager.enqueue(request);
            editor.putLong("download_request_id" + index, id);
            index++;
        }
        editor.putInt("download_request_count", index).commit();
        editor.commit();

        return true;
    }

    private boolean resolveEnable(Context context) {
        int state = context.getPackageManager()
                .getApplicationEnabledSetting(DOWNLOAD_MANAGER_PACKAGE_NAME);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return !(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                    state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED);
        } else {
            return !(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                    state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER);
        }
    }

    static public void initDownloaderState(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("SamTour_Pref", 0).edit();
        editor.putInt("download_request_count", 0);
        editor.putInt("downloaded_uri_index", 0);
        editor.putLong("last_updated", new Date().getTime()); // Set updated date
        editor.commit();
    }

    static public boolean isAlreadyDownloadRequest(Context context) {
        SharedPreferences pref = context.getSharedPreferences("SamTour_Pref", 0);

        int downloadRequestCnt = pref.getInt("download_request_count", 0);
        if (downloadRequestCnt > 0) {
            return true;
        }

        return false; // If no download request
    }

    static public int countOfDownloadRequest(Context context) {
        SharedPreferences pref = context.getSharedPreferences("SamTour_Pref", 0);
        int downloadRequestCnt = pref.getInt("download_request_count", 0);

        return downloadRequestCnt;
    }

    static public boolean isDownloadFinished(Context context) {
        SharedPreferences pref = context.getSharedPreferences("SamTour_Pref", 0);

        int downloadRequestCnt = pref.getInt("download_request_count", 0);
        int downloadedCnt = pref.getInt("downloaded_uri_index", 0);

        if (downloadRequestCnt != 0 && downloadRequestCnt == downloadedCnt) {
            return true;
        }

        return false;
    }
}