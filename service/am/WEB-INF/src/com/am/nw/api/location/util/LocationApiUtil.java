package com.am.nw.api.location.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.push.server.JPushNotice;
import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.util.Checker;

public class LocationApiUtil {
	private Logger logger = LoggerFactory.getLogger(getClass());
	/**
	 * 通过终端编号获取carID以及车牌号码(各表均使用)
	 * 
	 * @param terminalId
	 *            终端编号
	 * @return rValue = carId,如果carId为空,则此终端不存在
	 */
	public JSONObject getCarInfoByTerminalId(DBManager db, String terminalId) {

		JSONObject rValue = new JSONObject();
		String sql = "select id,license_plate_number,fault_code from cdms_VehicleBasicInformation where device_sn_number='"
				+ terminalId + "'";
		MapList mapList = db.query(sql);

		if (!Checker.isEmpty(mapList)) {
			rValue.put("carId", mapList.getRow(0).get(0));
			rValue.put("carNumber", mapList.getRow(0).get(1));
			if(!Checker.isEmpty(mapList.getRow(0).get(2))){
				rValue.put("alarm_code", mapList.getRow(0).get(2));
			}
			
		}

		return rValue;
	}
	
	/**
	 * 判断车辆基础信息表（车辆位置运行表）中的报警码是否为空，如果不为空，后续的报警需要拼逗号
	 * @param db
	 * @param terminalId
	 * @return
	 */
	public boolean isEmptyAlarm(DBManager db, String terminalId){
		boolean rValue = false;
		JSONObject carInfo = getCarInfoByTerminalId(db, terminalId);
		if(carInfo.has("alarm_code")){
			rValue = true;
		}
		return rValue;
	}

	/**
	 * 遍历json中的报警以及状态信息(车辆基础信息表和位置运行表使用)
	 * 
	 * @param alarmJsonObject
	 * @param statusJsonObject
	 * @return rValue = [报警码,状态码]
	 */
	public String[] queryAlarmStatus(DBManager db, JSONObject alarm,
			JSONObject status) {
		String alarmStr = "";
		String statusStr = "";
		String[] rValue = new String[2];// [报警码,状态码]

		String key1 = "alarmFlag";
		String key2 = "status";
		if (alarm.length() > 0 && status.length() > 0) {
			for (int i = 0; i < 32; i++) {
				int value1 = alarm.getInt(key1 + "_" + i);// 报警值
				int value2 = status.getInt(key2 + "_" + i);// 状态值

				// 如果报警码的值为1,报警码=alarmFlag_i
				if (value1 == 1) {
					alarmStr += key1 + "_" + i+",";	
				}
				// 如果状态码的值为0,则状态码=status_i_0
				// 如果状态码的值为1,状态码=status_i_1
				if (value2 == 1) {
					statusStr += key2 + "_" + i + "_1,";
				}
				if (value2 == 0) {
					statusStr += key2 + "_" + i + "_0,";
				}
			}
		}
		rValue[0] = alarmStr;
		rValue[1] = statusStr;
		return rValue;
	}

	/**
	 * 更新车辆基础信息表和车辆位置运行表的报警状态
	 * @param db
	 * @param terminalId
	 * @param carId
	 * @param aCode
	 */
	public void updateAlarmStatus(DBManager db,String terminalId,String carId,String aCode,String time){
		if(isEmptyAlarm(db, terminalId)){
			aCode = ","+aCode;
		}
		String updateCarState = "update cdms_VehicleBasicInformation set vehicle_state='7',fault_code = concat(fault_code,'"+aCode+"') where device_sn_number='" + terminalId+"'";
		db.execute(updateCarState);
		String updatePCarState = "update cdms_vcd_"
				+ terminalId+" set driving_behavior_status='7',alarm_status=concat(alarm_status,'"+aCode+"') where positioning_time='"+time+"'";
		db.execute(updatePCarState);
	}

	/**
	 * 获取当前车辆的驾驶员ID(报警表，进出区域表，非工作时间表使用)
	 * 
	 * @param db
	 * @param carId
	 * @return
	 */
	public String getDriver(DBManager db, String carId) {
		String rValue = "";
		String sql = "select member_id from cdms_vehiclebasicinformation where id='"
				+ carId + "'";
		MapList mapList = db.query(sql);
		if (!Checker.isEmpty(mapList.getRow(0).get(0))) {
			rValue = mapList.getRow(0).get(0);
		}
		return rValue;
	}

	/**
	 * 根据报警编码查询字典表中存储的报警类型id,名称以及报警分类id,名称(报警表，进出区域表，非工作时间表使用)
	 * 
	 * @param db
	 * @param alarmCode
	 *            报警编码
	 * @return [报警类型id，报警分类id]
	 */
	public MapList getAlarmType(DBManager db, String alarmCode) {
		MapList rValue = null;
		String sql = "select id,atid,fatname from cdms_faultalarmtype where alarm_code='"
				+ alarmCode + "'";
		MapList mapList = db.query(sql);
		if (!Checker.isEmpty(mapList)) {
			rValue = db.query(sql);
		}
		return rValue;
	}

	/**
	 * 查询报警记录表中是否存在该车辆的未结束报警信息(报警表，进出区域表，非工作时间表使用)
	 * 
	 * @param db
	 * @param carId
	 * @return true:存在 false:不存在
	 */
	public boolean getNotStopAlarmData(DBManager db, String carId,
			String alarmTypeId) {
		boolean rValue = false;
		String sql = "select id from cdms_AlarmRecord where car_id='" + carId
				+ "' and fault_class_alarm_type='" + alarmTypeId
				+ "' and time_state='1'";
		MapList mapList = db.query(sql);
		if (!Checker.isEmpty(mapList)) {
			if (mapList.size() > 0) {
				rValue = true;
			}
		}
		return rValue;
	}

