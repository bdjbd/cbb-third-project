package com.am.nw.api.location;

import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.am.nw.api.location.util.LocationApiUtil;
import com.fastunit.MapList;
import com.fastunit.util.Checker;

public class UpdateCarAlarm {
	private static Logger logger = LoggerFactory.getLogger(UpdateCarAlarm.class);
	/**
	 * 更新报警表
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
	 * @param TerminalVehicleRpt
	 *            本次行驶结束数据
	 * @return boolean true:更新成功 false：更新失败
	 */
	public static boolean updateCarAlarm(String driver,String carId,LocationApiUtil util,DBManager db, String terminalId,
			JSONObject alarmFlag, JSONObject status, JSONObject location,
			JSONObject appendInfo, JSONObject TerminalVehicleRpt) {
		
		boolean rValue = false;
		String id = UUID.randomUUID().toString();
		String key = "id,car_id,driver";// insert sql中的字段名
		String value = "'" + id + "','" + carId + "','" + driver + "'";// insert
		String updateParms = "";// update sql中的参数
		int count = 0;// sql执行成功影响的行数

		String datetime = null;// 时间
		double mileage = 0;// 里程
		double oilmass = 0;// 油量
		int rapidAccelerationCnt = 0;// 本次急加速次数
		int rapidDecelerationCnt = 0;// 本次急减速次数
		int sharpTurnCnt = 0;// 本次急转弯次数
		int steepRoadCnt = 0;// 本次急变道次数
		String alarmTypeId = "";// 报警类型id
		String alarmFenLeiId = "";// 报警分类id
		String fatname = "";// 报警内容
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

			// 如果实时位置数据包不为空
			if (location.length() > 0) {			
				updateParms += "end_time='" + util.formatDateTime(location.getString("datetime")) + "'";
				updateParms += ",time_length=extract(epoch from to_timestamp('"
						+ location.getString("datetime")
						+ "','YYYYmmDDHH24miSS'))-extract(epoch from to_timestamp(to_char(start_time, 'YYYYmmDDHH24miSS'),'YYYYmmDDHH24miSS'))";// 本次时长";
				
			}
			// 如果附加实时信息数据包不为空
			if (appendInfo.length() > 0) {
				mileage = appendInfo.getDouble("mileage");// 里程
				oilmass = util.getOilmassBymileage(db, terminalId,mileage);// 油量
				
				// 报警表中的上次里程
				double start_mileage = util.getLastOil(db, "cdms_alarmrecord", "start_mileage",
						"car_id='" + carId + "' and time_state='1'");
				double diffMileage = mileage-start_mileage;
				if(diffMileage<0){
					diffMileage = 0;
				}
				
				updateParms += ",end_mileage=" + mileage;
				updateParms += ",end_oil=" + oilmass;
				updateParms += ",mileage="+diffMileage;
				updateParms += ",oil=" + util.getOilmassBymileage(db, terminalId,diffMileage);
			}

			// 如果本次行驶结束数据数据包不为空
			if (TerminalVehicleRpt.length() > 0) {
				rapidAccelerationCnt = TerminalVehicleRpt
						.getInt("rapidAccelerationCnt");// 本次急加速次数
				rapidDecelerationCnt = TerminalVehicleRpt
						.getInt("rapidDecelerationCnt");// 本次急减速次数
				sharpTurnCnt = TerminalVehicleRpt.getInt("sharpTurnCnt");// 本次急转弯次数
				steepRoadCnt = TerminalVehicleRpt.getInt("steepRoadCnt");// 本次急变道次数

				// 报警次数不等于0时
				if (rapidAccelerationCnt != 0 || rapidDecelerationCnt != 0
						|| sharpTurnCnt != 0 || steepRoadCnt != 0) 
				{	
					logger.info("报警次数报警！");
					
					if (rapidAccelerationCnt != 0) {
						alarmCount(db,terminalId, carId, driver, location, util, "alarmFlag_32", rapidAccelerationCnt, alarmContent,datetime);
					}
					if (rapidDecelerationCnt != 0) {
						alarmCount(db,terminalId, carId, driver, location, util, "alarmFlag_33", rapidDecelerationCnt, alarmContent,datetime);
					}
					if (sharpTurnCnt != 0) {
						alarmCount(db,terminalId, carId, driver, location, util, "alarmFlag_34", sharpTurnCnt, alarmContent,datetime);
					}
					if (steepRoadCnt != 0) {
						alarmCount(db,terminalId, carId, driver, location, util, "alarmFlag_35", steepRoadCnt, alarmContent,datetime);
					}
				}
			}

			// 如果报警位数据包不为空
			if (alarmFlag.length() > 0) {

				for (int i = 0; i < 32; i++) {
					id = UUID.randomUUID().toString();
					key = "id,car_id,driver";// insert sql中的字段名
					value = "'" + id + "','" + carId + "','" + driver + "'";// insert
					// 如果实时位置数据包不为空
					if (location.length() > 0) {
						key += ",longitude,latitude,alarm_address";
						value += ",'" + location.getDouble("longitude") + "'";// 经度
						value += ",'" + location.getDouble("latitude") + "'";// 纬度
						value += ",'" + location.getString("address") + "'";// 位置
						datetime = util.formatDateTime(location
								.getString("datetime"));// 时间
					}
					
					String aCode = "alarmFlag_" + i;// 报警码
					int alarmValue = alarmFlag.getInt(aCode);// 报警位的值
					

					MapList alarmInfo = util.getAlarmType(db, aCode);
					if (!Checker.isEmpty(alarmInfo)) 
					{	
						
						
						alarmTypeId = alarmInfo.getRow(0).get(0);
						alarmFenLeiId = alarmInfo.getRow(0).get(1);
						fatname = alarmInfo.getRow(0).get(2);
						alarmContent.put("describe", fatname);
						alarmContent.put("title", fatname);

						key += ",frequency,alarm_type,fault_class_alarm_type,alarm_content,operation_status,alarm_time,start_time,start_mileage,start_oil,time_state";
						value += ",1,'" + alarmFenLeiId + "','" + alarmTypeId
								+ "','" + fatname + "','0','" + datetime
								+ "','" + datetime + "'," + mileage + ","
								+ oilmass + ",'1'";
					

						// 报警表中是否有未结束的报警位报警
						boolean flag = util.getNotStopAlarmData(db, carId, alarmTypeId);
						// 报警位值为1，报警开始
						if (1 == alarmValue) {
							
							logger.info("报警=" + aCode +alarmInfo.getText());
							String updateCarState = "update cdms_VehicleBasicInformation set vehicle_state='7' where device_sn_number='" + terminalId+"'";
							db.execute(updateCarState);
							String updatePCarState = "update cdms_vcd_"
									+ terminalId+" set driving_behavior_status='7' where positioning_time='"+datetime+"'";
							db.execute(updatePCarState);
							
							// 如果没有未结束的报警信息，则新增
							if (!flag) {
								String insertSql = "insert into cdms_AlarmRecord ("
										+ key + ") values (" + value + ")";
								count = db.execute(insertSql);
								// 推送报警信息
								//pushAlarm(db, carId, alarmContent);
							}
						}
						//如果报警表中有未结束的此类报警统计，则计算差值
						if(flag){
							String updateSql = "update cdms_AlarmRecord set "
									+ updateParms
									+ " where car_id='"
									+ carId
									+ "' and time_state='1' and fault_class_alarm_type='"
									+ alarmTypeId + "'";
							db.execute(updateSql);
						}
						
						
						// 如果报警位值为0，报警结束
						if(0 == alarmValue) {
							// 报警表中是否有未结束的报警位报警
							// 如果有未结束的报警信息，则更新
							if (flag) {						
								String updateSql = "update cdms_AlarmRecord set time_state='2' "
										+ " where car_id='"
										+ carId
										+ "' and time_state='1' and fault_class_alarm_type='"
										+ alarmTypeId + "'";
								count = db.execute(updateSql);
							}
						}
					}
				}

			}

			if (count > 0) {
				rValue = true;
			}
		return rValue;
	}
	
