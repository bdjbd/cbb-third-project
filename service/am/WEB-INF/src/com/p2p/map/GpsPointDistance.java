package com.p2p.map;


//返回GPS坐标2点之间距离，单位：千米
public class GpsPointDistance 
{
	//地球半径，单位千米
	private static double EARTH_RADIUS = 6378.137; 

    private static double rad(double d) { 

        return d * Math.PI / 180.0; 

    } 

 
    //获得GPS坐标2点之间距离，单位：千米
    public static double getDistance(double lat1, double lng1, double lat2, double lng2) 
    { 

        double radLat1 = rad(lat1); 

        double radLat2 = rad(lat2); 

        double a = radLat1 - radLat2; 

        double b = rad(lng1) - rad(lng2); 

        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) 

                + Math.cos(radLat1) * Math.cos(radLat2) 

                * Math.pow(Math.sin(b / 2), 2))); 

        s = s * EARTH_RADIUS; 

        s = Math.round(s * 10000) / 10000; 

        return s; 

    } 

}
