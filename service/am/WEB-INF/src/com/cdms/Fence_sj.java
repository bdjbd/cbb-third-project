package com.cdms;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

public class Fence_sj implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		String data2= request.getParameter("wlid");	
		System.out.println(data2);
		JSONObject objectJson =null;
		JSONObject objectJson1 = new JSONObject();
		JSONArray array = new JSONArray();
		 String sql="select * from cdms_fencetimeslot where fence_id='"+data2+"'";
		 DBManager db = new DBManager();
		 	MapList mapList = db.query(sql);
		 	if(!Checker.isEmpty(mapList)){		 		
		 		 for(int i = 0; i < mapList.size(); i++){		
		 		objectJson = new JSONObject();
		 		objectJson.put("start_time",mapList.getRow(i).get("start_time"));		 		
		 		objectJson.put("end_time",mapList.getRow(i).get("end_time"));	
		 		objectJson.put("id",mapList.getRow(i).get("id"));		 	
		 		array.put(objectJson);
		 		 }
		 		objectJson1 .put("total",mapList.size());
		 		objectJson1 .put("rows",array);
		 	}
		return objectJson1.toString();
	}

}
