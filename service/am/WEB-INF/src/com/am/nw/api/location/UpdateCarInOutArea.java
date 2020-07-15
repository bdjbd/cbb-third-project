package com.am.nw.api.location;

import java.awt.geom.Point2D;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.am.nw.api.ApiReportingTerminalLocationData;
import com.am.nw.api.location.util.LocationApiUtil;
import com.fastunit.MapList;
import com.fastunit.util.Checker;

public class UpdateCarInOutArea {
	
	private static Logger logger = LoggerFactory.getLogger(UpdateCarInOutArea.class);
	private ApiReportingTerminalLocationData data = new ApiReportingTerminalLocationData();
	
	/**
	 * 更新车辆进出区域表
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
	public static boolean updateCarInOutArea(String driver,LocationApiUtil util,DBManager db, String terminalId,
			JSONObject alarmFlag, JSONObject status, JSONObject location,
			JSONObject appendInfo) {
		boolean rValue = false;
		String carId = util.getCarInfoByTerminalId(db, terminalId).getString("carId");
		// 查询当前车id的围栏,根据车辆的经纬度和围栏的范围判断是否触发围栏(判断一个点是否在多边形中)
		JSONArray array = getFence(db, carId);// 围栏id,围栏范围
		JSONObject fence = null;// 围栏范围json
		String fenceId = "";// 围栏ID
		String range = "";// 围栏范围
		String type = "";//围栏类型
		Date startTime = null;//围栏开始时间
		Date stopTime = null;//围栏结束时间
		int count = 0;// sql执行成功影响的行数
		String updateSqlParms = "";//update区域表 sql中的参数
		String updateParms = "";// update报警表 sql中的参数
		String id = UUID.randomUUID().toString();
		String key = "id,car_id";// insert区域表 sql中的字段名
		String value = "'" + id + "','" + carId;// insert区域表 sql中的字段值
		String key1 = "id,car_id,driver";// insert报警表 sql中的字段名
		String value1 = "'" + id + "','" + carId + "','" + driver + "'";// insert报警表 sql中的字段值
		Point2D.Double point = null;// 当前坐标[经度,纬度]
		List<Point2D.Double> points = new ArrayList<Point2D.Double>();// 组成围栏的多个点
		String time = null;// 数据上报时间
		Date time1 = null;//数据上报时间Date
		double mileage = 0;// 里程
		double oilmass = 0;// 油量
		double diffOil = 0;// 开始油耗减结束油耗的差值
		// 如果实时位置数据包不为空
		if (location.length() > 0) {
			time = util.formatDateTime(location.getString("datetime"));// 触发围栏开始时间
			SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
			try {
				time1 = f.parse(location.getString("datetime"));
			} catch (JSONException | ParseException e) {
				e.printStackTrace();
			}
			double longitude = location.getDouble("longitude");// 经度
			double latitude = location.getDouble("latitude");// 纬度
			point = new Point2D.Double(longitude, latitude);//当前位置坐标
			
			key += ",start_time";
			value += "','" + time;// 开始时间
			key1 += ",longitude,latitude,alarm_address";
			value1 += ",'" + location.getDouble("longitude") + "'";// 经度
			value1 += ",'" + location.getDouble("latitude") + "'";// 纬度
			value1 += ",'" + location.getString("address") + "'";// 位置
			updateSqlParms += "end_time='" + time + "'";// 区域表触发围栏结束时间
			// 区域表 本次用车时长
			updateSqlParms += ",duration=extract(epoch from to_timestamp('"
					+ location.getString("datetime")
					+ "','YYYYmmDDHH24miSS'))-extract(epoch from to_timestamp(to_char(start_time, 'YYYYmmDDHH24miSS'),'YYYYmmDDHH24miSS'))";
			updateParms += " end_time='" + time+ "'";//报警表 触发围栏结束时间
			// 报警表 本次用车时长
			updateParms += ",time_length=extract(epoch from to_timestamp('"
					+ location.getString("datetime")
					+ "','YYYYmmDDHH24miSS'))-extract(epoch from to_timestamp(to_char(start_time, 'YYYYmmDDHH24miSS'),'YYYYmmDDHH24miSS'))";
		
		}
		// 如果附加实时信息数据包不为空
		if (appendInfo.length() > 0) {
			mileage = appendInfo.getDouble("mileage");// 里程
			oilmass = util.getOilmassBymileage(db, terminalId,mileage);// 油量

			key += ",start_mileage,start_oil";
			value += "'," + mileage;// 触发围栏开始里程
			value += "," + oilmass;// 触发围栏开始油量
			
			
			// 报警表中的上次里程
			double start_mileage = util.getLastOil(db, "cdms_alarmrecord", "start_mileage",
					"car_id='" + carId + "' and time_state='1'");
			double diffMileage = mileage-start_mileage;
			if(diffMileage<0){
				diffMileage = 0;
			}
			updateSqlParms += ",end_mileage="+ mileage ;// 触发围栏结束里程
			updateSqlParms += ",end_oil=" + oilmass;// 触发围栏结束油量
			updateSqlParms += ",mileage="+diffMileage;// 本次用车里程
			updateSqlParms += ",oil="+util.getOilmassBymileage(db, terminalId,diffMileage);//区域表本次用车油量

			updateParms += ",end_mileage="+ mileage;
			updateParms += ",end_oil=" + oilmass;// 触发围栏结束油量
			updateParms += ",mileage="+ diffMileage;
			updateParms += ",oil="+ util.getOilmassBymileage(db, terminalId,diffMileage);//报警表 本次用车油量		
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
			
		if (array.length() > 0) {
			// 循环围栏
			for (int i = 0; i < array.length(); i++) {
				fence = (JSONObject) array.get(i);
				fenceId = fence.getString("id");
				range = fence.getString("range");
				type = fence.getString("type");
				// 如果围栏有设置监控时间，围栏才生效
				if (fence.has("startTime") && fence.has("stopTime")) {
					String[] startTimeArray = (String[]) fence.get("startTime");
					String[] stopTimeArray = (String[]) fence.get("stopTime");
					
					points = getPoints(range);//将围栏范围转换成List<Point2D.Double>坐标点
					boolean flag = checkWithJdkPolygon(point, points);//当前位置是否在围栏内
					logger.info("当前为围栏fenceid="+fenceId+",范围="+range);
					logger.info("将围栏范围转换成List<Point2D.Double>坐标点="+points.toString());
					logger.info("当前车辆carid="+carId+",当前为围栏fenceid="+fenceId+",围栏类型fencetype="+type+",当前位置point="+point.toString()+",是否在围栏内？flag="+flag);
					//进出区域明细表中是否存在未结束的数据
					String inOutAreaDataId = getInOutAreaData(db, carId,fenceId);
					boolean flag1 = Checker.isEmpty(inOutAreaDataId);
					logger.info("进出区域明细表中是否存在未结束的数据?不存在=true：="+flag1);
					if (!flag1) {//进出区域明细表中存在未结束的数据
						// 计算区域表差值
						String updateSql = "update cdms_EnclosureRecord set "
								+ updateSqlParms
								+",fence_id='"
								+ fenceId
								+ "',status='0',member_id='"
								+ driver
								+ "'"
								+ " where id='"
								+ inOutAreaDataId + "'";
						logger.info("进出区域明细表如果存在未结束的数据，则计算差值更新记录：="+updateSql);
						db.execute(updateSql);
						//计算报警表差值
						updateSql = "update cdms_AlarmRecord set "
								+ updateParms
								+ " where car_id='"
								+ carId
								+ "' and time_state='1' and fault_class_alarm_type='32'";
						db.execute(updateSql);
					}
					
					// 判断是否在围栏监控时间段内围栏,围栏会有多个时间段，需要循环判断
					for (int j = 0; j < stopTimeArray.length; j++) {
						startTime = util.formatTime(startTimeArray[j]);
						stopTime = util.formatTime(stopTimeArray[j]);
						logger.info("当前时间="+time1);
						logger.info("围栏开始时间="+startTime);
						logger.info("围栏结束时间="+stopTime);
						logger.info("当前数据上报时间是否在围栏监控时间段中="+(util.compareTime(time1, startTime)&& util.compareTime(stopTime, time1)));
						//当前数据上报时间是否在围栏监控时间段中
						if (util.compareTime(time1, startTime)&& util.compareTime(stopTime, time1)) {
							if ("1".equals(type)) {
								if (flag) {
									logger.info("当前位置在进围栏监控区域内,更新车辆基础信息表和位置运行表的报警状态");
									// 如果当前位置在进围栏监控区域内,更新车辆基础信息表和位置运行表的报警状态
									util.updateAlarmStatus(db, terminalId, carId, "alarmFlag_37",time);		
									if (flag1) {//进出区域明细表中没有未结束的数据
										// insert区域表
										String insertSql = "insert into cdms_EnclosureRecord ("
												+ key
												+",fence_id,status,member_id"
												+ ") values ("+ value 
												+",'" + fenceId+ "','0','"+ driver+ "'"
												+ ")";
										logger.info("进出区域明细表中不存在未结束的数据，新增一条记录="+insertSql);
										count = db.execute(insertSql);
										//监控进围栏的区域外用车报警开始
										String aCode = "alarmFlag_37";
										MapList alarmInfo = util.getAlarmType(db, aCode);
										if (!Checker.isEmpty(alarmInfo)) {
											String alarmTypeId = alarmInfo.getRow(0).get(0);
											String alarmFenLeiId = alarmInfo.getRow(0).get(1);
											String fatname = alarmInfo.getRow(0).get(2);
											alarmContent.put("describe",fatname);
											alarmContent.put("title",fatname);
											
											insertSql = "insert into cdms_AlarmRecord ("
													+ key1
													+ ",frequency,alarm_type,fault_class_alarm_type,alarm_content,"
													+ "operation_status,alarm_time,start_time,start_mileage,start_oil,time_state"
													+ ") values ("
													+ value1 
													+",1,'"+ alarmFenLeiId + "','"+ alarmTypeId + "','"+ fatname + "','0','"
													+ time + "','" + time+ "'," + mileage + ","+ oilmass + ",'1'"
													+ ")";
											logger.info("监控进围栏的区域外用车报警开始="+insertSql);
											db.execute(insertSql);
											// 推送区域外用车报警信息
											//pushAlarm(db, carId,alarmContent);
										}
									}
								}else{//如果当前位置不在进围栏监控区域内
									if (!flag1) {//进出区域明细表中是否存在未结束的数据
										// 结束
										
										String updateSql = "update cdms_EnclosureRecord set status='1' where id='"+ inOutAreaDataId + "'";
										logger.info("进出区域明细表中存在未结束的数据，更新记录="+updateSql);
										count = db.execute(updateSql);
										//监控进围栏的区域外用车报警结束
										updateSql = "update cdms_AlarmRecord set time_state='2' where car_id='"
												+ carId+ "' and time_state='1' and fault_class_alarm_type='32'";
										logger.info("监控进围栏的区域外用车报警结束=="+updateSql);
										db.execute(updateSql);									
									}
								}
							}
							// 监控出围栏
							if ("2".equals(type)) {
								
								if (!flag) {// 如果当前位置不在出围栏监控区域内
									logger.info("当前位置不在出围栏监控区域内,更新车辆基础信息表和位置运行表的报警状态");
									util.updateAlarmStatus(db, terminalId, carId, "alarmFlag_37",time);
									if (flag1) {//进出区域明细表中没有未结束的数据
										// insert
										String insertSql = "insert into cdms_EnclosureRecord ("
												+ key
												+",fence_id,status,member_id"
												+ ") values ("+ value 
												+ ",'" + fenceId+ "','0','"+ driver+ "'"
												+ ")";
										logger.info("进出区域明细表中不存在未结束的数据，新增一条记录="+insertSql);
										count = db.execute(insertSql);
										//监控出围栏的区域外用车报警开始
										String aCode = "alarmFlag_37";
										MapList alarmInfo = util.getAlarmType(
												db, aCode);
										if (!Checker.isEmpty(alarmInfo)) {
											String alarmTypeId = alarmInfo.getRow(0).get(0);
											String alarmFenLeiId = alarmInfo.getRow(0).get(1);
											String fatname = alarmInfo.getRow(0).get(2);
											alarmContent.put("describe",fatname);
											alarmContent.put("title",fatname);
											insertSql = "insert into cdms_AlarmRecord ("
													+ key1
													+",frequency,alarm_type,fault_class_alarm_type,alarm_content,"
													+ "operation_status,alarm_time,start_time,start_mileage,start_oil,time_state"
													+ ") values ("+ value1 
													+",1,'"+ alarmFenLeiId + "','"+ alarmTypeId + "','"+ fatname + "','0','"
													+ time + "','" + time+ "'," + mileage + ","+ oilmass + ",'1'"
													+ ")";
											logger.info("监控出围栏的区域外用车报警开始="+insertSql);
											db.execute(insertSql);
											//推送区域外用车报警信息
											//pushAlarm(db, carId,alarmContent);
										}
									}
								} else {// 如果当前位置在出围栏监控区域内
									if (!flag1) {//进出区域明细表中有未结束的数据
										// 结束
										String updateSql = "update cdms_EnclosureRecord set status='1' where id='"+ inOutAreaDataId + "'";
										logger.info("进出区域明细表中存在未结束的数据，更新记录="+updateSql);
										count = db.execute(updateSql);
										//监控进围栏的区域外用车报警结束
										updateSql = "update cdms_AlarmRecord set time_state='2' where car_id='"
												+ carId+ "' and time_state='1' and fault_class_alarm_type='32'";
										logger.info("监控出围栏的区域外用车报警结束=="+updateSql);
										db.execute(updateSql);
									}
								}
							}
						}else{//不在围栏监控时间内
							if (!flag1) {//有未结束的数据
								// 结束
								logger.info("不在围栏监控时间时进出区域报警结束");
								String updateSql = "update cdms_EnclosureRecord set status='1' where id='"+ inOutAreaDataId + "'";
								count = db.execute(updateSql);
								//监控进围栏的区域外用车报警结束
								updateSql = "update cdms_AlarmRecord set time_state='2' where car_id='"
										+ carId+ "' and time_state='1' and fault_class_alarm_type='32'";
								db.execute(updateSql);
							}
						}
					}
				}else {
					logger.info(fenceId+"围栏没有设置监控时间，围栏无效");
				}
			}
		}
		if (count > 0) {
			rValue = true;

		}
		return rValue;
	}
	
	/**
	 * 查询当前车辆，当前围栏在进出区域表中是否存在状态=0（未结束）的记录,如果存在，返回记录ID
	 * 
	 * @param db
	 * @param carId
	 * @param fenceId
	 * @return
	 */
	public static String getInOutAreaData(DBManager db, String carId, String fenceId) {
		String rValue = "";
		String sql = "select id from cdms_EnclosureRecord where car_id='"
				+ carId + "' and fence_id='" + fenceId + "' and status='0'";
		MapList mapList = db.query(sql);
		if (!Checker.isEmpty(mapList)) {
			rValue = mapList.getRow(0).get(0);
		}
		return rValue;
	}
	
