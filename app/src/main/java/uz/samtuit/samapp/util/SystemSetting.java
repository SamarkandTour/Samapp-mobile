package uz.samtuit.samapp.util;

import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

/**
 * Utility for System settings
 */
public class SystemSetting {
    public static int checkGPSStatus(Context context) {
        int gpsStatus = 0;
        boolean isGpsEnabled = false;

        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // LOCATION_MODE_HIGH_ACCURACY=3, LOCATION_MODE_BATTERY_SAVING=2, LOCATION_MODE_SENSORS_ONLY=1 or LOCATION_MODE_OFF=0.
                gpsStatus = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

                Log.d("GPS", "checkGPSStatus():LOCATION_MODE:" + gpsStatus);
            } else {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                return isGpsEnabled ? 1 : 0;
            }

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
