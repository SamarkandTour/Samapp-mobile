package uz.samtuit.samapp.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import uz.samtuit.sammap.main.R;

/**
 * Common Dialog to turn on GSP
 */
public class GPSSettingDialog extends ActionBarActivity {
    int pressedVal;
    DialogInterface.OnClickListener mClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        enableGPSSetting(this);

        setResult(pressedVal);


    }
    //public GPSSettingDialog() {}

    public static int checkGPSStatus(Context context) {
        int gpsStatus = 0;

        try {
            // LOCATION_MODE_HIGH_ACCURACY=3, LOCATION_MODE_BATTERY_SAVING=2, LOCATION_MODE_SENSORS_ONLY=1 or LOCATION_MODE_OFF=0.
            gpsStatus = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            Log.d("GPS", "isGPSEnabled():LOCATION_MODE:" + gpsStatus);

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return gpsStatus;
    }

    private void enableGPSSetting(final Context context) {
        if(checkGPSStatus(this) == 0) { // LOCATION_MODE_OFF
            AlertDialog.Builder gsDialog = new AlertDialog.Builder(context);
            gsDialog.setTitle(R.string.title_dialog_gps_setting);
            gsDialog.setMessage(R.string.dialog_gps_setting);
            gsDialog.setPositiveButton(R.string.yes, mClick);
            gsDialog.setNegativeButton(R.string.no, mClick);
            gsDialog.show();
        }

        mClick = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    // Go to the GPS setting
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(intent);
                    pressedVal = 1;
                    finish();
                } else {
                    pressedVal = 0;
                    finish();
                }
            }

        };
    }

}
