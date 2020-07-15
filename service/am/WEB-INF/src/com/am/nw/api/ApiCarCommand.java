package com.am.nw.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.p2p.service.IWebApiService;


/**
 * 检查是否有新命令API
 * @author 王成阳
 * 2018-1-25
 */
public class ApiCarCommand implements IWebApiService{
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		
		logger.info("********************进入检查是否有新命令ApiCarCommand");
		
		//接参
		String tcpServerID = request.getParameter("tcpServerID");
		//声明DB
		DBManager db = new DBManager();
		//声明返回json
		 JSONObject resultJson = new JSONObject();
		
		//查询所有TcpServerID=,及命令状态=1 or 命令状态=4的数据
		 String select = "select car.TcpServerID as TcpServerID,car.ChannelID as channelID,car.DEVICE_SN_NUMBER as DEVICE_SN_NUMBER,command.COMMANDID as commandID,command.Cmd_Type as cmdType,command.Cmd_Content as cmdContent ,command.serial_number as serial_number "
				  + "from cdms_VehicleBasicInformation car , CDMS_TERMINALCOMMAND command "
				  + "where car.id = command.car_id and TcpServerID = '"+tcpServerID+"' and command.State = '1'"; 
		 
		 logger.info("********************ApiCarCommand.select:" + select);
		
		 //调用queryToJSON查询sql返回JsonArray
		 JSONArray jsonArray = db.queryToJSON(select);
		 resultJson = resultJson.put("DATA", jsonArray);
		 
		 logger.info("输出jsonarray" + jsonArray.toString());
		 
		 if (jsonArray.length()>0) {

			 logger.info("********************ApiCarCommand.return:" + resultJson.toString());
			 
			//设置所有TcpServerID=,及命令状态=1 or 命令状态=4的数据命令状态=2;
			String update = "update CDMS_TERMINALCOMMAND  "
							+ "set STATE = '2' "
							+ "where State = '1' and  car_id in (select id from CDMS_VEHICLEBASICINFORMATION where TcpServerID ='"+tcpServerID+"')";
			
			logger.info("********************ApiCarCommand.update:" + update);
				
			db.execute(update);
		 }
		
		return resultJson.toString();
	}

}
