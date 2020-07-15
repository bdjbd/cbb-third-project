package com.cdms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

public class Monitoring implements IWebApiService {
	Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		String cph = request.getParameter("cph");// 地图上添加的车辆
		
		JSONArray array = new JSONArray();
		JSONObject objectJson = null;
		DBManager db = new DBManager();
		
		String sql = "select cv.lng,cv.lat,cv.license_plate_number,cv.location,cv.speed,cv.positioning_time,cv.mileage,cv.vehicle_state,cv.direction,am.name,am.contact_number,"
				+ "(case when cv.vehicle_state='7' then '触警' else '未触警' end ) as  fatname from cdms_vehiclebasicinformation as cv left join am_member as am on cv.member_id=am.id "
				+ "where 1=1 ";

		// 所选车牌不为空
		if (!Checker.isEmpty(cph)) {
			cph = cph.substring(0, cph.length() - 1);
			sql += "and cv.license_plate_number in (" + cph + ") ";
			put(db, sql, objectJson, array);
		}
		return array.toString();
	}
	public void put(DBManager db,String sql,JSONObject objectJson,JSONArray array){
		MapList mapList = db.query(sql);
		if (!Checker.isEmpty(mapList)) {
			for (int i = 0; i < mapList.size(); i++) {
				objectJson = new JSONObject();
				objectJson.put("l",
						(mapList.getRow(i).get("lng") + "," + mapList.getRow(i)
								.get("lat")).split(","));
				objectJson.put("cph",
						mapList.getRow(i).get("license_plate_number"));
				objectJson.put("member", mapList.getRow(i).get("name"));
				objectJson.put("tel", mapList.getRow(i).get("contact_number"));
				objectJson.put("location", mapList.getRow(i).get("location"));
				objectJson.put("speed", mapList.getRow(i).get("speed"));

				objectJson.put("fatname", mapList.getRow(i).get("fatname"));
				objectJson.put("positioning_time",
						mapList.getRow(i).get("positioning_time"));
				objectJson.put("mileage",
						mapList.getRow(i).get("mileage"));
				objectJson.put("vehicle_state",
						mapList.getRow(i).get("vehicle_state"));
				objectJson.put("direction", mapList.getRow(i).get("direction"));
				array.put(objectJson);
			}
		}
	}
}
