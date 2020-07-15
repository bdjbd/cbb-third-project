package com.am.nw.api;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.am.nw.api.location.UpdateCarAlarm;
import com.am.nw.api.location.UpdateCarBasicInfo;
import com.am.nw.api.location.UpdateCarDayUtilizeSituation;
import com.am.nw.api.location.UpdateCarIdleSituation;
import com.am.nw.api.location.UpdateCarInOutArea;
import com.am.nw.api.location.UpdateCarNotWorkTime;
import com.am.nw.api.location.UpdateCarPositionRun;
import com.am.nw.api.location.UpdateCarRunSituation;
import com.am.nw.api.location.UpdateMemberLocation;
import com.am.nw.api.location.util.LocationApiUtil;
import com.am.utils.GetAddressByLatLng;
import com.am.utils.GpsCorrect;
import com.p2p.service.IWebApiService;

/**
 * 终端定位数据上报API
 * 
 * @author guorenjie 2018-02-05
 */
public class ApiReportingTerminalLocationData implements IWebApiService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private LocationApiUtil util = new LocationApiUtil();

	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject rValue = new JSONObject();// 返回值
		DBManager db = new DBManager();// DB
		
		logger.info("正在执行数据上报API！com.am.nw.api.ApiReportingTerminalLocationData.execute()");
		String isloc = "0";// 是否定位 0=未定位 1=定位
		
		
		String data = request.getParameter("terminalLocationParams");// data
		
		logger.info("[ApiReportingTerminalLocationData.data] =" +data);
		JSONObject json = new JSONObject(data);
		String terminalId = json.getString("terminalId");// 终端ID
		JSONObject car = util.getCarInfoByTerminalId(db, terminalId);
		if(car==null){
			rValue.put("code", "-1");
			rValue.put("msg", "终端ID不存在");
			logger.info("终端ID不存在");
		}else{
			
			if (car.has("carId")) {
				String carId = car.getString("carId");
				String driver = util.getDriver(db, carId);
				JSONObject alarmFlag = json.getJSONObject("alarmFlag");// 报警标志

				JSONObject status = json.getJSONObject("status");// 状态

				// 是否定位 0=未定位，1=定位
				if (util.isNotLocate(status)) {
					isloc = "1";
				}
				JSONObject location = json.getJSONObject("location");// 实时位置
				
				//2018-11-9 wcy add
				//获得终端上报的原始经纬度和上报时间
				JSONObject rawLocation = json.getJSONObject("location");
				//2018-11-9 wcy add end
				
				replaceData(location, db, terminalId);// 使用当前时间替换了数据包中的时间
				gpsToAmap(location);// gps坐标转换高德
				addAdress(location);// 位置反解
				JSONObject appendInfo = json.getJSONObject("appendInfo");// 附加实时信息

				JSONObject ObdRealTimeDataRpt = json
						.getJSONObject("ObdRealTimeDataRpt");// OBD实时数据

				JSONObject ObdOverDataRpt = json.getJSONObject("ObdOverDataRpt");// OBD本次行驶结束数据

				JSONObject TerminalVehicleRpt = json
						.getJSONObject("TerminalVehicleRpt");// 本次行驶结束数据

				// 更新车辆基础信息表
				boolean flag1 = UpdateCarBasicInfo.updateCarBasicInfo(isloc,util,db,
						terminalId, alarmFlag, status, location, appendInfo,
						ObdRealTimeDataRpt, ObdOverDataRpt, rawLocation);
				logger.info("更新车辆基础信息表：" + flag1);
	
				// 更新车辆位置运行表
				boolean flag2 = UpdateCarPositionRun.updateCarPositionRun(carId,isloc,util,db,
						terminalId, alarmFlag, status, location, appendInfo,
						ObdRealTimeDataRpt, rawLocation);
				logger.info("更新车辆位置运行表：" + flag2);
	
				// 更新车辆运行情况表
				boolean flag4 = UpdateCarRunSituation.updateCarRunSituation(carId,isloc,util,db,
						terminalId, status, location, appendInfo);
				logger.info("更新车辆运行情况表：" + flag4);
	
				// 更新车辆闲置情况表
				boolean flag5 = UpdateCarIdleSituation.updateCarIdleSituation(carId,util,db,
						terminalId, status, location);
				logger.info("更新车辆闲置情况表：" + flag5);
	
				// 更新报警表
				boolean flag8 = UpdateCarAlarm
						.updateCarAlarm(driver,carId,util,db, terminalId, alarmFlag, status,
								location, appendInfo, TerminalVehicleRpt);
				logger.info("更新报警表：" + flag8);
	
				// 更新非工作时间用车明细表
				// 2018-04-03，应增加利用百公里油耗计算油耗的方法
				boolean flag6 = UpdateCarNotWorkTime.updateCarNotWorkTime(driver,carId,util,db,
						terminalId, status, location, appendInfo);
				logger.info("更新非工作时间用车明细表：" + flag6);
	
				// 更新进出区域表
				// 2018-04-03，应增加利用百公里油耗计算油耗的方法
				boolean flag7 = UpdateCarInOutArea.updateCarInOutArea(driver,util,db,
						terminalId, alarmFlag, status, location, appendInfo);
				logger.info("更新进出区域表：" + flag7);
	
				// 更新车辆天利用情况表
				// 2018-04-03，增加利用百公里油耗计算油耗的方法
				boolean flag9 = UpdateCarDayUtilizeSituation
						.updateCarDayUtilizeSituation(carId,util,db, terminalId, alarmFlag,
								status, location, appendInfo);
				logger.info("更新车辆天利用情况表：" + flag9);
	
				// 如果车辆绑定了人员，更新人员位置
				boolean flag10 = UpdateMemberLocation.updateMemberLnglat(driver,carId,util,db,
						location, status);
				logger.info("更新车辆人员位置：" + flag10);
	
				// 更新操作有一个返回true,则数据上报成功,否则上报失败
				if (flag1 || flag2 || flag4 || flag5 || flag6 || flag7 || flag8
						|| flag9) {
					rValue.put("code", "1");
					rValue.put("msg", "数据上报成功");
					logger.info("数据上报成功！");
				} else {
					rValue.put("code", "0");
					rValue.put("msg", "数据上报失败");
					logger.info("数据上报失败！");
				}
			} else {
				rValue.put("code", "-1");
				rValue.put("msg", "终端ID不存在");
				logger.info("终端ID不存在");
			}
		}
		return rValue.toString();
	}
	
	/**
	 * 替换实时位置的时间为当前时间
	 * @param jsonObject
	 */
	public void replaceData(JSONObject location,DBManager db,String sn_number){
		Date date = new Date();
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
		location.put("ReceiveTime", location.getString("datetime"));
		location.put("datetime", f.format(date));
	}
	/**
	 * 实时位置数据增加地址
	 * @param jsonObject
	 */
	public void addAdress(JSONObject location){
		String lat = location.getDouble("latitude")+"";
		String lng = location.getDouble("longitude")+"";
		GetAddressByLatLng gAddress = new GetAddressByLatLng();
		String address = gAddress.getAddress(lat, lng);
		location.put("address",address);
	}
	/**
	 * gps坐标转高德坐标
	 * @param jsonObject
	 */
	public void gpsToAmap(JSONObject location){
		double lat = location.getDouble("latitude");
		double lng = location.getDouble("longitude");
		location.put("ys_latitude",lat);
		location.put("ys_longitude",lng);
		double []corr = new double[2];
		corr[0] = lat;
		corr[1] = lng;
		GpsCorrect.transform(lat, lng, corr);
		lat = corr[0];
		lng = corr[1];
		location.put("latitude",lat);
		location.put("longitude",lng);
	}

}
