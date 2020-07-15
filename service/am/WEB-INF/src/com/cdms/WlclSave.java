package com.cdms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.p2p.service.IWebApiService;

public class WlclSave implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		String car_id=request.getParameter("car_id");
		String id=request.getParameter("id");
		String wlid=request.getParameter("wlid");		
		DBManager db=new DBManager();	
	   String isql="insert into cdms_fencecars (id,car_id,fence_id) "
	   		+ "VALUES('"+id+"','"+car_id+"','"+wlid+"')";
	    db.execute(isql);
		return"成功";
	}

}
