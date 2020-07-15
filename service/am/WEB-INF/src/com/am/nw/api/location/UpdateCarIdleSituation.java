package com.am.nw.api.location;

import java.util.UUID;

import org.json.JSONObject;

import com.am.frame.webapi.db.DBManager;
import com.am.nw.api.location.util.LocationApiUtil;
import com.fastunit.MapList;
import com.fastunit.util.Checker;

public class UpdateCarIdleSituation {
	
	/**
	 * 更新车辆闲置情况表，车辆使用情况表中用到（因为只用到了一个闲置时长，就不管是否定位了）
	 * 
	 * @param db
	 * @param terminalId
	 *            终端ID
	 * @param status
	 *            状态
	 * @param location
	 *            实时位置
	 * @return boolean true:更新成功 false：更新失败
	 */
	public static boolean updateCarIdleSituation(String carId,LocationApiUtil util,DBManager db, String terminalId,
			JSONObject status, JSONObject location) {
		
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
				insertKey += ",acc_turn_off_time";
				insertValue += "','"
						+ util.formatDateTime(location.getString("datetime") + "'")
						+ "'";// ACC关闭时间
				updateSqlParms += "acc_turn_on_time='"
						+ util.formatDateTime(location.getString("datetime")) + "'";// ACC开启时间
				updateSqlParms += ",stop_time=extract(epoch from to_timestamp('"
						+ location.getString("datetime")
						+ "','YYYYmmDDHH24miSS'))-extract(epoch from to_timestamp(to_char(acc_turn_off_time, 'YYYYmmDDHH24miSS'),'YYYYmmDDHH24miSS'))";
			}

			// 车辆闲置表中该车是否有ACC=1的记录
			boolean flag = isExistVehicleIdling(db, carId, "1");
			//获取变量设定的速度
			double speedVar = util.getSpeedVar();
			
			// acc的关闭开启状态如果是关闭且速度读数小于设定的变量值
			if (0 == status.getInt("status_0")) {
				if (!flag) {
					insertKey += ",acc_status";
					insertValue += ",'1'";
					String insertSql = "insert into cdms_VehicleIdling ("
							+ insertKey + ") values (" + insertValue + ")";
					count = db.execute(insertSql);
					
				}
				if(speed<=speedVar){
				String updateCarState = "update cdms_VehicleBasicInformation set vehicle_state='5' where device_sn_number='" + terminalId+"'";
				db.execute(updateCarState);
				String updatePCarState = "update cdms_vcd_"
						+ terminalId+" set driving_behavior_status='5' where positioning_time='"+util.formatDateTime(location
								.getString("datetime"))+"'";
							db.execute(updatePCarState);
				}
				
			}
			//如果车辆闲置表中有未结束的停车统计，则计算差值
			if(flag){
				//api每次都算差值，计划任务检查如果车辆离线且统计表中有未结束的数据则结束，结束就是修改状态为2,这里的acc_status不是指acc状态，只是标志怠速是否结束（因为需求更改，acc关闭的时候不算停车了）
				updateSqlParms += ",acc_status='1'";
				String updateSql = "update cdms_VehicleIdling set "
						+ updateSqlParms + " where car_id='" + carId + "' and acc_status='1'  ";
				db.execute(updateSql);
			}
			
			//不满足停车条件
			if (!(0 == status.getInt("status_0"))) {
				if (flag) {
					String updateSql = "update cdms_VehicleIdling set acc_status='2'"
							+ " where car_id='" + carId + "' and acc_status='1'  ";
					count = db.execute(updateSql);
				}
			}

			if (count > 0) {
				rValue = true;
			}
		return rValue;
	}
	
	/**
	 * 依据车辆id搜索车辆闲置情况表中该车辆acc状态=state的记录
	 * 
	 * @param db
	 * @param carId
	 * @return true:有 false:没有
	 */
	public static boolean isExistVehicleIdling(DBManager db, String carId,
			String state) {
		boolean rValue = false;
		String sql = "select id from cdms_VehicleIdling where car_id='" + carId
				+ "' and acc_status='" + state + "'";
		MapList mapList = db.query(sql);
		if (!Checker.isEmpty(mapList)) {
			if (mapList.size() > 0) {
				rValue = true;
			}
		}
		return rValue;
	}
}
