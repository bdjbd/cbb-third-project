package com.am.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.webapi.db.DBManager;
import com.p2p.service.IWebApiService;

/**
 * 获取管理端变量
 * @author 王成阳
 * 2018-4-4
 */
public class AvarApi implements IWebApiService{

	public String execute(HttpServletRequest request, HttpServletResponse response) {
		
		JSONObject resultJson = new JSONObject();
		DBManager db = new DBManager();
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		String vid = "";
		
		//查询所有后台变量
		String selectSQL = "select vid,vname,vvalue,remark from avar";
		jsonArray = db.queryToJSON(selectSQL);
		
		for (int i = 0; i < jsonArray.length(); i++) {

			try {
				
				jsonObject = jsonArray.getJSONObject(i);
				vid = jsonObject.getString("VID");
				resultJson.put(vid, jsonObject);
			
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		
		return resultJson.toString();
	}

}
