package uz.samtuit.samapp.util;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Bitmap Utilities
 */
public class BitmapUtil {

    // Decode Base64 encoded image
    public static Bitmap decodeBase64Image(String encodedImage) {

        final BitmapFactory.Options options = new BitmapFactory.Options();



        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return decodedByte;
    }

    public static Bitmap decodeBase64Bitmap(String encodedImage,
                                                         float reqWidth, float reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);

        BitmapFactory.decodeByteArray(decodedString,0,decodedString.length,options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length,options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, float reqWidth, float reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * Create Bitmap with Text in the center of the bitmap
     */
    public static class BitmapWithText extends BitmapDrawable {
        private String name;
        private Bitmap bitmap;
        private Paint textPaint;
        private float density;

        public BitmapWithText(Context ctx, String strName, int bitmapId) {
            super(BitmapFactory.decodeResource(ctx.getResources(), bitmapId));
            bitmap = BitmapFactory.decodeResource(ctx.getResources(), bitmapId);
            name = strName;
            this.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
            this.setTargetDensity(metrics);
            density = metrics.density; //mdpi=1, hdpi=1.5, xhdpi=2, xxhdpi=2.5

            textPaint = new Paint();
            textPaint.setTypeface(Typeface.create((String) null, Typeface.BOLD));
            textPaint.setTextSize(18 * density);
            textPaint.setTypeface(Typeface.DEFAULT);
            textPaint.setAntiAlias(true);
            textPaint.setStyle(Paint.Style.FILL);
        }

        @Override
        public void draw(Canvas canvas) {
            Paint bitmapPaint = new Paint();
            bitmapPaint.setAlpha(127);
            canvas.drawBitmap(bitmap, null, this.getBounds(), bitmapPaint);

            int textWidth = getTextWidth(name) / 2;
            int centerX = this.getBounds().width() / 2;
            int centerY = this.getBounds().height() / 2;
            canvas.drawText(name, centerX - textWidth, centerY, textPaint);
        }

        private int getTextWidth(String text) {
            int count = text.length();
            float[] widths = new float[count];
            textPaint.getTextWidths(text, widths);
            int textWidth = 0;

            for (int i = 0; i < count; i++) {
                textWidth += widths[i];
            }

            return textWidth;
        }
    }

    /**
     * Draw rounded Drawable image
     */
    public static class RoundedDrawable extends Drawable {
        private final Bitmap mBitmap;
        private final Paint mPaint;
        private final RectF mRectF;
        private final int mBitmapWidth;
        private final int mBitmapHeight;
        private boolean mRoundedRect;
        private int mRadius;

        public RoundedDrawable(Bitmap bitmap, boolean roundedRect) {
            mBitmap = bitmap;
            mRectF = new RectF();
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            final BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mPaint.setShader(shader);

            mBitmapWidth = mBitmap.getWidth();
            mBitmapHeight = mBitmap.getHeight();

            if (mBitmapWidth > mBitmapHeight) {
                mRadius = mBitmapHeight / 2;
            } else {
                mRadius = mBitmapWidth / 2;
            }

            mRoundedRect = roundedRect;
        }

        @Override
        public void draw(Canvas canvas) {
            if (mRoundedRect) {
                canvas.drawRoundRect(mRectF, 20, 20, mPaint);
            } else {
                canvas.drawCircle(mRectF.centerX(), mRectF.centerY(), mRadius, mPaint);
            }
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);

            mRectF.set(bounds);
        }

        @Override
        public void setAlpha(int alpha) {
            if (mPaint.getAlpha() != alpha) {
                mPaint.setAlpha(alpha);
                invalidateSelf();
            }
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            mPaint.setColorFilter(cf);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        @Override
        public int getIntrinsicWidth() {
            return mBitmapWidth;
        }

        @Override
        public int getIntrinsicHeight() {
            return mBitmapHeight;
        }

        public void setAntiAlias(boolean aa) {
            mPaint.setAntiAlias(aa);
            invalidateSelf();
        }

        @Override
        public void setFilterBitmap(boolean filter) {
            mPaint.setFilterBitmap(filter);
            invalidateSelf();
        }

        @Override
        public void setDither(boolean dither) {
            mPaint.setDither(dither);
            invalidateSelf();
        }

        public Bitmap getBitmap() {
            return mBitmap;
        }
    }

    /**
     * Download Manager
     */
    public static class  Downloader {
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
}
