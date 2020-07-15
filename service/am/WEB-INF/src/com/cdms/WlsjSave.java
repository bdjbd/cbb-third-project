package com.cdms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.p2p.service.IWebApiService;

public class WlsjSave implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		String start_time=request.getParameter("start_time");
		String end_time=request.getParameter("end_time");
		String wlid=request.getParameter("wlid");
		String id=request.getParameter("id");
		
		DBManager db=new DBManager();	
	   String isql="insert into cdms_fencetimeslot (id,start_time,end_time,fence_id) "
	   		+ "VALUES('"+id+"','"+start_time+"','"+end_time+"','"+wlid+"')";
	    db.execute(isql);
		return"成功";
	}

}
