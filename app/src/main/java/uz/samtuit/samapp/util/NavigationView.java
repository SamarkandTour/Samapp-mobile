package uz.samtuit.samapp.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;

import uz.samtuit.samapp.main.R;

/**
 * Navigation View to guide you to the destination
 */
public class NavigationView extends View {
    private Drawable mCompass;
    private float mAzimuth;
    private float mDistance;
    private float mBearing;
    private float mAntipodal;
    private float PADDING;
    private int deviceWidth;
    private int deviceHeight;
    private int left, right, top, bottom;
    private Paint textPaint;

    public NavigationView(Context ctx) {
        super(ctx);

        this.mCompass = ctx.getResources().getDrawable(R.drawable.navigation_compass);
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float density = metrics.density;

        deviceWidth = metrics.widthPixels;
        deviceHeight = metrics.heightPixels;
        left = (deviceWidth - mCompass.getMinimumWidth()) / 2;
        top = (deviceHeight - mCompass.getMinimumHeight()) / 2;
        right = left + mCompass.getMinimumWidth();
        bottom = top + mCompass.getMinimumHeight();
        PADDING = density * 47;

        textPaint = new Paint();
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        textPaint.setTextSize(16 * density);
        textPaint.setAntiAlias(true);
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

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        // Because the degree of Azimuth is counter-clockwise, should be with minus(-)
        // Because the value of bearing is given as True north, Compensate difference of both Magnetic north and True north
        canvas.rotate(-(mAzimuth + mAntipodal) + mBearing, deviceWidth / 2, deviceHeight / 2);
        mCompass.setBounds(left, top, right, bottom);

        String distanceText = getDistanceString();
        int startPoint = (mCompass.getBounds().width() - getTextWidth(distanceText)) / 2;
        canvas.drawText(distanceText, left + startPoint, top + PADDING, textPaint);

        mCompass.draw(canvas);
        canvas.restore();
    }

    public void setAzimuth(float aAzimuth) {
        mAzimuth = aAzimuth;
    }

    public void setDeclination(float Declination) {
        mAntipodal = Declination;
    }

    public void setBearing(float bearing) {
        mBearing = bearing;
    }

    private String getDistanceString() {
        if(mDistance > 1000) {
            return Math.round(mDistance/1000 * 10.0) / 10.0 + " km"; // Round up to first decimal place
        } else {
            return (int)mDistance + " m";
        }
    }

    public void setDistance(float distance){
        mDistance = distance;
    }

    public boolean hasDistance(){
        if (mDistance == 0) {
            return false;
        }
        return true;
    }
}
