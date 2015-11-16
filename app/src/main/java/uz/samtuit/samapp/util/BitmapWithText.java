package uz.samtuit.samapp.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;

/**
 * Create Bitmap with Text in the center of the bitmap
 */
public class BitmapWithText extends BitmapDrawable {
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
        arg0.drawBitmap(bitmap, null, this.getBounds(), textPaint);
        textPaint.setTypeface(Typeface.create((String)null, Typeface.BOLD));
        textPaint.setTextSize(18 * density);
        textPaint.setTypeface(Typeface.DEFAULT);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);

        int textWidth = getTextWidth(name) / 2;
        int centerX = this.getBounds().width() / 2;
        int centerY = this.getBounds().height() / 2;
        arg0.drawText(name, centerX - textWidth, centerY, textPaint);
    }

    int getTextWidth(String text) {
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
