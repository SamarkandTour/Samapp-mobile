package uz.samtuit.samapp.util;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Download Manager
 */
public class Downloader {
    private DownloadManager downloadManager;
    private DownloadManager.Request[] downloadRequests;
    private URL[] serverURLs;
    private String[] fileNames;

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

    public void startDownload(Context context, String title, String desc) {
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
    }
}