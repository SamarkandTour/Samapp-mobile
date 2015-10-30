package uz.samtuit.samapp.util;

import android.content.ContentResolver;
import android.provider.Settings;
import android.util.Log;

/**
 * Utility for System settings
 */
public class SystemSetting {
    public static int checkGPSStatus(ContentResolver contentResolver) {
        int gpsStatus = 0;

        try {
            // LOCATION_MODE_HIGH_ACCURACY=3, LOCATION_MODE_BATTERY_SAVING=2, LOCATION_MODE_SENSORS_ONLY=1 or LOCATION_MODE_OFF=0.
            gpsStatus = Settings.Secure.getInt(contentResolver, Settings.Secure.LOCATION_MODE);

            Log.d("GPS", "checkGPSStatus():LOCATION_MODE:" + gpsStatus);

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return gpsStatus;
    }

    public static String checkSystemLocale() {
        String systemLocale = System.getProperty("user.language", "en"); // If there is no property, "en" will be default

        return systemLocale;
    }
}
