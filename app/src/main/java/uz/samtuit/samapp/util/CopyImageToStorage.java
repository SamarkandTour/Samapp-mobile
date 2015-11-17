package uz.samtuit.samapp.util;

import android.content.Context;

public class CopyImageToStorage {

    public void CopyToExternalFiles(Context context, String fileName, String base64String)
    {
        FileUtil fileUtil = new FileUtil();
        fileUtil.fileWrite(context, fileName, base64String);
    }
}
