package com.am.nw.api.location;

import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.am.nw.api.location.util.LocationApiUtil;
import com.fastunit.util.Checker;

public class UpdateCarPositionRun {

	static Logger log = LoggerFactory.getLogger(UpdateCarPositionRun.class);
	
	/**
	 * 更新车辆位置运行表，轨迹用到
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
	 * @param  rawLocation
	 * 			  终端上报的原始经纬度和上报时间               
	 * @return boolean true:更新成功 false：更新失败
	 */
	public static boolean updateCarPositionRun(String carId,String isloc,LocationApiUtil util,DBManager db, String terminalId,
			JSONObject alarmFlag, JSONObject status, JSONObject location,
			JSONObject appendInfo, JSONObject ObdRealTimeDataRpt, JSONObject rawLocation) {
		
		
		log.debug(">>>>>>>>>>>>>>>>>>>>>>");
		log.debug(rawLocation.toString());
		log.debug("<<<<<<<<<<<<<<<<<<<<<<");
		
		boolean rValue = false;			
		// 检查数据库是否存在cdms_vcd_terminalId这张表
		String checkTableExistSql = "select concat('1',(select to_regclass('cdms_vcd_"
				+ terminalId + "') is not null)) as result";
		String isTableExist = db.query(checkTableExistSql).getRow(0).get(0);
		// 如果不存在则创建
		if ("1f".equals(isTableExist)) {
			createTable(db, terminalId);
		}
		String id = UUID.randomUUID().toString();
		String key = "id,car_id,driving_behavior_status";// insert sql中的字段名
		String value = "'" + id + "','" + carId+"','1";// insert sql中的字段值

		String aCode = util.queryAlarmStatus(db, alarmFlag, status)[0];// 报警码
		String sCode = util.queryAlarmStatus(db, alarmFlag, status)[1];// 状态码
		// 故障码
		if (!Checker.isEmpty(aCode)) {
			key += ",alarm_status";
			value += "','" + aCode.substring(0, aCode.length()-1);
		}

		// 状态码
		if (!Checker.isEmpty(sCode)) {
			key += ",state_of_vehicle";
			value += "','" + sCode.substring(0, sCode.length()-1);
		}
		// 如果实时位置数据包不为空
		if (location.length() > 0) {
			key += ",lng,lat,location,hign,speed,direction,positioning_time,ys_lat,ys_lng,ReceiveTime";
			value += "','" + location.getDouble("longitude");// 经度
			value += "','" + location.getDouble("latitude");// 纬度
			value += "','" + location.getString("address");// 位置
			value += "','" + location.getInt("high");// 高程
			value += "','" + location.getDouble("speed");// 速度
			value += "','" + location.getInt("direc");// 方向
			value += "','" + util.formatDateTime(location.getString("datetime"));// 定位时间
			//2018-11-9 wcy add 
			value += "','" + rawLocation.getDouble("ys_latitude");// 纬度
			value += "','" + rawLocation.getDouble("ys_longitude");// 经度
			value += "','" + rawLocation.getString("ReceiveTime");// 接收位置包时间
			//2018-11-9 wcy add end 
		}

		// 如果附加实时信息数据包不为空
		if (appendInfo.length() > 0) {
			key += ",current_mileage,oilmass,drivingRecordspeed,alarmEventId";
			value += "','" + appendInfo.getDouble("mileage");// 里程
			value += "','" + util.getOilmassBymileage(db, terminalId,appendInfo.getDouble("mileage"));// 油量
			value += "','" + appendInfo.getInt("drivingRecordspeed");// 速度
			value += "','" + appendInfo.getString("alarmEventId");// 报警事件ID
		}

		// 如果OBD实时数据数据包不为空
		if (ObdRealTimeDataRpt.length() > 0) {
			key += ",battery_voltage,engine_speed,driving_speed,throttle_opening,engine_load,coolant_temperature,instantaneous_fuel_consumption,total_mileage,accumulative_oil_consumption";
			value += "'," + ObdRealTimeDataRpt.getInt("levelVoltage");// 电平电压
			value += "," + ObdRealTimeDataRpt.getInt("engineSpeed");// 发动机转速
			value += "," + ObdRealTimeDataRpt.getInt("instantaneousSpeed");// 瞬时车速
			value += ",'" + ObdRealTimeDataRpt.getInt("throttleOpening");// 节气门开度
			value += "','" + ObdRealTimeDataRpt.getInt("engineLoad");// 发动机负荷
			value += "','"
					+ ObdRealTimeDataRpt.getInt("coolantTemperature");// 冷却液温度
			value += "',"
					+ ObdRealTimeDataRpt.getInt("instFuelConsumption");// 瞬时油耗
			value += "," + ObdRealTimeDataRpt.getInt("totalMileage");// 总里程
			value += "," + ObdRealTimeDataRpt.getInt("accOilConsumption");// 累计耗油量
		}
		// 向终端编号对应的位置运行表中插入一条记录
		String updateCarPositionRun = "insert into cdms_vcd_" + terminalId
				+ "(" + key + ",isloc,Acc,isAlarm) values (" + value + ",'"+isloc+"','"+status.getInt("status_0")+"','"+alarmFlag.getInt("alarmFlag_0")+"')";
		int count = db.execute(updateCarPositionRun);
		if (count > 0) {
			rValue = true;
		}
		return rValue;
	}
	
