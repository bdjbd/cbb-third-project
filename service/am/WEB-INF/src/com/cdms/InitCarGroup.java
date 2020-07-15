package com.cdms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

public class InitCarGroup implements IWebApiService {
	private MapList rValues = new MapList();
	private int count = 0;
	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		String orgid=request.getParameter("orgid");
		DBManager db=new DBManager();
		String orgname = "";
		String sql = "select orgname from aorg where orgid='"+orgid+"'";
		MapList mapList = db.query(sql);
		if(!Checker.isEmpty(mapList)){
			orgname = mapList.getRow(0).get("orgname");
		}
		rValues.put(0, "name", orgname);
		rValues.put(0, "value", orgid);
		getOrg(db, "", orgid);
		JSONArray ja=new JSONArray();
		JSONObject jObject = null;
		if(!Checker.isEmpty(rValues)){
			for (int i = 0; i < rValues.size(); i++) {
				jObject = new JSONObject();
				jObject.put("ORGID", rValues.getRow(i).get("value"));
				jObject.put("ORGNAME", rValues.getRow(i).get("name"));
				ja.put(jObject);
			}
		}
		
		return ja.toString();
	}
	public void getOrg(DBManager db,String prefix,String parentid) {
		count++;
		prefix+="&nbsp;&nbsp;";
		String sql = "select orgid as value,orgname as name,parentid from aorg where parentid='"+parentid+"'";
		MapList mapList = db.query(sql);
		if(!Checker.isEmpty(mapList)){
			for (int i = 0; i < mapList.size(); i++) {
				String name = prefix+mapList.getRow(i).get("name");
				String value = mapList.getRow(i).get("value");
				rValues.put(count, "name", name);
				rValues.put(count, "value", value);
				getOrg(db,prefix,value);
			}
			
		}
		
		
	}
}
