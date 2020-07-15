package com.cdms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * 监控页面查询故障码
 * 
 * @author guorenjie
 *
 */
public class Monitoring_gz implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		String data2 = request.getParameter("cph");

		JSONObject objectJson = null;
		JSONObject objectJson1 = new JSONObject();
		JSONArray array = new JSONArray();
		String sql = "select (select alarm_code from cdms_faultalarmtype where id=alarm.fault_class_alarm_type),(select fatname from cdms_faultalarmtype where id=alarm.fault_class_alarm_type),alarm_time from cdms_alarmrecord alarm where car_id=(select id from cdms_vehiclebasicinformation where alarm_type='1' and license_plate_number='"
				+ data2 + "')";
		DBManager db = new DBManager();
		MapList mapList = db.query(sql);
		if (!Checker.isEmpty(mapList)) {
			for (int i = 0; i < mapList.size(); i++) {
				objectJson = new JSONObject();
				objectJson.put("alarm_code", mapList.getRow(i).get("alarm_code"));
				objectJson.put("fatname",mapList.getRow(i).get("fatname"));
				objectJson.put("alarm_time", mapList.getRow(i).get("alarm_time"));

				array.put(objectJson);
			}
			objectJson1.put("total", 1);
			objectJson1.put("rows", array);
		}else{
			objectJson = new JSONObject();
			array.put(objectJson);
			objectJson1.put("total", 1);
			objectJson1.put("rows", array);
		}
		return objectJson1.toString();
	}

}
