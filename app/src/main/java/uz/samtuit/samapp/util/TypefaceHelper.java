package uz.samtuit.samapp.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Bakha on 19.02.2016.
 */
public class TypefaceHelper {
    public static void setViewGroupTypeface(ViewGroup container, Typeface typeface) {
        final int children = container.getChildCount();

        for (int i = 0; i < children; i++) {
            View child = container.getChildAt(i);
            if (child instanceof TextView) {
                setTextViewTypeface((TextView) child, typeface);
            } else if (child instanceof ViewGroup) {
                setViewGroupTypeface((ViewGroup) child, typeface);
            }
        }
    }

    public static void setTextViewTypeface(TextView textView, Typeface typeface) {
        textView.setTypeface(typeface);
    }

    public static Typeface getTypeface(Context context){
        return Typeface.createFromAsset(context.getAssets(), "font/Roboto-Thin.ttf");
    }

    public static Typeface getTypeface(Context context, String TypefaceName){
        return Typeface.createFromAsset(context.getAssets(), "font/" +TypefaceName+".ttf");
    }
}

