package com.am.nw.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * 终端心跳API
 * @author 王成阳
 * 2018-1-30
 */
public class ApiHeartBeat implements IWebApiService{

	Logger logger = LoggerFactory.getLogger(getClass());
	
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		
		logger.info("********************进入终端心跳ApiHeartBeat");
		
		//接参
		String terminalId = request.getParameter("terminalId");
		//声明DB
		DBManager db = new DBManager();
		//声明返回json
		JSONObject jsonObject = new JSONObject();
		
		//根据终端id查询
		String select = "select ID from CDMS_VEHICLEBASICINFORMATION where DEVICE_SN_NUMBER = '"+terminalId+"'";
		logger.info("********************ApiHeartBeat.select =" + select);
		
		MapList mapList = db.query(select);
		
		//如果查询结果为空，直接返回失败
		if (Checker.isEmpty(mapList)) {
			jsonObject = jsonObject.put("code", "0");
			jsonObject = jsonObject.put("msg", "Fail");
			logger.info("********************ApiHeartBeat.return =" + jsonObject.toString());
			return jsonObject.toString();
		}else{
			
			//根据终端id设置车辆基础信息表.车辆运行状态=1
			String update = "update CDMS_VEHICLEBASICINFORMATION set CAR_RUN_STATE = '1',LAST_HEARTBEAT_TIME = now() where DEVICE_SN_NUMBER = '"+terminalId+"'";
			logger.info("********************ApiHeartBeat.update" + update);
			db.execute(update);
			
			jsonObject = jsonObject.put("code", "1");
			jsonObject = jsonObject.put("msg", "Success");
			logger.info("********************ApiHeartBeat.return =" + jsonObject.toString());
			return jsonObject.toString();
		}
	}
}
