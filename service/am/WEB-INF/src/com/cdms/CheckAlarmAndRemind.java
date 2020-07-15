package com.cdms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * 检查报警和提醒
* @author guorenjie  
* @date 2018年5月2日
 */
public class CheckAlarmAndRemind implements IWebApiService {
	Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject rValue = new JSONObject();
		DBManager db = new DBManager();

		String params = request.getParameter("params");
		JSONObject json = new JSONObject(params);
		String orgid = json.getString("orgid");// orgid

		String alarmSql = "select c.id from cdms_alarmrecord a,cdms_vehiclebasicinformation c"
				+ " where a.car_id=c.id"
				+ " and operation_status='0'"
				+ " and c.orgcode like '" + orgid + "%';";
		MapList alarmMapList = db.query(alarmSql);
		if (!Checker.isEmpty(alarmMapList)) {
			rValue.put("alarm_msg", "有未读报警");
			rValue.put("alarm_value", true);
		} else {
			rValue.put("alarm_msg", "无未读报警");
			rValue.put("alarm_value", false);
		}
		//保养提醒
		MapList byMapList = getBY(db, orgid);
		//保险提醒
		MapList bxMapList = getBX(db, orgid);
		// 年检提醒
		MapList njMapList = getNJ(db, orgid);

		if (!Checker.isEmpty(byMapList) || !Checker.isEmpty(bxMapList)
				|| !Checker.isEmpty(njMapList)) {
			rValue.put("remind_msg", "有未读提醒");
			rValue.put("remind_value", true);
		} else {
			rValue.put("remind_msg", "无未读提醒");
			rValue.put("remind_value", false);
		}

		return rValue.toString();
	}
	
	/**
	 * 获取保养提醒
	* @param @param orgid
	* @param @return    参数  
	* @return MapList    返回类型  
	* @throws
	 */
	public MapList getBY(DBManager db,String orgid){
		MapList rValue = null;

		String sql = "select license_plate_number," + "device_sn_number," + "sim_card_number," + "current_mileage, (maintenance_mileage-current_mileage) as residue,"
				+ "to_char(maintenance_mileage_time,'yyyy-MM-dd HH24:MI:SS') as maintenance_mileage_time "
				+ "from CDMS_VEHICLEBASICINFORMATION " + "where orgcode like '" + orgid+ "%' "
				+ "and current_mileage>=reminder_mileage "
				+ "order by  (maintenance_mileage-current_mileage)";
		rValue = db.query(sql);
		return rValue;
		
	}
	
	/**
	 * 获取保险提醒
	* @param @param orgid
	* @param @return    参数  
	* @return MapList    返回类型  
	* @throws
	 */
	public MapList getBX(DBManager db,String orgid){
		MapList rValue = null;
		// 保险提醒
		// 查询变量表，取得保险提前提醒天数
		int bxremindTime = Var.getInt("insurance", 0);
	
		String sql = "select * from (select " + "id," + "license_plate_number," + "device_sn_number,"
				+ "sim_card_number,"
				+ "to_char(duration_of_insurance,'yyyy-MM-dd HH24:MI:SS') as duration_of_insurance,"
				+ "(select DATE_PART('day',duration_of_insurance-now()) from CDMS_VEHICLEBASICINFORMATION where id=a.id) insurancelastday,"
				+ "orgcode " + "from CDMS_VEHICLEBASICINFORMATION a) temp where orgcode like '" + orgid+ "%' "
				+ "and insurancelastday<=" + bxremindTime + " "
				+ "order by insurancelastday";
		rValue = db.query(sql);
		return rValue;
		
	}
	
	/**
	 * 获取年检提醒
	* @param @param orgid
	* @param @return    参数  
	* @return MapList    返回类型  
	* @throws
	 */
	public MapList getNJ(DBManager db,String orgid){
		MapList rValue = new MapList();
		// 年检提醒
		int remindTime = Var.getInt("annual_inspection",0);
		String sql = "select * from "
				+ "(select id,license_plate_number,device_sn_number,sim_card_number,annual_inspection_time,orgcode,"
				+ "DATE_PART('day',(annual_inspection_time + (annual_inspection_interval||'y')::interval)-now()) as annualinspectionlastday,"
				+ "to_char((annual_inspection_time + (annual_inspection_interval||'y')::interval),'yyyy-MM-dd HH24:MI:SS') as annualInspectionMaturity "
				+ "from CDMS_VEHICLEBASICINFORMATION) temp " + "where orgcode like '" + orgid+ "%' "
				+ "and annualinspectionlastday<='" + remindTime + "' "
				+ "order by annualinspectionlastday";
		rValue = db.query(sql);
		return rValue;
		
	}

}
