package com.p2p.base;


//地图管理类
public class MapManager 
{
	//地球半径，单位千米
	private  double EARTH_RADIUS = 6378.137;//地球半径
	
	private  double rad(double d)
	{
	   return d * Math.PI / 180.0;
	}

	public  double GetDistance(double lat1, double lng1, double lat2, double lng2)
	{
	   double radLat1 = rad(lat1);
	   double radLat2 = rad(lat2);
	   double a = radLat1 - radLat2;
	   double b = rad(lng1) - rad(lng2);

	   double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) 
			   + Math.acos(radLat1)*Math.acos(radLat2)*Math.pow(Math.sin(b/2),2)));
	   
	   s = s * EARTH_RADIUS;
	   s = Math.round(s * 10000) / 10000;
	   
	   return s;
	}
	
	/*
	 * c# 代码 替换相应Java函数
	double s = 2 * Math.Asin(Math.Sqrt(Math.Pow(Math.Sin(a/2),2) +
    Math.Cos(radLat1)*Math.Cos(radLat2)*Math.Pow(Math.Sin(b/2),2)));
          对应关系如下：
    Math.Asin=
    Math.Sqrt=
    Math.Pow=
    Math.Sin=
    Math.Cos=
	 * */

}
