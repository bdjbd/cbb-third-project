package com.am.frame.webapi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.p2p.service.IWebApiService;

public class SaveDataOfTable implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) 
	{
		String tTableName = request.getParameter("tablename");
		String tKeyName = request.getParameter("keyname");
		String tValue = request.getParameter("value");

		JSONObject obj = new JSONObject();
		DBManager db = new DBManager();

		String tSql = "select * from " + tTableName + " where " + tKeyName + "='" + tValue + "'";

		try 
		{
			MapList mlData = db.query(tSql);
				
			obj.put("CODE", "1");
			obj.put("MSG", "查询成功");
			obj.put("DATA", db.mapListToJSon(mlData));
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

		return obj.toString();
	}

}
