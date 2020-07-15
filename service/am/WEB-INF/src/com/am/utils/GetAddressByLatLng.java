package com.am.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.Var;
import com.fastunit.util.Checker;

public class GetAddressByLatLng {

	private Logger logger = LoggerFactory.getLogger(getClass());

	public String getAddress(String lat, String lng) {
		String rValue = "解析失败";// 返回的地址
		// 获取变量，取得url
		String url = Var.get("amap_getadress_url");
		url = url.replaceAll("lat", lat).replaceAll("lng", lng);
		String rJsonStr = HttpRequest.sendGet(url, "");
		if(!Checker.isEmpty(rJsonStr)){
			JSONObject json = new JSONObject(rJsonStr);
			String info = json.getString("info");
			
			if (info.equals("OK")) {
				rValue = json.getJSONObject("regeocode").get("formatted_address").toString();
				if(rValue.equals("[]")){
					rValue = "解析失败";
				}
				} else {
				logger.info("高德请求失败，失败原因=" + info + ",请参考http://lbs.amap.com/api/webservice/guide/tools/info");
			}
		}
		

		return rValue;
	}

	// 通过地址获取坐标
	public String getLngLat(String address) {

		// 获取变量，取得url
		String url = Var.get("amap_getlnglat");
		url = url.replaceAll("myAddress", address);

		// 发送请求，取得高德返回的json字符串
		String sLatLngJson = HttpRequest.sendGet(url, "");

		System.out.println("===============");
		logger.debug("GetAddressByLatLng.getLngLat(1).url=" + url);
		logger.debug("GetAddressByLatLng.getLngLat().sLatLngJson=" + sLatLngJson);
		System.out.println("===============");

		JSONObject json = new JSONObject(sLatLngJson);
		String info = json.getString("info");

		// 定义经纬度，返回的格式(108.946784,34.321377)中间用逗号隔开
		String lnglat = "";
		if (info.equals("OK")) {
			JSONArray lnglataaa = json.getJSONArray("geocodes");
			for (int i = 0; i < lnglataaa.length(); i++) {
				lnglat = lnglataaa.getJSONObject(i).getString("location");
			}
		} else {
			logger.error("高德请求失败，失败原因=" + info + ",请参考http://lbs.amap.com/api/webservice/guide/tools/info");
			lnglat = "解析失败";
		}

		return lnglat;
	}
	@Test
	public void test(){
		String rValue="";
		String rJsonStr = HttpRequest.sendGet("http://restapi.amap.com/v3/geocode/regeo?key=a5bf76c78efebd788d3c59731012c1d5&location=102.60864760995835,15.81369921967888&poitype=&radius=&extensions=base&batch=false&roadlevel=0", "");
		if(!Checker.isEmpty(rJsonStr)){
			JSONObject json = new JSONObject(rJsonStr);
			String info = json.getString("info");
			
			if (info.equals("OK")) {				
				rValue =  json.getJSONObject("regeocode").get("formatted_address").toString();
				
				} else {
				logger.info("高德请求失败，失败原因=" + info + ",请参考http://lbs.amap.com/api/webservice/guide/tools/info");
			}
		}
		System.err.println(rValue);
	}

}
