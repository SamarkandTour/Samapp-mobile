package uz.samtuit.samapp.util;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
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

    static public final String DOWNLOAD_REQUEST_CNT = "download_request_count";
    static public final String DOWNLOAD_REQUEST_ID = "download_request_id";
    static public final String DOWNLOADED_CNT = "downloaded_uri_index";
    static public final String DOWNLOADED_URI = "downloaded_uri";

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
            editor.putLong(DOWNLOAD_REQUEST_ID + index, id);
            index++;
        }
        editor.putInt(DOWNLOAD_REQUEST_CNT, index);
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
        editor.putInt(DOWNLOAD_REQUEST_CNT, 0);
        editor.putInt(DOWNLOADED_CNT, 0);
        editor.putLong("last_updated", new Date().getTime()); // Set updated date
        editor.commit();
    }

    static public boolean isAlreadyDownloadRequest(Context context) {
        SharedPreferences pref = context.getSharedPreferences("SamTour_Pref", 0);

        int downloadRequestCnt = pref.getInt(DOWNLOAD_REQUEST_CNT, 0);
        if (downloadRequestCnt > 0) {
            return true;
        }

        return false; // If no download request
    }

    static public int countOfDownloadRequest(Context context) {
        SharedPreferences pref = context.getSharedPreferences("SamTour_Pref", 0);
        int downloadRequestCnt = pref.getInt(DOWNLOAD_REQUEST_CNT, 0);

        return downloadRequestCnt;
    }

    static public boolean isAllDownloadSuccessful(Context context) {
        SharedPreferences pref = context.getSharedPreferences("SamTour_Pref", 0);
        SharedPreferences.Editor editor = pref.edit();

        int downloadRequestCnt = pref.getInt(DOWNLOAD_REQUEST_CNT, 0);
        int downloadedCnt = pref.getInt(DOWNLOADED_CNT, 0);
        boolean downloadFailed = false;

        if (downloadRequestCnt == 0 || downloadedCnt == 0) {
            return false;
        }

        for (int i=0; i < downloadedCnt; i++) {
            long downloadReference = pref.getLong(DOWNLOAD_REQUEST_ID + i, 0);
            if (!checkDownloadStatus(context, downloadReference)) { // If failed, make it fresh for re-downlaod
                Log.e("Downloader", "There is a broken download, Make it un-downloaded for re-download");
                editor.putInt(DOWNLOAD_REQUEST_CNT, downloadRequestCnt - 1);
                editor.putInt(DOWNLOADED_CNT, downloadedCnt-1);
                editor.putString(DOWNLOADED_URI + i, "");
                editor.commit();
                downloadFailed = true;
            }
        }

        if (downloadFailed) {
            return false;
        }

        return true;
    }

    static public boolean checkDownloadStatus(Context context, long downloadReference) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query myDownloadQuery = new DownloadManager.Query();
        //set the query filter to our previously Enqueued download
        myDownloadQuery.setFilterById(downloadReference);
        //Query the download manager about downloads that have been requested.
        Cursor cursor = downloadManager.query(myDownloadQuery);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            //column for status
            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int status = cursor.getInt(columnIndex);
            //column for reason code if the download failed or paused
            int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
            int reason = cursor.getInt(columnReason);
            //get the download filename
            int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
            String filename = cursor.getString(filenameIndex);

            String statusText = "";
            String reasonText = "";

            switch (status) {
                case DownloadManager.STATUS_FAILED:
                    statusText = "STATUS_FAILED";
                    switch (reason) {
                        case DownloadManager.ERROR_CANNOT_RESUME:
                            reasonText = "ERROR_CANNOT_RESUME";
                            break;
                        case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                            reasonText = "ERROR_DEVICE_NOT_FOUND";
                            break;
                        case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                            reasonText = "ERROR_FILE_ALREADY_EXISTS";
                            break;
                        case DownloadManager.ERROR_FILE_ERROR:
                            reasonText = "ERROR_FILE_ERROR";
                            break;
                        case DownloadManager.ERROR_HTTP_DATA_ERROR:
                            reasonText = "ERROR_HTTP_DATA_ERROR";
                            break;
                        case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                            reasonText = "ERROR_INSUFFICIENT_SPACE";
                            break;
                        case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                            reasonText = "ERROR_TOO_MANY_REDIRECTS";
                            break;
                        case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                            reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                            break;
                        case DownloadManager.ERROR_UNKNOWN:
                            reasonText = "ERROR_UNKNOWN";
                            break;
                    }
                    Log.e("Downloader", statusText + " " + reasonText);
                    cursor.close();
                    return false;
                case DownloadManager.STATUS_PAUSED:
                    statusText = "STATUS_PAUSED";
                    switch (reason) {
                        case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                            reasonText = "PAUSED_QUEUED_FOR_WIFI";
                            break;
                        case DownloadManager.PAUSED_UNKNOWN:
                            reasonText = "PAUSED_UNKNOWN";
                            break;
                        case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                            reasonText = "PAUSED_WAITING_FOR_NETWORK";
                            break;
                        case DownloadManager.PAUSED_WAITING_TO_RETRY:
                            reasonText = "PAUSED_WAITING_TO_RETRY";
                            break;
                    }
                    Log.d("Downloader", statusText + " " + reasonText);
                    break;
                case DownloadManager.STATUS_PENDING:
                    statusText = "STATUS_PENDING";
                    Log.d("Downloader", statusText + " " + reasonText);
                    break;
                case DownloadManager.STATUS_RUNNING:
                    statusText = "STATUS_RUNNING";
                    Log.d("Downloader", statusText + " " + reasonText);
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    statusText = "STATUS_SUCCESSFUL";
                    reasonText = "Filename:\n" + filename;
                    Log.d("Downloader", statusText + " " + reasonText);
                    break;
            }
        }

        cursor.close();
        return true;
    }
}