	/**
	 * 为当前车辆的所属机构用户推送报警消息(报警表，进出区域表，非工作时间表使用)
	 * 
	 * @param db
	 * @param carId
	 * @param alarmContent
	 *            推送的内容
	 */
	public void pushAlarm(DBManager db, String carId, JSONObject alarmContent) {
		// 根据当前车辆所在机构的机构id，查询人员，得到同一机构下的所有人员的xtoken注册码
		String memberSql = "select * from mall_mobile_type_record m where m.member_id in (select userid from auser where orgid=(select orgcode from cdms_VehicleBasicInformation where id='"
				+ carId + "'))";
		MapList memberMapList = db.query(memberSql);
		for (int j = 0; j < memberMapList.size(); j++) {

			// 进行消息推送(mobile_type: 手机类型，1为android，2为ios)
			if ("1".equals(memberMapList.getRow(j).get("mobile_type"))) {
				Collection<Object> coll = new LinkedList<Object>();
				coll.add(memberMapList.getRow(j).get("xtoken"));
				// 发送
				JPushNotice.sendNoticeById(alarmContent, coll, "1");
			} else if ("2".equals(memberMapList.getRow(j).get("mobile_type"))) {
				Collection<Object> coll = new LinkedList<Object>();
				coll.add(memberMapList.getRow(j).get("xtoken"));
				// 发送
				JPushNotice.sendNoticeById(alarmContent, coll, "2");
			}
		}
	}

	

	/**
	 * 根据表名，字段名，条件获取上次油耗值(报警表、进出区域表使用)
	 * 
	 * @param db
	 * @param tableName
	 *            表名
	 * @param colmnName
	 *            字段名
	 * @param condition
	 *            条件
	 * @return
	 */
	public double getLastOil(DBManager db, String tableName, String colmnName,
			String condition) {
		double lastOil = 0;
		String sql = "select " + colmnName + " from " + tableName + " where "
				+ condition;
		MapList mapList = db.query(sql);
		if (!Checker.isEmpty(mapList)) {
			lastOil = mapList.getRow(0).getDouble(0, 0);
		}
		return lastOil;
	}

	
	/**
	 * 给时间前面拼接（当前）日期(进出区域表，非工作时间用车使用)
	 * @param time
	 * @return
	 */
	public Date formatTime(String time) {
		Date rValue = null;
		Date now = new Date();
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat fy = new SimpleDateFormat("yyyy-MM-dd");
		String date = fy.format(now);
		try {
			rValue = f.parse(date + " " + time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return rValue;
	}
	
	/**
	 * 比较两个时间的大小(进出区域表，非工作时间用车使用)
	 * 
	 * @param dateTime
	 * @param time
	 * @return true:第一个时间大 false:第二个时间大
	 */
	public boolean compareTime(Date time1, Date time2) {
		boolean rValue = false;
		if (time1.getTime() >= time2.getTime()) {
			rValue = true;
		}

		return rValue;
	}
	
	

	/**
	 * 将上报时间格式化(各表均使用)
	 * 
	 * @param time
	 * @return
	 */
	public String formatDateTime(String time) {
		String rValue = null;
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			rValue = format.format(f.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return rValue;
	}

	
	/**
	 * 查询定位是否启用(基础信息表，位置运行表，会员表使用)
	 * @param db
	 * @param sn_number
	 * @return		true:启用，false:禁用
	 */
	public boolean isNotLocate(JSONObject status){
		boolean rValue = false;
		int s = 0;//默认未定位
		s = status.getInt("status_1");
		if(s==1){
			rValue = true;
		}
		return rValue;
	}
	
	/**
	 * 获取速度变量，(车辆运行情况表，车辆闲置情况表，非工作时间，天利用情况表使用)
	 * @return
	 */
	public double getSpeedVar(){
		double rValue = Var.getDouble("speed", 0);
		return rValue;
	}
	
	/**
	 * 根据里程计算油量,保留一位小数(使用计算油量代替读数油量，各表均使用)
	 * wcy 2018-4-2 added
	 * @param db 
	 * @param sn_number 终端编号
	 * @param thisMileage 里程
	 */
	public double getOilmassBymileage(DBManager db,String sn_number,double thisMileage)
	{
		
		double oilmass=0.0;
		
		//获得当前车辆百公里油耗量
		double hundredKilometersFuelCon = getHundredKilometersFuelConByTerminalId(db,sn_number);
		
		if (thisMileage >= 0) {
			oilmass =  (thisMileage/100)*hundredKilometersFuelCon;
		}
		
//		logger.debug("计算油耗 ApiReportingTerminalLocationData.getOilmassBymileage() oilmass=" + oilmass + " | thisMileage=" + thisMileage);
//		DecimalFormat df = new DecimalFormat("0.0");
//		oilmass = Double.parseDouble(df.format(oilmass));
		
		return oilmass;
	}
	
	/**
	 * 根据终端编号获得百公里油耗(用来计算油量)
	 * wcy 2018-4-2 added
	 * @param sn_number	终端编号
	 * @return hundredKilometersFuelCon	百公里油耗
	 */
	public float getHundredKilometersFuelConByTerminalId(DBManager db,String sn_number) {
		
		String selectSql = "select one_hundred_kilometers_fuel_con from cdms_vehiclebasicinformation where device_sn_number = '"+sn_number+"'";
		
		
		float hundredKilometersFuelCon = 0;
		
		MapList mapList = db.query(selectSql);
		if (!Checker.isEmpty(mapList)) {
			
			hundredKilometersFuelCon = mapList.getRow(0).getFloat(0, 0);
			
			logger.debug("百公里油耗 = " + hundredKilometersFuelCon);
		}
		return hundredKilometersFuelCon;
	}
	
}
