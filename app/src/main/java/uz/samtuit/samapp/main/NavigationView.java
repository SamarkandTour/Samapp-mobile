package uz.samtuit.samapp.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import uz.samtuit.sammap.main.R;

/**
 * Navigation View to guide you to the destination
 */
public class NavigationView extends View {
    private Drawable mCompass;
    private float mAzimuth = 0;
    private int mDistance = 0;
    private int PADDING = 2;

    public NavigationView(Context ctx) {
        super(ctx);

        this.mCompass = ctx.getResources().getDrawable(R.drawable.arrow_n);
    }

    protected void onDraw(Canvas canvas) {
        canvas.save();

        canvas.rotate(360 - mAzimuth, PADDING + mCompass.getMinimumWidth()
                / 2, PADDING + mCompass.getMinimumHeight() / 2);
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

    public void setDistance(int distance){
        mDistance = distance;
    }
}