	/**
	 * 报警次数报警
	 * @param db
	 * @param carId
	 * @param driver
	 * @param location
	 * @param util
	 * @param aCode
	 * @param alarmCount
	 * @param alarmContent
	 * @return
	 */
	private static int alarmCount(DBManager db,String terminalId,String carId,String driver,JSONObject location,LocationApiUtil util,String aCode,int alarmCount,JSONObject alarmContent,String datetime){
		int rValue = 0;
		
		String id = UUID.randomUUID().toString();
		String key = "id,car_id,driver";// insert sql中的字段名
		String value = "'" + id + "','" + carId + "','" + driver + "'";// insert
		// 如果实时位置数据包不为空
		if (location.length() > 0) {

			key += ",longitude,latitude,alarm_address";
			value += ",'" + location.getDouble("longitude")
					+ "'";// 经度
			value += ",'" + location.getDouble("latitude")
					+ "'";// 纬度
			value += ",'" + location.getString("address") + "'";// 位置
			datetime = util.formatDateTime(location
					.getString("datetime"));// 时间
		}
		util.updateAlarmStatus(db, terminalId, carId, aCode,datetime);
		MapList alarmInfo = util.getAlarmType(db, aCode);

		if (!Checker.isEmpty(alarmInfo)) {
			String alarmTypeId = alarmInfo.getRow(0).get(0);
			String alarmFenLeiId = alarmInfo.getRow(0).get(1);
			String fatname = alarmInfo.getRow(0).get(2);
			alarmContent.put("describe", fatname);
			alarmContent.put("title", fatname);

			key += ",alarm_type,fault_class_alarm_type,frequency,alarm_content,alarm_time,operation_status,time_state";
			value += ",'" + alarmFenLeiId + "','" + alarmTypeId
					+ "'," + alarmCount + ",'" + fatname
					+ "','" + datetime + "','0','2'";
			String insertSql = "insert into cdms_AlarmRecord ("
					+ key + ") values (" + value + ")";
			rValue = db.execute(insertSql);
			// 推送报警信息
			//pushAlarm(db, carId, alarmContent);
		}
			
		return rValue;
	}
}
