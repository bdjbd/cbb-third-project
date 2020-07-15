package com.am.nw.api.location;

import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.am.nw.api.location.util.LocationApiUtil;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.util.Checker;

public class UpdateCarRunSituation {
	
	private static Logger logger = LoggerFactory.getLogger(UpdateCarRunSituation.class);
	
	/**
	 * 更新车辆运行情况表，怠速统计表用到
	 * 
	 * @param db
	 * @param terminalId
	 *            终端ID
	 * @param status
	 *            状态
	 * @param location
	 *            实时位置
	 * @param appendInfo
	 *            附加实时信息
	 * @return boolean true:更新成功 false：更新失败
	 */
	public static boolean updateCarRunSituation(String carId,String isloc,LocationApiUtil util,DBManager db, String terminalId,
			JSONObject status, JSONObject location, JSONObject appendInfo) {
		boolean rValue = false;
		String uuid = UUID.randomUUID().toString();
		String insertKey = "id,car_id";// insertsql中拼接的key
		String insertValue = "'" + uuid + "','" + carId;// insertsql中拼接的value
		String updateSqlParms = "";// updatesql中拼接的变量
		int count = 0;// sql执行成功影响的行数
		double speed = 0;//上报的速度读数,用于比较是否大于设置的速度变量
		// 如果实时位置数据包不为空
		if (location.length() > 0) {
			speed = location.getDouble("speed");
			String address = "未定位";
			//因为怠速统计表只显示了时间和位置，时间使用的是当前时间，这里判断如果未定位，地址=未定位就可以了
			if(isloc.equals("1")){
				address = location.getString("address");
			}
			insertKey += ",longitude,latitude,car_location,acc_start_time";
			insertValue += "','" + location.getDouble("longitude");// 经度
			insertValue += "','" + location.getDouble("latitude");// 纬度
			insertValue += "','" + address;// 位置
			insertValue += "','"
					+ util.formatDateTime(location.getString("datetime")) + "'";// ACC开启时间
			updateSqlParms += "longitude='"
					+ location.getDouble("longitude") + "'";
			updateSqlParms += ",latitude='"
					+ location.getDouble("latitude") + "'";
			updateSqlParms += ",car_location='"
					+ address + "'";
			updateSqlParms += ",acc_end_time='"
					+ util.formatDateTime(location.getString("datetime")) + "'";// 结束时间
			updateSqlParms += ",run_time=extract(epoch from to_timestamp('"
					+ location.getString("datetime")
					+ "','YYYYmmDDHH24miSS'))-extract(epoch from to_timestamp(to_char(acc_start_time, 'YYYYmmDDHH24miSS'),'YYYYmmDDHH24miSS'))";// 运行时间
		}

		// 如果附加实时信息数据包不为空
		if (appendInfo.length() > 0) {
			insertKey += ",start_mileage";
			insertValue += "," + appendInfo.getDouble("mileage");// 开始里程
			updateSqlParms += ",end_mileage="
					+ appendInfo.getDouble("mileage");// 结束里程
			updateSqlParms += ",road_haul=(" + appendInfo.getDouble("mileage")
					+ "-start_mileage)";// 行驶里程
		}

		// 车辆闲置表中该车是否有ACC=2的记录
		boolean flag = isExistRunning(db, carId, "2");
		//获取变量设定的速度
		double speedVar = util.getSpeedVar();
		
		// ACC开且速度读数小于设定的变量值
		if (1 == status.getInt("status_0")&&speed<=speedVar) {
			if(!flag){
				insertKey += ",acc_status";
				insertValue += ",'2'";
				String insertSql = "insert into cdms_VehiclesRunningCondition ("
						+ insertKey + ") values (" + insertValue + ")";
				count = db.execute(insertSql);
				
			}
			String updateCarState = "update cdms_VehicleBasicInformation set vehicle_state='6' where device_sn_number='" + terminalId+"'";
			db.execute(updateCarState);
			String updatePCarState = "update cdms_vcd_"
					+ terminalId+" set driving_behavior_status='6' where positioning_time='"+util.formatDateTime(location
								.getString("datetime"))+"'";
			db.execute(updatePCarState);
		}
		//如果车辆运行表中有未结束的怠速统计，则计算差值
		if(flag){
			//api每次都算差值，计划任务检查如果车辆离线且统计表中有未结束的数据则结束，结束就是修改状态为2,这里的acc_status不是指acc状态，只是标志怠速是否结束（因为需求更改，acc关闭的时候不算怠速了）
			updateSqlParms += ",acc_status='2'";
			String updateSql = "update cdms_VehiclesRunningCondition set "
					+ updateSqlParms
					+ " where car_id='"
					+ carId
					+ "' and acc_status='2'";
			db.execute(updateSql);
		}
		
		//ACC开时，1、速度为0-5，则为怠速,不满足怠速条件时结束
		if (!(1 == status.getInt("status_0")&&speed<=speedVar)) {
			if(flag){
				String updateSql = "update cdms_VehiclesRunningCondition set acc_status='1' "
						+ " where car_id='"
						+ carId
						+ "' and acc_status='2'";
				count = db.execute(updateSql);
				deleteData(db, carId);
			}
		}

		if (count > 0) {
			rValue = true;
		}
		return rValue;
	}
	
	/**
	 * 依据车辆id搜索车辆运行情况表中该车辆acc状态=state的记录
	 * 
	 * @param db
	 * @param carId
	 * @return true:有 false:没有
	 */
	public static boolean isExistRunning(DBManager db, String carId, String state) {
		boolean rValue = false;
		String sql = "select id from cdms_vehiclesrunningcondition where car_id='"
				+ carId + "' and acc_status='" + state + "'";
		MapList mapList = db.query(sql);
		if (!Checker.isEmpty(mapList)) {
			if (mapList.size() > 0) {
				rValue = true;
			}
		}
		return rValue;
	}
	
	/**
	 * 行驶里程<变量.记录里程阈值,则删除该记录(车辆运行情况表)
	 * 
	 * @param db
	 * @param carId
	 */
	public static void deleteData(DBManager db, String carId) {
		double mileageThreshold = getMileageThreshold(db);
		if (!(mileageThreshold == 0)) {
			String sql = "select road_haul from cdms_VehiclesRunningCondition where car_id='"
					+ carId + "'";
			MapList mapList = db.query(sql);
			if (!Checker.isEmpty(mapList)) {
				String road_haul = mapList.getRow(0).get(0);
				if (!Checker.isEmpty(road_haul)) {
					double road_haul1 = Double.parseDouble(road_haul);
					// 如果行驶里程小于变量里程阈值,则删除记录
					if (road_haul1 < mileageThreshold) {
						String deleteSql = "delete from cdms_VehiclesRunningCondition where car_id='"
								+ carId + "'";
						db.execute(deleteSql);
					}
				}

			}
		}

	}
	
	/**
	 * 获取变量中的里程阈值
	 * 
	 * @return
	 */
	public static double getMileageThreshold(DBManager db) {
		double rValue = 0;// 里程阈值
		rValue = Var.getDouble("mileage_threshold", 0);
		return rValue;
	}
}
