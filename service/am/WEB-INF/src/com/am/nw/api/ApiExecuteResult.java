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
 * 执行结果
 * @author 王成阳
 * 2018-1-25
 */
public class ApiExecuteResult implements IWebApiService{

	Logger logger = LoggerFactory.getLogger(getClass());
	
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		
		//修改基础信息表
		String vehiclebasicinformation = "";
		//修改命令表
		String terminalcommand = "";
		
		logger.info("********************进入执行结果ApiExecuteResult");
		
		//接参
		String tcpServerID = request.getParameter("tcpServerID");
		logger.info("********************ApiExecuteResult.tcpServerID:" + tcpServerID);
		
		String channelID = request.getParameter("channelID");
		logger.info("********************ApiExecuteResult.channelID:" + channelID);
		
		String commandID = request.getParameter("commandID");
		logger.info("********************ApiExecuteResult.commandID:" + commandID);
		
		String state = request.getParameter("state");
		logger.info("********************ApiExecuteResult.state:" + state);
		
		String serialNumber = request.getParameter("serialNumber");
		logger.info("********************ApiExecuteResult.serialNumber:" + serialNumber);
		
		//声明DB
		DBManager db = new DBManager();
		//声明返回json
		JSONObject jsonObject = new JSONObject();
		
		//根据参数查询数据,若数据为空则返回失败
		String select = "select car.id "
					  + "from CDMS_TERMINALCOMMAND command , CDMS_VEHICLEBASICINFORMATION  car "				
					  + "where command.car_id = car.id "
					  + "and car.TCPSERVERID ='"+tcpServerID+"' and car.CHANNELID ='"+channelID+"' and command.COMMANDID ='"+commandID+"' and command.SERIAL_NUMBER ='"+serialNumber+"'";
		
		logger.info("********************ApiExecuteResult.select=" + select );
		JSONArray selectArray = db.queryToJSON(select);
		logger.info("********************ApiExecuteResult.jsonArray=" + selectArray);
		
		if (selectArray.length() == 0) {
				
			jsonObject = jsonObject.put("code", "0");
			jsonObject = jsonObject.put("msg", "Fail");
			logger.info("********************ApiExecuteResult.return" + jsonObject.toString());
			
			return jsonObject.toString();
			
		}else{
			
			JSONObject idObject = selectArray.getJSONObject(0);
			String id = idObject.getString("ID");
			
			//设置终端命令状态为成功
			if (state.equals("3")) {
				
				terminalcommand = "update CDMS_TERMINALCOMMAND  "
							  + "set state = '3' "
							  + "where commandID = '"+commandID+"' and  car_id in (select id from CDMS_VEHICLEBASICINFORMATION where channelID ='"+channelID+"' and TCPSERVERID ='"+tcpServerID+"')";
				
				//修改基础信息表远程升级结果字段为3(成功)
				if (commandID.equals("10008")) {
					
					vehiclebasicinformation = "update cdms_vehiclebasicinformation set upgrade_status = '3' where id = '"+id+"'";
					System.err.println("**********远程升级成功 SQL = " + vehiclebasicinformation );
				
				//修改基础信息表参数设置结果字段为3(成功)	
				}else if (commandID.equals("10006")) {
					
					vehiclebasicinformation = "update cdms_vehiclebasicinformation set parameter_setting_state = '3' where id = '"+id+"'";
				}
				
				logger.info("********************ApiExecuteResult.terminalcommand:" + terminalcommand);
				logger.info("********************ApiExecuteResult.vehiclebasicinformation:" + vehiclebasicinformation);
			
			//设置终端命令状态为成功	
			}else if(state.equals("4")){
				
				//设置命令表
				terminalcommand = "update CDMS_TERMINALCOMMAND  "
							  + "set state = '4' "
							  + "where commandID = '"+commandID+"' and  car_id in (select id from CDMS_VEHICLEBASICINFORMATION where channelID ='"+channelID+"'and TCPSERVERID ='"+tcpServerID+"')";
				
				
				//修改基础信息表
				if (commandID.equals("10008")) {
					
					vehiclebasicinformation = "update cdms_vehiclebasicinformation set upgrade_status = '4' where id = '"+id+"'";
					System.err.println("**********远程升级失败SQL  = " + vehiclebasicinformation );
				
				}else if (commandID.equals("10006")) {
					
					vehiclebasicinformation = "update cdms_vehiclebasicinformation set parameter_setting_state = '4' where id = '"+id+"'";
					System.err.println(vehiclebasicinformation);
				
				}
				logger.info("********************ApiExecuteResult.update:" + terminalcommand);
			
			//设置终端命令状态为取消	
			}else if (state.equals("5")) {
				
				//设置命令表
				terminalcommand = "update CDMS_TERMINALCOMMAND  "
							  + "set state = '5' "
							  + "where commandID = '"+commandID+"' and  car_id in (select id from CDMS_VEHICLEBASICINFORMATION where channelID ='"+channelID+"'and TCPSERVERID ='"+tcpServerID+"')";
				
				//修改基础信息表
				if (commandID.equals("10008")) {
					
					vehiclebasicinformation = "update cdms_vehiclebasicinformation set upgrade_status = '5' where id = '"+id+"'";
					System.err.println("**********远程升级取消SQL  = " + vehiclebasicinformation );
				
				}else if (commandID.equals("10006")) {
					
					vehiclebasicinformation = "update cdms_vehiclebasicinformation set parameter_setting_state = '5' where id = '"+id+"'";
					System.err.println(vehiclebasicinformation);
				
				}
				logger.info("********************ApiExecuteResult.update:" + terminalcommand);
			}
			
			db.execute(terminalcommand);
			db.execute(vehiclebasicinformation);
			
			jsonObject = jsonObject.put("code", "1");
			jsonObject = jsonObject.put("msg", "Success");
			logger.info("********************ApiExecuteResult.return" + jsonObject.toString());
			
			return jsonObject.toString();
			}
		}
	
	}
	

