package uz.samtuit.samapp.util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Zip file utility
 */
public class ZipFileUtil {
    public static boolean unZipToExteranlDir(Context context, String srcPath, int mode) {
        try {
            String targetDir = context.getExternalFilesDir(null).getAbsolutePath();
            File zipFile = new File(srcPath);
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry zipEntry = null;

            while ((zipEntry = zis.getNextEntry()) != null) {
                String fileNameInZip = zipEntry.getName();
                File targetFile = new File(targetDir, fileNameInZip);
                FileOutputStream fos = new FileOutputStream(targetFile);

                byte[] buffer = new byte[1024];

                int len;
                for (;;) {
                    len = zis.read(buffer);
                    if (len <= 0) break;
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zis.close();
            if (mode == ZipFile.OPEN_DELETE) {
                zipFile.delete(); // Delete this file when closed
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
