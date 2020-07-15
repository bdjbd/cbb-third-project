package com.am.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Test;

/**
 * 高德地图工具类
 * 
 * @author guorenjie
 */
public class AmapUtils {
	public static String getHttpResponse(String allConfigUrl) {
		BufferedReader in = null;
		StringBuffer result = null;
		try {
			allConfigUrl = URLDecoder.decode(allConfigUrl, "utf-8");
			// url请求中如果有中文，要在接收方用相应字符转码
			URI uri = new URI(allConfigUrl);
			URL url = uri.toURL();
			URLConnection connection = url.openConnection();
			connection.setRequestProperty("Content-type", "text/html");
			connection.setRequestProperty("Accept-Charset", "utf-8");
			connection.setRequestProperty("contentType", "utf-8");
			connection.connect();
			result = new StringBuffer();
			// 读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
			return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 高德地图WebAPI : 驾车路径规划 计算两地之间行驶的距离(米)<br/>
	 * String origins:起始坐标<br/>
	 * String destination:终点坐标
	 */
	public static String distance(String origins, String destination) {
		int strategy = 0;// 0速度优先（时间）1费用优先（不走收费路段的最快道路）2距离优先 3不走快速路 4躲避拥堵
							// 5多策略（同时使用速度优先、费用优先、距离优先三个策略计算路径）。6不走高速 7不走高速且避免收费
							// 8躲避收费和拥堵 9不走高速且躲避收费和拥堵
		String url = "http://restapi.amap.com/v3/direction/driving?" + "origin=" + origins + "&destination="
				+ destination + "&strategy=" + strategy + "&extensions=base&key=713c91579ad30c8ea86fee997b91ca28";
		JSONObject jsonobject = JSONObject.fromObject(getHttpResponse(url));
		JSONArray pathArray = jsonobject.getJSONObject("route").getJSONArray("paths");
		String distanceString = pathArray.getJSONObject(0).getString("distance");
		return distanceString;
	}

	/**
	 * 高德地图WebAPI : 地址转化为高德坐标 <br/>
	 * String address：高德地图地址
	 * 
	 */
	public static String coordinate(String address) {
		try {
			address = URLEncoder.encode(address, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String url = "http://restapi.amap.com/v3/geocode/geo?address=" + address
				+ "&output=json&key=713c91579ad30c8ea86fee997b91ca28";
		JSONObject jsonobject = JSONObject.fromObject(getHttpResponse(url));
		System.out.println(jsonobject.toString());
		JSONArray pathArray = jsonobject.getJSONArray("geocodes");
		String coordinateString = pathArray.getJSONObject(0).getString("location");
		return coordinateString;
	}

	/**
	 * 高德地图WebAPI : gps坐标转化为高德坐标 <br/>
	 * String coordsys：高德地图坐标
	 * 
	 */
	public static String convert(String coordsys) {
		try {
			coordsys = URLEncoder.encode(coordsys, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String url = "http://restapi.amap.com/v3/assistant/coordinate/convert?locations=" + coordsys
				+ "&coordsys=gps&output=json&key=713c91579ad30c8ea86fee997b91ca28";
		JSONObject jsonobject = JSONObject.fromObject(getHttpResponse(url));
		String coordinateString = jsonobject.getString("locations");
		return coordinateString;
	}

	/**
	 * 测试
	 */
	@Test
	public void test() {
		String origin = "104.043390" + "," + "30.641982";
		String destination = "106.655347" + "," + "31.786691";
		String distance = distance(origin, destination);
		System.out.println("行驶距离-----" + distance);

		String address = "陕西省西安市未央区凤城三路";
		String coordinate = coordinate(address);
		System.out.println("地址转换高德坐标-----" + coordinate);

		String coordsys = "116.481499,39.990475";
		String moordsys = convert(coordsys);
		System.out.println(moordsys);
	}
}