	/**
	 * 根据终端编号为其创建对应的位置运行表
	 * 
	 * @param db
	 * @param terminalId
	 *            终端ID
	 */
	public static void createTable(DBManager db, String terminalId) {
		String createTableSql = "create table cdms_vcd_"
				+ terminalId
				+ " ("
				+ "id                   VARCHAR(50)          not null,"
				+ "driving_behavior_status               VARCHAR(2)          null,"
				+ "car_id               VARCHAR(50)          null,"
				+ "location             VARCHAR(50)          null,"
				+ "alarm_status         TEXT          		 null,"
				+ "state_of_vehicle     TEXT          		 null,"
				+ "lat             	 	VARCHAR(50)          null,"
				+ "lng  	            VARCHAR(50)          null,"
				+ "hign                 VARCHAR(50)          null,"
				+ "speed                VARCHAR(50)          null,"
				+ "direction            VARCHAR(50)          null,"
				+ "positioning_time     TIMESTAMP WITH TIME ZONE null,"
				+ "current_mileage              varchar(50)          null,"
				+ "oilmass              varchar(50)          null,"
				+ "drivingRecordspeed   varchar(50)          null,"
				+ "alarmEventId         varchar(50)          null,"
				+ "battery_voltage      FLOAT8               null,"
				+ "engine_speed         FLOAT8               null,"
				+ "driving_speed        FLOAT8               null,"
				+ "throttle_opening     VARCHAR(50)           null,"
				+ "engine_load          VARCHAR(50)           null,"
				+ "coolant_temperature  VARCHAR(50)           null,"
				+ "instantaneous_fuel_consumption FLOAT8               null,"
				+ "total_mileage        FLOAT8               null,"
				+ "accumulative_oil_consumption FLOAT8               null,"
				+ "is_offline           VARCHAR(2)           null,"
				+ "offline_reason       VARCHAR(2000)        null,"
				+ "is_working           VARCHAR(10)          null,"
				+ "member_id            VARCHAR(50)          null,"
				+ "is_case              VARCHAR(10)          null,"
				+ "case_id              VARCHAR(50)          null,"
				+ "isloc                VARCHAR(2)           null,"
				//2018-11-9 wcy add
				+ "ys_lng              	VARCHAR(50)          null,"
				+ "ys_lat              	VARCHAR(50)          null,"
				+ "ReceiveTime          VARCHAR(50)          null,"
				+ "Acc                	VARCHAR(2)           null,"
				+ "isAlarm              VARCHAR(2)           null,"
				//2018-11-9 wcy add end
				+ "constraint PK_CDMS_vcd_"
				+ terminalId
				+ " primary key (id)"
				+ ");"
				+ "comment on table cdms_vcd_"
				+ terminalId
				+ " is'jt808中0x0200中的所有数据及附加消息中的0x01,0x02,0x03,0x04,0x08的所有数据诺维协议中相同的数据也存这里';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".id is 'ID';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".car_id is '车辆ID';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".location is '位置';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".alarm_status is '报警码';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".state_of_vehicle is '状态码';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".lat is '纬度';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".lng is '经度';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".hign is '高程';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".speed is '速度';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".direction is '方向';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".positioning_time is '定位时间';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".current_mileage is '里程';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".oilmass is '油量';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".drivingRecordspeed is '附加实时信息的速度';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".alarmEventId is '报警事件ID';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".battery_voltage is '电瓶电压V';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".engine_speed is '发动机转速rpm';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".driving_speed is '行驶车速km/h';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".throttle_opening is '节气门开度%';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".engine_load is '发动机负荷%';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".coolant_temperature is '冷却液温度℃';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".instantaneous_fuel_consumption is '瞬时油耗L/100km';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".total_mileage is '总里程km';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".accumulative_oil_consumption is '累计耗油量L';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".is_offline is '是否离线:0/1 0：离线 1：在线';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".offline_reason is '离线原因';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".is_working is '1=工作中,0=非工作中 0x0200,获得当前位置时,该车如果绑定人则设置为1,反之设置为0';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".member_id is '当前绑定人id';"
				+

				"comment on column cdms_vcd_"
				+ terminalId
				+ ".is_case is '1=处理中,0=非处理中 0x0200,获得当前位置时,该车如果绑定人正在处理案件则设置为1(也就是绑定人工作状态=工作中),反之设置为0';"
				+

				"comment on column cdms_vcd_" + terminalId
				+ ".case_id is '案件ID';"
				+
				
				"comment on column cdms_vcd_"
				+ terminalId
				+ ".isloc is '是否定位:0/1 0：未定位 1：定位';"
				+
				
				//2018-11-9 add 
				"comment on column cdms_vcd_"
				+ terminalId
				+ ".ys_lng is '终端上报原始经度';"
				+
				
				"comment on column cdms_vcd_"
				+ terminalId
				+ ".ys_lat is '终端上报原始纬度';"
				+
				
				"comment on column cdms_vcd_"
				+ terminalId
				+ ".ReceiveTime is '接收位置包时间';"
				+
				
				"comment on column cdms_vcd_"
				+ terminalId
				+ ".Acc is '车辆Acc状态';"
				+
				
				"comment on column cdms_vcd_"
				+ terminalId
				+ ".isAlarm is '是否触警';"
				//2018-11-9 add end
				;
		db.execute(createTableSql);
	}
}
