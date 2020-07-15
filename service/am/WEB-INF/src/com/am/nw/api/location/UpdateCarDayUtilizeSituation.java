package com.am.nw.api.location;

import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.am.nw.api.location.util.LocationApiUtil;
import com.fastunit.MapList;
import com.fastunit.util.Checker;

public class UpdateCarDayUtilizeSituation {
	
	private static Logger logger = LoggerFactory.getLogger(UpdateCarDayUtilizeSituation.class);
	
	/**
	 * 更新车辆天利用情况表
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
	public static boolean updateCarDayUtilizeSituation(String carId,LocationApiUtil util,DBManager db,
			String terminalId, JSONObject alarmFlag, JSONObject status,
			JSONObject location, JSONObject appendInfo) {

		boolean rValue = false;		
			int count = 0;// sql执行成功影响的行数
			String datetime = null;// 时间
			String date = null;// 日期
			double speed = 0;// 速度
			double mileage = 0;// 里程
			double oilmass = 0;// 油耗
			double srppedVar = util.getSpeedVar();
			// 如果实时位置数据包不为空
			if (location.length() > 0) {
				datetime = util.formatDateTime(location.getString("datetime"));// 开始时间
				date = datetime;
				speed = location.getDouble("speed");// 速度
			}
			
			// 如果附加实时信息数据包不为空
			if (appendInfo.length() > 0) {
				mileage = appendInfo.getDouble("mileage");// 里程
				oilmass = util.getOilmassBymileage(db, terminalId,mileage);// 油耗

			}
			//获取当前车辆当天的天利用表数据
			MapList dayUtilization = getDayUtilization(db, carId, date);
			//判断当前车辆当天的天利用表数据是否为空，空则新增，否则更新
			if (Checker.isEmpty(dayUtilization)) {
				//新增
				String id = UUID.randomUUID().toString();
				String key = "id,car_id,date,mileage_statistics,fuel_sum,the_travel_time,the_rest_time,work_fuel,work_haul,word_time,the_operation_fuel,the_operation_mileage,the_operation_time,mileage";// insert
																																																	// sql中的字段名
				String value = "'" + id + "','" + carId + "','" + date + "',0,0,0,0,0,0,0,0,0,0,"+mileage;// insert
				// sql中的字段值
				String insertSql = "insert into cdms_VehicleDayUtilization ("
						+ key + ") values (" + value + ")";
				count = db.execute(insertSql);
			} else {
				//更新
				double oldMileage=dayUtilization.getRow(0).getDouble("mileage", 0.0);
				double thisMileage=mileage-oldMileage;
				
				if(thisMileage<0){thisMileage=0.0;}

				double oilDifference = 0;// 上次油耗减当前油耗的差值
				oilDifference=util.getOilmassBymileage(db, terminalId,thisMileage);// 根据里程计算油量
					
				// update里程和油耗
				String updateSqlParms = "date='" + date
						+ "',mileage_statistics=mileage_statistics+" + thisMileage + ",fuel_sum=fuel_sum+"
						+ oilDifference;

				if (1 == status.getInt("status_0")&&speed > srppedVar) {
					// 运行中(运行时长)
					updateSqlParms += ",the_travel_time=the_travel_time+extract(epoch from to_timestamp('"
							+ location.getString("datetime")
							+ "','YYYYmmDDHH24miSS'))-extract(epoch from to_timestamp(to_char(date, 'YYYYmmDDHH24miSS'),'YYYYmmDDHH24miSS'))";
					// 工作中
					if ("1".equals(getWorkState(db, carId))) {
						// 上次油耗减当前油耗大于0，则更新工作油耗油耗
						if (oilDifference > 0) {
							updateSqlParms += ",work_fuel=work_fuel+"
									+ oilDifference + "";
						}
						//工作里程
						updateSqlParms += ",work_haul=work_haul+" + thisMileage;
						updateSqlParms += ",word_time=word_time+extract(epoch from to_timestamp('"
								+ location.getString("datetime")
								+ "','YYYYmmDDHH24miSS'))-extract(epoch from to_timestamp(to_char(date, 'YYYYmmDDHH24miSS'),'YYYYmmDDHH24miSS'))";
						
					}
					// 案件处理中
					if ("2".equals(getWorkState(db, carId))) {
						//作业油耗
						if (oilDifference > 0) {
							updateSqlParms += ",the_operation_fuel=the_operation_fuel+"
									+ oilDifference + "";
							updateSqlParms += ",work_fuel=work_fuel+"
									+ oilDifference + "";
						}
						//工作里程(作业里程也属于工作里程)
						updateSqlParms += ",work_haul=work_haul+" + thisMileage;
						updateSqlParms += ",word_time=word_time+extract(epoch from to_timestamp('"
								+ location.getString("datetime")
								+ "','YYYYmmDDHH24miSS'))-extract(epoch from to_timestamp(to_char(date, 'YYYYmmDDHH24miSS'),'YYYYmmDDHH24miSS'))";
						//作业里程
						updateSqlParms += ",the_operation_mileage=the_operation_mileage+"+thisMileage;
						//作业时长
						updateSqlParms += ",the_operation_time=the_operation_time+extract(epoch from to_timestamp('"
								+ location.getString("datetime")
								+ "','YYYYmmDDHH24miSS'))-extract(epoch from to_timestamp(to_char(date, 'YYYYmmDDHH24miSS'),'YYYYmmDDHH24miSS'))";
					}
				}
				if(1 == status.getInt("status_0")&&speed<=srppedVar){
					// 静止中(怠速时长)
					updateSqlParms += ",the_rest_time=the_rest_time+extract(epoch from to_timestamp('"
							+ location.getString("datetime")
							+ "','YYYYmmDDHH24miSS'))-extract(epoch from to_timestamp(to_char(date, 'YYYYmmDDHH24miSS'),'YYYYmmDDHH24miSS'))";
				}
				updateSqlParms +=",mileage="+mileage;//本次里程读数，用于下次计算里程差值
				String updateSql = "update cdms_VehicleDayUtilization set "
						+ updateSqlParms + " where car_id='" + carId
						+ "' and to_char(date,'yyyy-mm-dd')='"
						+ date.substring(0, 10) + "'";
				count = db.execute(updateSql);
			}

			if (count > 0) {
				rValue = true;
			}
		return rValue;
	}
	
	/**
	 * 获取当前车辆绑定人的工作状态
	 * 
	 * @param db
	 * @param carId
	 * @return 1:工作中 2：案件处理中 ''：没有绑定人
	 */
	public static String getWorkState(DBManager db, String carId) {
		String rValue = "";
		String sql1 = "select member_id from cdms_VehicleBasicInformation where id='"
				+ carId + "'";
		MapList mapList = db.query(sql1);
		if (!Checker.isEmpty(mapList)) {
			String member_id = mapList.getRow(0).get(0);
			if (!Checker.isEmpty(member_id)) {
				rValue = "1";
				String sql2 = "select work_state from am_member where id='"
						+ member_id + "'";
				MapList mList = db.query(sql2);
				if (!Checker.isEmpty(mList)) {
					String workSate = mList.getRow(0).get(0);
					if ("2".equals(workSate)) {
						rValue = "2";
					}
				}
			}
		}

		return rValue;
	}
	
	/**
	 * 根据车辆id和日期获取车辆天利用表中的数据
	 * 
	 * @param db
	 * @param carId
	 * @param date
	 * @return
	 */
	public static MapList getDayUtilization(DBManager db, String carId, String date) {
		MapList rValue = null;
		String sql = "select fuel_sum,work_fuel,the_operation_fuel,mileage_statistics,mileage from cdms_VehicleDayUtilization where car_id='"
				+ carId
				+ "' and to_char(date,'yyyy-mm-dd')='"
				+ date.substring(0, 10) + "'";
		rValue = db.query(sql);
		return rValue;
	}
}
