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

public class CarTrajectory implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		String cph = request.getParameter("cph");
		String starttime = request.getParameter("starttime");
		String endtime = request.getParameter("endtime");
		JSONObject objectJson =null;
		JSONArray array = new JSONArray();
		 String sql="select cd.positioning_time,cd.lng||','||cd.lat as l,ci.license_plate_number "+
		                  "from cdms_vehiclebasicinformation as ci where "+
                          "ci.id=cd.car_id and ci.license_plate_number='"+cph+"'"+
		                  "and cd.positioning_time>=to_date('"+starttime+"','yyyy-mm-dd hh24:mi:ss') "+
                          "and cd.positioning_time<to_date('"+endtime+"','yyyy-mm-dd hh24:mi:ss') "+
		                  "order by cd.positioning_time";
		 DBManager db = new DBManager();
		 	MapList mapList = db.query(sql);
		 	if(!Checker.isEmpty(mapList)){		 		
		 		 for(int i = 0; i < mapList.size(); i++){		
		 			objectJson = new JSONObject();
		 		objectJson.put("l",(mapList.getRow(i).get("l").split(",")));
		 		objectJson.put("cph",mapList.getRow(i).get("license_plate_number"));
		 		objectJson.put("member",mapList.getRow(i).get("name"));
		 		objectJson.put("tel",mapList.getRow(i).get("contact_number"));
		 		objectJson.put("location",mapList.getRow(i).get("location"));
		 		objectJson.put("speed",mapList.getRow(i).get("speed"));		 			 		
		 		objectJson.put("fatname",mapList.getRow(i).get("fatname"));
		 		objectJson.put("positioning_time",mapList.getRow(i).get("positioning_time"));
		 		objectJson.put("total_mileage",mapList.getRow(i).get("total_mileage"));
		 		
		 		 }
		 	}
		return  objectJson.toString();
	}

}
