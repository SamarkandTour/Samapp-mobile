package uz.samtuit.samapp.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.cocoahero.android.geojson.FeatureCollection;
import com.cocoahero.android.geojson.GeoJSON;
import com.mapbox.mapboxsdk.util.DataLoadingUtils;
import com.mapbox.mapboxsdk.util.constants.UtilConstants;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * File handling Utilities
 */
public class FileUtil {

    public static String fileReadFromExternalDir(Context context, String fileName) {
        BufferedReader input = null;
        File file = null;
        StringBuffer buffer = new StringBuffer();

        try {
            file = new File(context.getExternalFilesDir(null), fileName); // Pass getFilesDir() and "MyFile" to read file
            input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;

            while ((line = input.readLine()) != null) {
                buffer.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return buffer.toString();
    }

    public static boolean createDirectoryInExternalDir(Context context, String dirName) {
        File dir = new File(context.getExternalFilesDir(null), dirName);

        if (!dir.mkdirs()) {
            return false;
        }
        return true;
    }

    public static boolean fileWriteToExternalDir(Context context, String fileName, String content) {
        File file = new File(context.getExternalFilesDir(null), fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static boolean CopyFilesFromAssetToExternalDir(Context context, String[] listFiles) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }

        String root = context.getExternalFilesDir(null).getAbsolutePath();

        for (String filename : listFiles) {
            String destpath = root + "/" + filename;
            File f = new File(destpath);
            if (f.exists()) {
                continue;
            }

            AssetManager am = context.getAssets();
            try {
                InputStream is = am.open("data/" + filename);
                FileOutputStream os = new FileOutputStream(destpath);
                byte buffer[] = new byte[1024];
                for ( ; ; ) {
                    int read = is.read(buffer);
                    if (read <= 0) break;
                    os.write(buffer, 0, read);
                }
                is.close();
                os.close();
            } catch (IOException e) {
                return false;
            }
        }

        return  true;
    }

    public static FeatureCollection loadFeatureCollectionfromExternalGeoJSONFile(final Context context, final String fileName)  throws IOException, JSONException {
        if (TextUtils.isEmpty(fileName)) {
            throw new NullPointerException("No GeoJSON File Name passed in.");
        }

        if (UtilConstants.DEBUGMODE) {
            Log.d(DataLoadingUtils.class.getCanonicalName(), "Mapbox SDK loading GeoJSON URL: " + fileName);
        }

        FileInputStream fis = new FileInputStream(context.getExternalFilesDir(null).getAbsolutePath() + "/" + fileName);
        BufferedReader rd = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
        String jsonText = DataLoadingUtils.readAll(rd);

        FeatureCollection parsed = (FeatureCollection) GeoJSON.parse(jsonText);
        if (UtilConstants.DEBUGMODE) {
            Log.d(DataLoadingUtils.class.getCanonicalName(), "Parsed GeoJSON with " + parsed.getFeatures().size() + " features.");
        }

        return parsed;
    }

    public static String[] getFileNameListWithFilter(Context context, final String filter) {
        File file = new File(context.getExternalFilesDir(null).getAbsolutePath());
        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.startsWith(filter);
            }
        };

        return file.list(filenameFilter);
    }
}
