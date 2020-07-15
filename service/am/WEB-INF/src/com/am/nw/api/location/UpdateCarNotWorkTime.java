package com.am.nw.api.location;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.am.nw.api.location.util.LocationApiUtil;
import com.fastunit.MapList;
import com.fastunit.util.Checker;

public class UpdateCarNotWorkTime {
	
	private static Logger logger = LoggerFactory.getLogger(UpdateCarNotWorkTime.class);
	private static LocationApiUtil util = new LocationApiUtil();
	
	/**
	 * 更新车辆非工作时间表
	 * 
	 * @param db
	 * @param terminalId
	 *            终端ID
	 * @param alarmFlag
	 *            报警标志
	 * @param status
	 *            状态
	 * @param location
	 *            实时位置
	 * @param appendInfo
	 *            附加实时信息
	 * @return boolean true:更新成功 false：更新失败
	 */
	public static boolean updateCarNotWorkTime(String driver,String carId,LocationApiUtil util,DBManager db, String terminalId,
			JSONObject status, JSONObject location, JSONObject appendInfo) {
		boolean rValue = false;
		String updateSqlParms = "";
		String id = UUID.randomUUID().toString();
		String key = "id,car_id";// insert sql中的字段名
		String value = "'" + id + "','" + carId;// insert sql中的字段值
		String key1 = "id,car_id,driver";// insert sql中的字段名
		String value1 = "'" + id + "','" + carId + "','" + driver + "'";// insert
		String updateParms = "";// update报警表 sql中的参数
		int count = 0;// sql执行成功影响的行数
		double speed = 0;
		String time = null;
		String datetime = null;// 时间
		double mileage = 0;// 里程
		double oilmass = 0;// 油量
		boolean flag = false;// 数据上报时间是否在正在用车时间段内				
		double differenceOil = 0;// 上次油耗减当前油耗的差值
		// 如果实时位置数据包不为空
		if (location.length() > 0) {
			speed = location.getDouble("speed");// 速度
			time = util.formatDateTime(location.getString("datetime"));// 异常用车开始时间
			flag = isUsualTimeUseCar(db, carId, time);
			datetime = util.formatDateTime(location.getString("datetime"));// 时间
			key += ",abnormal_vehicle_start_time";
			value += "','" + time;// 定位时间
			key1 += ",longitude,latitude,alarm_address";
			value1 += ",'" + location.getDouble("longitude") + "'";// 经度
			value1 += ",'" + location.getDouble("latitude") + "'";// 纬度
			value1 += ",'" + location.getString("address") + "'";// 位置
			updateSqlParms += "abnormal_vehicle_end_time='"
					+ util.formatDateTime(location.getString("datetime")) + "'";// 异常用车结束时间
			updateSqlParms += ",duration_of_the_vehicle=extract(epoch from to_timestamp('"
					+ location.getString("datetime")
					+ "','YYYYmmDDHH24miSS'))-extract(epoch from to_timestamp(to_char(abnormal_vehicle_start_time, 'YYYYmmDDHH24miSS'),'YYYYmmDDHH24miSS'))";// 本次用车时长
		}
		// 如果附加实时信息数据包不为空
		if (appendInfo.length() > 0) {
			mileage = appendInfo.getDouble("mileage");// 里程
			oilmass = util.getOilmassBymileage(db, terminalId,mileage);// 油量

			key += ",abnormal_vehicle_start_mileage,abnormal_vehicle_start_oil";
			value += "'," + mileage;// 异常用车开始里程
			value += "," + oilmass;// 异常用车开始油量
			
			//获得上次里程计算油耗差值
			String sql = "select abnormal_vehicle_start_mileage from cdms_NotworkingVehicleDetail where car_id='"
					+ carId + "' and status='1'";
			MapList mapList = db.query(sql);
			if (!Checker.isEmpty(mapList)) {
				double oldMileage=mapList.getRow(0).getDouble("abnormal_vehicle_start_mileage", 0.0);
				double thisMileage=mileage-oldMileage;//里程差
				
				if(thisMileage<0){thisMileage=0.0;}
				differenceOil=util.getOilmassBymileage(db, terminalId,thisMileage);// 根据里程差计算本次用车油量

				updateSqlParms += ",abnormal_vehicle_end_mileage="
						+ mileage;// 异常用车结束里程
				updateSqlParms += ",mileage="+thisMileage+"";// 本次用车里程
				updateSqlParms += ",abnormal_vehicle_end_oil="
						+ oilmass;// 异常用车结束油量
				if (differenceOil > 0) {
					updateSqlParms += ",oil=" + differenceOil;// 本次用车油量
				}
			}
		}

		JSONObject alarmContent = new JSONObject();// 报警内容
		JSONObject pushData = new JSONObject();
		JSONObject params = new JSONObject();// 报警内容ID
		params.put("id", id);
		alarmContent.put("describe", "");
		alarmContent.put("title", "");
		alarmContent.put("url", "commodity.alarmDetail");
		alarmContent.put("params", params);
		pushData.put("url", "commodity.alarmDetail");
		pushData.put("params", params);
		alarmContent.put("DATA", pushData);
		
		double srppedVar = util.getSpeedVar();
		//更新车辆基础信息表和车辆位置运行表的报警状态
		if(1 == status.getInt("status_0") && speed > srppedVar && !flag){
			util.updateAlarmStatus(db, terminalId, carId, "alarmFlag_36",datetime);
		}
		// acc的关闭开启状态如果是开启,速度大于变量，不在用车时间内，报警表不存在此车辆的未结束的非工作时间报警报警，则新增则新增记录，记为非工作时间用车异常开始
		if (1 == status.getInt("status_0") && speed > srppedVar && !flag&&!util.getNotStopAlarmData(db, carId, "31")) {
			key += ",status,member_id";
			value += ",'1','" + driver + "'";// 异常用车状态
			String insertSql = "insert into cdms_NotworkingVehicleDetail ("
					+ key + ") values (" + value + ")";
				count = db.execute(insertSql);
				logger.info("非工作时间报警开始！");
				String aCode = "alarmFlag_36";
				MapList alarmInfo = util.getAlarmType(db, aCode);
				if (!Checker.isEmpty(alarmInfo)) {
					String alarmTypeId = alarmInfo.getRow(0).get(0);
					String alarmFenLeiId = alarmInfo.getRow(0).get(1);
					String fatname = alarmInfo.getRow(0).get(2);
					alarmContent.put("describe", fatname);
					alarmContent.put("title", fatname);
					key1 += ",frequency,alarm_type,fault_class_alarm_type,alarm_content,operation_status,alarm_time,start_time,start_mileage,start_oil,time_state";
					value1 += ",1,'" + alarmFenLeiId + "','" + alarmTypeId
							+ "','" + fatname + "','0','" + datetime + "','"
							+ datetime + "'," + mileage + "," + oilmass
							+ ",'1'";
						insertSql = "insert into cdms_AlarmRecord (" + key1
								+ ") values (" + value1 + ")";
						count = db.execute(insertSql);
						// 推送报警信息
//							pushAlarm(db, carId, alarmContent);
						
				}
		}
		//如果报警表中有未结束的非工作时间报警统计，则计算非工作时间用车表和报警表的差值
		if(util.getNotStopAlarmData(db, carId, "31")){
			updateSqlParms += ",status='1',member_id='"
					+ driver + "'";
			String updateSql = "update cdms_NotworkingVehicleDetail set "
					+ updateSqlParms + " where car_id='" + carId
					+ "' and status='1'";
			count = db.execute(updateSql);
			
			// 结束里程，违规里程，结束油耗，违规油耗，结束时间，违规时长 update
			updateParms += "time_state='1'";
			updateParms += ",end_time='" + datetime + "'";
			updateParms += ",end_mileage=" + mileage;
			updateParms += ",mileage=" + mileage + "-start_mileage";
			updateParms += ",end_oil=" + oilmass;
			// 如果开始油耗-当前油耗>0，则更新油耗差值，否则不更新
			if (differenceOil > 0) {
				updateParms += ",oil=" + differenceOil;
			}

			updateParms += ",time_length=extract(epoch from to_timestamp('"
					+ location.getString("datetime")
					+ "','YYYYmmDDHH24miSS'))-extract(epoch from to_timestamp(to_char(start_time, 'YYYYmmDDHH24miSS'),'YYYYmmDDHH24miSS'))";// 本次时长";
			updateSql = "update cdms_AlarmRecord set "
					+ updateParms
					+ " where car_id='"
					+ carId
					+ "' and time_state='1' and fault_class_alarm_type='31'";
			count = db.execute(updateSql);
		}
		// acc的关闭开启状态如果是关闭,不在用车时间内且报警表存在此车辆的未结束的非工作时间报警报警则更新记录，记位异常结束
		if (!flag && 0 == status.getInt("status_0")&&util.getNotStopAlarmData(db, carId, "31")) {
			//非工作用车结束
			String updateSql = "update cdms_NotworkingVehicleDetail set status='0' "
					+ "where car_id='" + carId+"' and status='1'";
			count = db.execute(updateSql);
			//非工作时间报警结束
			updateSql = "update cdms_AlarmRecord set time_state='2' where car_id='"
					+ carId+ "' and time_state='1' and fault_class_alarm_type='31'";
			count = db.execute(updateSql);
		}

		// acc的关闭开启状态如果是开启,表里有异常开启的记录(一直异常用车用到了车辆设置的正常时间,认为异常用车结束)
		if (1 == status.getInt("status_0") && flag&&util.getNotStopAlarmData(db, carId, "31")) {
				//非工作用车结束
				String updateSql = "update cdms_NotworkingVehicleDetail set status='0' "
						+ "where car_id='" + carId+"' and status='1'";
				count = db.execute(updateSql);
				//非工作时间报警结束
				updateSql = "update cdms_AlarmRecord set time_state='2' where car_id='"
						+ carId+ "' and time_state='1' and fault_class_alarm_type='31'";
				count = db.execute(updateSql);
		}

		if (count > 0) {
			rValue = true;
		}

		return rValue;
	}
	
