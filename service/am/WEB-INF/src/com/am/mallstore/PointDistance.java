package com.am.mallstore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;




import org.json.JSONArray;
import org.json.JSONObject;

import com.am.frame.webapi.ActionParame;
import com.am.frame.webapi.IExecuteResAfter;
import com.fastunit.util.Checker;
import com.p2p.map.GpsPointDistance;

/**
 *@author 作者：yangdong
 *@create 时间：2016年6月24日 下午5:04:05
 *@version 说明：计算两个gps点之间的距离
 */
public class PointDistance implements IExecuteResAfter{
	@Override
	public JSONObject execute(HttpServletRequest request,
			HttpServletResponse response, ActionParame param, JSONObject json) {
		
		String lat1 = "";
		String lng1 =  "";
		//当前用户所在地坐标
		
		try{
			String params = request.getParameter("params");
			JSONObject js = new JSONObject(params);
			String lat2 =  js.getString("lat");
			String lng2 =  js.getString("lng");
			JSONArray ja = json.getJSONArray("DATA");
			if(ja.length()>0){
				JSONObject obj = new JSONObject();
				for(int i = 0; i< ja.length(); i ++){
					//解析json
					obj= ja.getJSONObject(i);
					lat1 = obj.getString("LATITUDE");
					lng1 = obj.getString("LONGITUD");
					
					//计算两点之间的距离
					GpsPointDistance gpd = new GpsPointDistance();
					double distance = 0;
					if(!Checker.isEmpty(lat1)&&!Checker.isEmpty(lng1)&&!Checker.isEmpty(lat2)&&!Checker.isEmpty(lng2)){
						distance = GpsPointDistance.getDistance(Double.parseDouble(lat1),Double.parseDouble(lng1), Double.parseDouble(lat2), Double.parseDouble(lng2));
					}
					
					//重新组装json
					ja.getJSONObject(i).put("DISTANCE", distance);
				}
				json.putOpt("DATA",ja);	
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return json;
		
	}
}
