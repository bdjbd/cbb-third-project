package com.cdms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.dispatcher.AmResServlet;
import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

public class Fence implements IWebApiService {
	private static final Logger log = LoggerFactory.getLogger(AmResServlet.class);
	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		String data2= request.getParameter("orgid");	
		log.debug(data2);
		int pageSize=Integer.parseInt( request.getParameter("pageSize"));
		int pageNumber=Integer.parseInt( request.getParameter("pageIndex"));
		int page=(pageNumber-1)*pageSize;
		JSONObject objectJson =null;
		JSONObject objectJson1 = new JSONObject();
		JSONArray array = new JSONArray();
		 String sql1="select cd.electronic_fence_name,cd.electronic_fence_range,cd.id,cd.orgcode,a.orgname as orgname,(case when electronic_fence_type='1' then '监控进围栏' when electronic_fence_type='2' then '监控出围栏' end) as type from cdms_electronicfence as cd ,aorg as a  where cd.orgcode=a.orgid  and orgcode = '"+data2+"'";
		 String sql="select cd.electronic_fence_name,cd.electronic_fence_range,cd.id,cd.orgcode,a.orgname as orgname,(case when electronic_fence_type='1' then '监控进围栏' when electronic_fence_type='2' then '监控出围栏' end) as type from cdms_electronicfence as cd ,aorg as a  where cd.orgcode=a.orgid  and orgcode = '"+data2+"' limit 10 offset "+page+" ";
		 DBManager db = new DBManager();
		 	MapList mapList = db.query(sql);
		 	MapList mapList1 = db.query(sql1);
		 	if(!Checker.isEmpty(mapList)){		 		
		 		 for(int i = 0; i < mapList.size(); i++){		
		 		objectJson = new JSONObject();
		 		objectJson.put("electronic_fence_name",mapList.getRow(i).get("electronic_fence_name"));		 		
		 		objectJson.put("orgname",mapList.getRow(i).get("orgname"));
		 		objectJson.put("id",mapList.getRow(i).get("id"));
		 		objectJson.put("type",mapList.getRow(i).get("type"));	
		 		objectJson.put("zonename",mapList.getRow(i).get("zonename"));	
		 		objectJson.put("orgcode",mapList.getRow(i).get("orgcode"));	
		 		objectJson.put("electronic_fence_range",mapList.getRow(i).get("electronic_fence_range"));	
		 		array.put(objectJson);
		 		 }
		 		objectJson1 .put("page",pageNumber);
		 		objectJson1 .put("total",mapList1.size());
		 		objectJson1 .put("rows",array);
		 	}
		return objectJson1.toString();
	}

}
