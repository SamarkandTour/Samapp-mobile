package uz.samtuit.samapp.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Check Update Manager
 */
public class CheckUpdateManager {
    public CheckUpdateManager() {}

    public boolean isNewUpdate(final Context context, final Handler handler, String url) {
        URLConnection connection = null;
        Timer timer = null;

        try {
            URL serverURL = new URL(url);
            connection = serverURL.openConnection();
            connection.setConnectTimeout(5000); // We will wait until max 5s for connection waiting
            connection.setReadTimeout(5000);

            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message msg = handler.obtainMessage();
                    msg.arg1 = 1;
                    handler.sendMessage(msg);
                }
            }, 5000L); // If this routine doesn't handle within 5s, display connection problem
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        long lastUpdated = context.getSharedPreferences("SamTour_Pref", 0).getLong("last_updated", 0);
        long lastModifiedServer =  connection.getLastModified();

        timer.cancel();

        if (lastUpdated < lastModifiedServer) {
            return true;
        }
        return false;
    }
}
