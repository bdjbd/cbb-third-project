package com.cdms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.p2p.service.IWebApiService;

public class Dzeldelete implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		String id=request.getParameter("id");
		String type=request.getParameter("type");
	   
		DBManager db=new DBManager();
		String cesql="delete  from  cdms_electronicfence where id='"+id+"'";
		String ccsql="delete  from  cdms_fencecars where fence_id='"+id+"'";
		String ctsql="delete  from  cdms_fencetimeslot where fence_id='"+id+"'";
		String ctsql1="delete  from  cdms_fencetimeslot where id='"+id+"'";
		String ccsql1="delete  from  cdms_fencecars where id='"+id+"'";
		 if(type.equals("1")){
			 db.execute(cesql);
			    db.execute(ccsql);
			    db.execute(ctsql);
		 }else if(type.equals("2")){
			 db.execute(ctsql1);
		 }else{
			 db.execute(ccsql1);
		 }
	   
		return"成功";
	}

}
