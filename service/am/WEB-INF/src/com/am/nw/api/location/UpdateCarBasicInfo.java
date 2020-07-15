package com.am.nw.api.location;

import org.json.JSONObject;
import com.am.frame.webapi.db.DBManager;
import com.am.nw.api.location.util.LocationApiUtil;
import com.fastunit.MapList;
import com.fastunit.util.Checker;

public class UpdateCarBasicInfo {
	
	/**
	 * 更新车辆基础信息表
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
	 * @param ObdRealTimeDataRpt
	 *            OBD实时数据
	 * @param ObdOverDataRpt
	 *            OBD本次行驶结束数据
	 * @param  rawLocation
	 * 			  终端上报的原始经纬度和上报时间          
	 * @return boolean true:更新成功 false：更新失败
	 */
	public static boolean updateCarBasicInfo(String isloc,LocationApiUtil util,DBManager db, String terminalId,
			JSONObject alarmFlag, JSONObject status, JSONObject location,
			JSONObject appendInfo, JSONObject ObdRealTimeDataRpt,
			JSONObject ObdOverDataRpt, JSONObject rawLocation) {
		
		boolean rValue = false;
		int count = 0;
		
		String updateCarBasicInfoSql = "update cdms_VehicleBasicInformation set device_sn_number='"+ terminalId + "";
		String aCode = util.queryAlarmStatus(db, alarmFlag, status)[0];// 报警码
		String sCode = util.queryAlarmStatus(db, alarmFlag, status)[1];// 状态码
		if(isloc.equals("1")){//定位
			// 如果实时位置数据包不为空
			if (location.length() > 0) {
				updateCarBasicInfoSql += "',lng='"
						+ location.getDouble("longitude")// 经度
						+ "',lat='"
						+ location.getDouble("latitude") // 纬度
						+"',ys_lng='"
						+ location.getDouble("ys_longitude")// 经度
						+ "',ys_lat='"
						+ location.getDouble("ys_latitude") // 纬度
						+ "',hign='"
						+ location.getInt("high") // 高程
						+ "',speed='"
						+ location.getDouble("speed")// 速度
						+ "',direction='"
						+ location.getInt("direc")// 方向
						+ "',location='"
						+ location.getString("address")// 地址
						//wcy 2018-11-9 add
						+ "',ReceiveTime='"
						+ rawLocation.getString("ReceiveTime")// 接收位置包时间
						//wcy 2018-11-9 add end
				;
			}
			
		}
		updateCarBasicInfoSql+="',positioning_time='"
				+ util.formatDateTime(location.getString("datetime")) + "'";// 定位时间
		//wcy 2018-11-9 add
		updateCarBasicInfoSql+=",Acc='"+ status.getInt("status_0") + "'";// 车辆ACC状态
		updateCarBasicInfoSql+=",IsAlarm='"+ alarmFlag.getInt("alarmFlag_0") + "'";// 是否触警
		//wcy 2018-11-9 add end
		// 如果附加实时信息数据包不为空
		if (appendInfo.length() > 0) {
			updateCarBasicInfoSql += ",mileage='"
					+ appendInfo.getDouble("mileage")// 里程
					+ "',oilmass='"
					+ util.getOilmassBymileage(db, terminalId,appendInfo.getDouble("mileage")) // 油量
					+ "',driving_record_speed='"
					+ appendInfo.getInt("drivingRecordspeed")// 速度
					+ "',alarm_event_id='"
					+ appendInfo.getString("alarmEventId") + "'";// 报警事件ID
			
			//更新
			double oldMileage=getOldMileage(db, terminalId);
			double thisMileage=appendInfo.getDouble("mileage")-oldMileage;
			
			if(thisMileage<0){thisMileage=0.0;}
			
			updateCarBasicInfoSql+= ",current_mileage=current_mileage+" + thisMileage; //保养已行驶里程
			
		}

		// 如果OBD实时数据数据包不为空
		if (ObdRealTimeDataRpt.length() > 0) {
			updateCarBasicInfoSql += ",battery_voltage="
					+ ObdRealTimeDataRpt.getInt("levelVoltage")// 电平电压
					+ ",engine_speed="
					+ ObdRealTimeDataRpt.getInt("engineSpeed") // 发动机转速
					+ ",driving_speed="
					+ ObdRealTimeDataRpt.getInt("instantaneousSpeed")// 瞬时车速
					+ ",throttle_opening='"
					+ ObdRealTimeDataRpt.getInt("throttleOpening")
					+ "'"// 节气门开度
					+ ",engine_load='"
					+ ObdRealTimeDataRpt.getInt("engineLoad")
					+ "'"// 发动机负荷
					+ ",coolant_temperature='"
					+ ObdRealTimeDataRpt.getInt("coolantTemperature")
					+ "'"// 冷却液温度
					+ ",instantaneous_fuel_consumption="
					+ ObdRealTimeDataRpt.getInt("instFuelConsumption") // 瞬时油耗
					+ ",total_mileage='"
					+ ObdRealTimeDataRpt.getInt("totalMileage") // 总里程
					+ "',accumulative_oil_consumption="
					+ ObdRealTimeDataRpt.getInt("accOilConsumption") // 累计耗油量
			;
		}

		// 如果OBD本次行驶结束数据数据包不为空
		if (ObdOverDataRpt.length() > 0) {
			updateCarBasicInfoSql += ",average_speed='"
					+ ObdOverDataRpt.getInt("averageSpeed")// 平均车速
					+ "',average_fuel_consumption="
					+ ObdOverDataRpt.getInt("averageFuelConsumption") // 平均油耗
					+ ",travel_mileage="
					+ ObdOverDataRpt.getInt("travelMileage")// 本次行驶里程
					+ ",fuel_consumption="
					+ ObdOverDataRpt.getInt("oilConsumption")// 本次耗油量
			;
		}

		// 故障码不为空时，更新故障码以及故障时间，否则设置故障码为空，故障时间为null
		if (!Checker.isEmpty(aCode)) {
			updateCarBasicInfoSql += ",fault_code='" + aCode.substring(0, aCode.length()-1)
					+ "',fault_time='"
					+ util.formatDateTime(location.getString("datetime")) + "'";
		} else {
			updateCarBasicInfoSql += ",fault_code='',fault_time=null";
		}

		// 状态码不为空时，更新故障码，否则设为空
		if (!Checker.isEmpty(sCode)) {
			updateCarBasicInfoSql += ",status_code='" + sCode.substring(0, sCode.length()-1) + "'";
		} else {
			updateCarBasicInfoSql += ",status_code=''";
		}

		updateCarBasicInfoSql += ",vehicle_state='1',last_heartbeat_time=now()  where device_sn_number='" + terminalId
				+ "'";
		
		count = db.execute(updateCarBasicInfoSql);
		if (count > 0) {
			rValue = true;
		}
		return rValue;
	}
	public static double getOldMileage(DBManager db,String terminalId){
		double rValue = 0;
		String sql = "select mileage from cdms_vehiclebasicinformation where device_sn_number='" + terminalId+ "'";
		MapList mapList = db.query(sql);
		if(!Checker.isEmpty(mapList)){
			rValue = mapList.getRow(0).getDouble(0, 0);
		}
		return rValue;
	}
}