	/**
	 * 判断位置上报时间是否在正常用车时间段内
	 * 
	 * @param db
	 * @param carId
	 * @param time
	 * @return true:在正常用车时间段内 false：不在在正常用车时间段内
	 */
	public static boolean isUsualTimeUseCar(DBManager db, String carId, String time) {
		boolean rValue = false;
		String startTime = "";
		String stopTime = "";
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = f.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		String currentTime = format.format(date);
		// 查询该车的用车开始时间(时、分)和用车结束时间(时、分)
		String sql = "select car_statr_time,car_stop_time from cdms_VehicleBasicInformation where id='"
				+ carId + "'";

		MapList mapList = db.query(sql);
		if (!Checker.isEmpty(mapList)) {
			startTime = mapList.getRow(0).get(0);
			stopTime = mapList.getRow(0).get(1);
//			if(Checker.isEmpty(startTime) || Checker.isEmpty(stopTime)){
//				String sql1 = "select car_start_time,car_stop_time from aorg where orgid=(select orgcode from cdms_VehicleBasicInformation where id='"
//						+ carId + "')";
//				MapList mapList1 = db.query(sql1);
//				if (!Checker.isEmpty(mapList1)) {
//					startTime = mapList1.getRow(0).get(0);
//					stopTime = mapList1.getRow(0).get(1);
//
//				}
//			}
			
		}

		if (!Checker.isEmpty(startTime) && !Checker.isEmpty(stopTime)) {

			logger.info(startTime + "\n" + stopTime);
			Date current = util.formatTime(currentTime);
			Date startDate = util.formatTime(startTime);
			Date stopDate = util.formatTime(stopTime);
			logger.info("当前时间=" + current);
			logger.info("开始时间=" + startDate);
			logger.info("结束时间=" + stopDate);
			if (util.compareTime(current, startDate)
					&& util.compareTime(stopDate, current)) {
				rValue = true;
			}
		} else {
			rValue = true;
		}
		return rValue;
	}	
}