	/**
	 * 判断当前位置是否在电子围栏中
	 * 
	 * @param point
	 *            当前坐标
	 * @param polygon
	 *            电子围栏坐标集合
	 * 
	 * @return true:在电子围栏中 false:不在电子围栏中
	 */
	public static boolean checkWithJdkPolygon(Point2D.Double point,
			List<Point2D.Double> polygon) {
		java.awt.geom.GeneralPath p = new java.awt.geom.GeneralPath();

		Point2D.Double first = polygon.get(0);
		p.moveTo(first.x, first.y);
		polygon.remove(0);
		for (Point2D.Double d : polygon) {
			p.lineTo(d.x, d.y);
		}

		p.lineTo(first.x, first.y);
		p.closePath();
		return p.contains(point);
	}
	
	/**
	 * 获取当前车辆的所属围栏id,围栏范围，围栏类型，监控时间段
	 * 
	 * @param db
	 * @param carId
	 * @return
	 */
	public static JSONArray getFence(DBManager db, String carId) {
		JSONArray rValue = new JSONArray();
		JSONObject jObject = null;
		String sql = "select fence.id,fence.electronic_fence_range,fence.electronic_fence_type from cdms_ElectronicFence fence,cdms_FenceCars cars where fence.id=cars.fence_id and cars.car_Id='"
				+ carId + "' group by fence.id";
		MapList mapList = db.query(sql);
		if (!Checker.isEmpty(mapList)) {
			for (int i = 0; i < mapList.size(); i++) {
				String fenceId = mapList.getRow(i).get(0);
				String electronicFenceRange = mapList.getRow(i).get(1);
				String electronicFenceType = mapList.getRow(i).get(2);
				String[] startTime = null;
				String[] stopTime = null;
				if (!Checker.isEmpty(fenceId)) {
					String queryTimesSql = "select start_time,end_time from cdms_FenceTimeSlot where fence_id='"
							+ fenceId + "'";
					MapList timeMapList = db.query(queryTimesSql);
					if (!Checker.isEmpty(timeMapList)) {
						startTime = new String[timeMapList.size()];
						stopTime = new String[timeMapList.size()];
						for (int j = 0; j < timeMapList.size(); j++) {
							startTime[j] = timeMapList.getRow(j).get(0);
							stopTime[j] = timeMapList.getRow(j).get(1);
						}
					} else {
						logger.info("此围栏没有监控时间，不生效");
					}
				}
				jObject = new JSONObject();
				jObject.put("id", fenceId);
				jObject.put("range", electronicFenceRange);
				jObject.put("type", electronicFenceType);
				jObject.put("startTime", startTime);
				jObject.put("stopTime", stopTime);
				rValue.put(jObject);
			}
		}
		return rValue;
	}
	
