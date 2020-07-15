package com.cdms;

import java.text.DecimalFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

public class Monitoring_ss implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		String data2= request.getParameter("cph");
		DecimalFormat f = new DecimalFormat("#.000000");
		JSONObject objectJson =null;
		JSONObject objectJson1 = new JSONObject();
		JSONArray array = new JSONArray();
		 String sql="select cv.*,(select orgname from aorg as a where a.orgid=cv.orgcode and cv.license_plate_number ='"+data2+"') as orgname ,(select name from am_member as am, cdms_vehiclebasicinformation as cv where cv.member_id=am.id and cv.license_plate_number ='"+data2+"') as name ,(select contact_number from am_member as am, cdms_vehiclebasicinformation as cv where cv.member_id=am.id and cv.license_plate_number ='"+data2+"')  as contact_number ,(case when cv.vehicle_state='7' then '<a target=\"_blank\" href=\"cdms_alarmrecord.do?name=报警查询管理\">触警</a>' else '未触警' end ) as  fatname from cdms_vehiclebasicinformation as cv where  cv.license_plate_number ='"+data2+"'";
		 DBManager db = new DBManager();
		 	MapList mapList = db.query(sql);
		 	if(!Checker.isEmpty(mapList)){
		 		int direction = 0;//方向
		 		String dir = "";//方向
		 		 for(int i = 0; i < mapList.size(); i++){		
		 			objectJson = new JSONObject();
		 			direction = mapList.getRow(i).getInt("direction", 0);
		 			if(direction==0){
		 				dir="北";
		 			}
		 			if(direction>0&&direction<90){
		 				dir="东北";
		 			}
		 			if(direction==90){
		 				dir="东";
		 			}
		 			if(direction>90&&direction<180){
		 				dir="东南";
		 			}
		 			if(direction==180){
		 				dir="南";
		 			}
		 			if(direction>180&&direction<270){
		 				dir="西南";
		 			}
		 			if(direction==270){
		 				dir="西";
		 			}
		 			if(direction>270&&direction<360){
		 				dir="西北";
		 			}
		 		objectJson.put("l",(f.format(mapList.getRow(i).getDouble("lng", 0))+","+f.format(mapList.getRow(i).getDouble("lat",0))).split(","));
		 		objectJson.put("cph",mapList.getRow(i).get("license_plate_number"));
		 		objectJson.put("orgenam",mapList.getRow(i).get("orgname"));
		 		objectJson.put("member",mapList.getRow(i).get("name"));
		 		objectJson.put("tel",mapList.getRow(i).get("contact_number"));
		 		objectJson.put("location",mapList.getRow(i).get("location"));
		 		objectJson.put("speed",mapList.getRow(i).get("speed"));		 				 		
		 		objectJson.put("fatname",mapList.getRow(i).get("fatname"));
		 		objectJson.put("positioning_time",mapList.getRow(i).get("positioning_time"));
		 		objectJson.put("mileage",mapList.getRow(i).get("mileage"));
		 		objectJson.put("direction",dir);
		 		objectJson.put("battery_voltage",mapList.getRow(i).getDouble("battery_voltage", 0));
		 		objectJson.put("travel_mileage",mapList.getRow(i).getDouble("travel_mileage", 0));
		 		objectJson.put("total_mileage",mapList.getRow(i).getDouble("total_mileage", 0));
		 		objectJson.put("fuel_consumption",mapList.getRow(i).getDouble("fuel_consumption", 0));
		 		objectJson.put("accumulative_oil_consumption",mapList.getRow(i).getDouble("accumulative_oil_consumption", 0));
		 		objectJson.put("driving_speed",mapList.getRow(i).getDouble("driving_speed", 0));
		 		objectJson.put("oilmass",mapList.getRow(i).getDouble("oilmass", 0));

		 		objectJson.put("instantaneous_fuel_consumption",mapList.getRow(i).getDouble("instantaneous_fuel_consumption", 0));
		 		objectJson.put("average_fuel_consumption",mapList.getRow(i).getDouble("average_fuel_consumption", 0));
		 		objectJson.put("engine_speed",mapList.getRow(i).getDouble("engine_speed", 0));
		 		objectJson.put("throttle_opening",mapList.getRow(i).get("throttle_opening"));
		 		objectJson.put("engine_load",mapList.getRow(i).get("engine_load"));
		 		objectJson.put("coolant_temperature",mapList.getRow(i).get("coolant_temperature"));
		 		objectJson.put("fault_code",mapList.getRow(i).get("fault_code"));
		 		objectJson.put("fault",mapList.getRow(i).get("fault"));
		 		objectJson.put("fault_time",mapList.getRow(i).get("fault_time"));
		 	
		 		
		 		array.put(objectJson);
		 		 }
		 		objectJson1 .put("total",1);
		 		objectJson1 .put("rows",array);
		 	}
		return objectJson1.toString();
	}

}
