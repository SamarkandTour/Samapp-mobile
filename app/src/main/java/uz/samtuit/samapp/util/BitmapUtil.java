package uz.samtuit.samapp.util;

import android.content.Context;
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
import android.util.Base64;
import android.util.DisplayMetrics;

/**
 * Bitmap Utilities
 */
public class BitmapUtil {

    // Decode Base64 encoded image
    public static Bitmap decodeBase64Image(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return decodedByte;
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
        }

        @Override
        public void draw(Canvas arg0) {
            Paint bitmapPaint = new Paint();
            bitmapPaint.setAlpha(127);
            arg0.drawBitmap(bitmap, null, this.getBounds(), bitmapPaint);
            textPaint.setTypeface(Typeface.create((String) null, Typeface.BOLD));
            textPaint.setTextSize(18 * density);
            textPaint.setTypeface(Typeface.DEFAULT);
            textPaint.setAntiAlias(true);
            textPaint.setStyle(Paint.Style.FILL);

            int textWidth = getTextWidth(name) / 2;
            int centerX = this.getBounds().width() / 2;
            int centerY = this.getBounds().height() / 2;
            arg0.drawText(name, centerX - textWidth, centerY, textPaint);
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

            mRoundedRect = roundedRect;
        }

        @Override
        public void draw(Canvas canvas) {
            if (mRoundedRect) {
                canvas.drawRoundRect(mRectF, 20, 20, mPaint);
            } else {
                canvas.drawCircle(mRectF.centerX(), mRectF.centerY(), canvas.getHeight(), mPaint);
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
}
