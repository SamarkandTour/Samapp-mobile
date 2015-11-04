package uz.samtuit.samapp.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;

import uz.samtuit.sammap.main.R;

/**
 * Navigation View to guide you to the destination
 */
public class NavigationView extends View {
    private Drawable mCompass;
    private float mAzimuth;
    private float mDistance;
    private float mBearing;
    private float mAntipodal;
    private int PADDING = 2;

    public NavigationView(Context ctx) {
        super(ctx);

        this.mCompass = ctx.getResources().getDrawable(R.drawable.arrow_n);
    }

    protected void onDraw(Canvas canvas) {
        canvas.save();

        // Because the degree of Azimuth is counter-clockwise, should be with minus(-)
        // Because the value of bearing is given as True north, Compensate difference of both Magnetic north and True north
        canvas.rotate(-(mAzimuth+mAntipodal) + mBearing, PADDING + mCompass.getMinimumWidth()
                / 2, PADDING + mCompass.getMinimumHeight() / 2);
        canvas.drawText(getDistanceString(), PADDING + mCompass.getMinimumWidth()
                / 2, mCompass.getMinimumHeight(),new Paint(Color.BLACK));
        mCompass.setBounds(PADDING, PADDING, PADDING
                + mCompass.getMinimumWidth(), PADDING
                + mCompass.getMinimumHeight());

        mCompass.draw(canvas);
        canvas.restore();

        super.onDraw(canvas);
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
            return Float.toString(mDistance / 10000) + " km"; // Calculate to the second decimal place
        } else {
            return Integer.toString((int) mDistance) + " m";
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
