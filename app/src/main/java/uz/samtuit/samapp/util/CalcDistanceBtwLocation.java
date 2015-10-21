package uz.samtuit.samapp.util;

import android.graphics.PointF;

/**
 * Created by MBP on 2015. 10. 16..
 */
public class CalcDistanceBtwLocation {

    public CalcDistanceBtwLocation() {}

    public static String calcDistanceBtwGPSPos(double lat1, double lon1, double lat2, double lon2){
        double EARTH_R, Rad, radLat1, radLat2, radDist;
        double distance, ret;

        EARTH_R = 6371000.0;
        Rad = Math.PI/180;
        radLat1 = Rad * lat1;
        radLat2 = Rad * lat2;
        radDist = Rad * (lon1 - lon2);

        distance = Math.sin(radLat1) * Math.sin(radLat2);
        distance = distance + Math.cos(radLat1) * Math.cos(radLat2) * Math.cos(radDist);
        ret = EARTH_R * Math.acos(distance);

        double rslt = Math.round(Math.round(ret) / 1000);
        String result = rslt + " km";
        if(rslt == 0) result = Math.round(ret) +" m";

        return result;
    }

    /*
    Projection mProjection = new Projection();
    PointF currentPos = mProjection.toMapPixels(currentLatLngPos);
    PointF targetPos = mProjection.toMapPixels(targetLatLngPos);
    getAngle(currnetPos, targetPos);
    */

    public double getAngle(PointF start, PointF end) {
        double dy = end.y-start.y;
        double dx = end.x-start.x;
        double angle = Math.atan(dy/dx) * (180.0/Math.PI);

        if(dx < 0.0) {
            angle += 180.0;
        } else {
            if(dy<0.0) angle += 360.0;
        }

        return angle;
    }
}
