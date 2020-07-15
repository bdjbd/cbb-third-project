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
 * 请求注册
 * @author 王成阳
 * 2018-1-25 
 */
public class ApiRequestRegister implements IWebApiService{
	
	private Logger logger=LoggerFactory.getLogger(getClass());

	public String execute(HttpServletRequest request, HttpServletResponse response) {

		logger.info("********************进入终端注册ApiRequestRegister");
		
		//接参
		String terminalId = request.getParameter("terminalId");
		String TcpServerID = request.getParameter("TcpServerID");
		String channelID = request.getParameter("channelID");
		
		logger.info("********************ApiRequestRegister.terminalId" + terminalId);
		
		//声明DB
		DBManager db = new DBManager();
		//声明返回json
		JSONObject jsonObject = new JSONObject();
		
		//根据终端id查询车辆信息
		String select = "select id from  CDMS_VEHICLEBASICINFORMATION where DEVICE_SN_NUMBER = '"+terminalId+"' ";
		logger.info("********************ApiRequestRegister.select:" + select);
		
		//承接查询结果
		MapList mapList = db.query(select);
		
		//如果查询结果不为空
		if (!Checker.isEmpty(mapList)) {
			
			//根据终端id把服务端id,终端id修改进车辆基本信息表中,并修改车辆运行状态为运行中1
			String update = "update  CDMS_VEHICLEBASICINFORMATION set CAR_RUN_STATE = '1' ,CHANNELID = '"+channelID+"',TCPSERVERID = '"+TcpServerID+"' where DEVICE_SN_NUMBER = '"+terminalId+"'";
			logger.info("********************ApiRequestRegister.update:" + update);
			db.execute(update);
			
			jsonObject = jsonObject.put("code", "1");
			jsonObject = jsonObject.put("msg", "Success");
			logger.info("********************ApiRequestRegister.return:" + jsonObject.toString());
			
			return jsonObject.toString();
		
		}else{
			
			jsonObject = jsonObject.put("code", "2");
			jsonObject = jsonObject.put("msg", "Fail,Terminal does not exist");
			logger.info("********************ApiRequestRegister.return:" + jsonObject.toString());
			
			return jsonObject.toString();
		}
	}
}
