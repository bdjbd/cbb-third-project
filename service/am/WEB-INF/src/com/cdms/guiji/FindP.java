package com.cdms.guiji;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.p2p.service.IWebApiService;

public class FindP implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		String orgid=request.getParameter("orgid");
		String pname="";
		String sql="select p.proname from aorg as a ,province as p where a.province=p.proid and orgid ='"+orgid+"'";
		DBManager db=new DBManager();
		MapList mapList = db.query(sql);
		
		pname=mapList.getRow(0).get("proname");
		return pname;
	}

}
