package com.am.nw.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.p2p.service.IWebApiService;

/**
 * 终端定位数据上报API测试
 * @author 王成阳
 * 2018-2-8
 */
public class ApiTest20004 implements IWebApiService{
	
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		
		System.err.println("********************进入终端定位数据上报API测试");
		
		String terminalLocationParams = request.getParameter("terminalLocationParams");
		
		System.err.println("********************ApiTest20004.terminalLocationParams：" + terminalLocationParams);
		
		JSONObject terminalLocationObject = new JSONObject(terminalLocationParams);
		
		String terminalId = terminalLocationObject.getString("terminalId"); //终端ID
		System.err.println("终端id：" + terminalId);
		
		JSONObject alarmFlag = terminalLocationObject.getJSONObject("alarmFlag");//报警标志
		System.err.println("报警标志：" + alarmFlag);
		
		JSONObject status = terminalLocationObject.getJSONObject("status");//状态
		System.err.println("状态：" + status);
		
		JSONObject location = terminalLocationObject.getJSONObject("location");//实时位置
		System.err.println("实时位置：" + location);
		
		JSONObject appendInfo = terminalLocationObject.getJSONObject("appendInfo");//附加实时信息
		System.err.println("附加实时信息：" + appendInfo);
		
		JSONObject ObdRealTimeDataRpt = terminalLocationObject.getJSONObject("ObdRealTimeDataRpt");//OBD实时数据
		System.err.println("OBD实时数据：" + ObdRealTimeDataRpt);
		
		JSONObject ObdOverDataRpt = terminalLocationObject.getJSONObject("ObdOverDataRpt");//OBD本次行驶结束数据
		System.err.println("OBD本次行驶结束数据：" + ObdOverDataRpt);
		
		JSONObject TerminalVehicleRpt = terminalLocationObject.getJSONObject("TerminalVehicleRpt");//本次行驶结束数据
		System.err.println("本次行驶结束数据：" + TerminalVehicleRpt);
		
		return terminalLocationObject.toString();
	}

}
