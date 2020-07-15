package com.cdms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

public class QueryOrgName implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		String orgname="";
		String orgid=request.getParameter("orgid");
		
		DBManager db=new DBManager();
		String sql="select orgname from aorg where orgid='"+orgid+"'";			
		MapList mapList =db.query(sql);
		if(!Checker.isEmpty(mapList)){
			orgname=mapList.getRow(0).get(0);
		}
		return orgname;
	}
}