	/**
	 * 将围栏范围转换成List<Point2D.Double>坐标点
	 * @param points
	 * @param range
	 */
	public static List<Point2D.Double> getPoints(String range){
		List<Point2D.Double> points = new ArrayList<Point2D.Double>();
		range = range.replaceAll("\\[", "");
		range = range.replaceAll("\\]", "");
		range = range.replaceAll("\"", "");
		String[] points1 = range.split(",");

		String lonStr = "";
		String latStr = "";
		for (int j = 0; j < points1.length; j++) {
			if (j % 2 == 0) {
				lonStr += "," + points1[j];
			} else {
				latStr += "," + points1[j];
			}
		}
		String[] lon1 = lonStr.split(",");
		String[] lat1 = latStr.split(",");
		for (int i1 = 0; i1 < lon1.length; i1++) {
			if (!Checker.isEmpty(lon1[i1])
					&& !Checker.isEmpty(lat1[i1])) {
				double lon = Double.parseDouble(lon1[i1]);// 经度
				double lat = Double.parseDouble(lat1[i1]);// 纬度
				points.add(new Point2D.Double(lon, lat));
			}

		}
		return points;
	}
	@Test
	public void test(){
		Point2D.Double point = new Point2D.Double(108.9261135808153, 34.32005845032463);
		List<Point2D.Double> points = new ArrayList<Point2D.Double>();
		String range = "[[\"108.926349\",\"34.355209\"],[\"108.975444\",\"34.35691\"],[\"108.972182\",\"34.178019\"],[\"108.763442\",\"34.319914\"]]";
//		String range = "[[\"108.946732\",\"34.318258\"],[\"108.954027\",\"34.318204\"],[\"108.954027\",\"34.317566\"],[\"108.946796\",\"34.31746\"],[\"108.946796\",\"34.31746\"]]";
		points = getPoints(range);//将围栏范围转换成List<Point2D.Double>坐标点
		boolean flag = checkWithJdkPolygon(point, points);//当前位置是否在围栏内
		logger.info("是否在围栏内？flag="+flag);
	}
}
