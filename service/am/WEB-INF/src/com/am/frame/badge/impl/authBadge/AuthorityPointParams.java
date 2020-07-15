package com.am.frame.badge.impl.authBadge;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author YueBin
 * @create 2016年6月13日
 * @version 
 * 说明:<br />
 * 社员权限徽章参数，权限参数
 */
public class AuthorityPointParams {

	/**可以访问路由**/
	private JSONArray ACCESS_ROUTE;
	/**获得经营分红权**/
	private boolean GET_OOD;
	/**获得经营分红比例**/
	private double OOD_RATIO;
	
	public AuthorityPointParams(){}
	
	public AuthorityPointParams(JSONObject params){
		if(params!=null){
			try {
				this.GET_OOD=params.getBoolean("GET_OOD");
				this.OOD_RATIO=params.getDouble("OOD_RATIO");
				this.ACCESS_ROUTE=params.getJSONArray("ACCESS_ROUTE");
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public JSONObject toJOSN(){
		JSONObject result=new JSONObject();
		try {
			result.put("ACCESS_ROUTE",ACCESS_ROUTE);
			result.put("GET_OOD",GET_OOD);
			result.put("OOD_RATIO",OOD_RATIO);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	public JSONArray getACCESS_ROUTE() {
		return ACCESS_ROUTE;
	}
	public void setACCESS_ROUTE(JSONArray aCCESS_ROUTE) {
		ACCESS_ROUTE = aCCESS_ROUTE;
	}
	public boolean isGET_OOD() {
		return GET_OOD;
	}
	public void setGET_OOD(boolean gET_OOD) {
		GET_OOD = gET_OOD;
	}
	public double getOOD_RATIO() {
		return OOD_RATIO;
	}
	public void setOOD_RATIO(double oOD_RATIO) {
		OOD_RATIO = oOD_RATIO;
	}
	
	
}
