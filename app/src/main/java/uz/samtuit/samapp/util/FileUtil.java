package uz.samtuit.samapp.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * File handling Utilities
 */
public class FileUtil {

    public static String fileReadFromExternal(Context context, String fileName) {
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

    public static boolean fileWriteToExternal(Context context, String fileName, String content) {
        File file = new File(context.getExternalFilesDir(null), fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  true;
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
}
