package com.am.nw.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * 终端升级进度更新API
 * @author 王成阳
 * 2018-3-20
 */
public class ApiTerminalUpgradeUpdate implements IWebApiService{
	
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		
		System.err.println("********************进入终端升级进度更新API");
	
		//接参
		String serial_number = request.getParameter("serial_number");						//终端命令流水号
		String total_package = request.getParameter("total_package");					    //总包数
		String current_package = request.getParameter("current_package");  				    //当前包数
		//声明DB
		DBManager db = new DBManager();
		//声明返回json
		JSONObject jsonObject = new JSONObject();
		
		
		String select = "select id from cdms_terminalcommand where serial_number = '"+serial_number+"'";
		System.err.println("********************ApiTerminalUpgradeUpdate.select =" + select);
		
		MapList mapList = db.query(select);
		
		//如果查询结果为空，直接返回失败
		if (Checker.isEmpty(mapList)) {
			jsonObject = jsonObject.put("code", "0");
			jsonObject = jsonObject.put("msg", "Fail");
			System.err.println("********************ApiTerminalUpgradeUpdate.return =" + jsonObject.toString());
			return jsonObject.toString();
		}else{
			
			//根据终端命令流水号设置总包数和当前包数
			String update = "update cdms_terminalcommand set total_package='"+total_package+"' ,current_package = '"+current_package+"' where serial_number = '"+serial_number+"'";
			System.err.println("********************ApiTerminalUpgradeUpdate.update" + update);
			db.execute(update);
			
			jsonObject = jsonObject.put("code", "1");
			jsonObject = jsonObject.put("msg", "Success");
			System.err.println("********************ApiHeartBeat.return =" + jsonObject.toString());
			return jsonObject.toString();
		}

	}

}
