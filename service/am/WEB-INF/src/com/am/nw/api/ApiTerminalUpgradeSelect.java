package com.am.nw.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.am.frame.webapi.db.DBManager;
import com.p2p.service.IWebApiService;

/**
 * 终端升级进度查询API
 * @author 王成阳
 * 2018-3-20
 */
public class ApiTerminalUpgradeSelect implements IWebApiService{
	
	
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		
		System.err.println("********************进入终端升级进度查询API");
		
		//接参
		String serial_number = request.getParameter("serial_number");			//终端命令流水号
		//声明DB
		DBManager db = new DBManager();
		//声明返回json
		JSONObject jsonObject = new JSONObject();
		
		String select = "select cmd_content,total_package,current_package from cdms_terminalcommand where serial_number = '"+serial_number+"'";
		
	   //调用queryToJSON查询sql返回JsonArray
	   JSONArray jsonArray = db.queryToJSON(select);
	   jsonObject = jsonObject.put("DATA", jsonArray);
	   
	   System.err.println("********************ApiTerminalUpgradeSelect.return" + jsonObject.toString());
	   
		return jsonObject.toString();
	}

}
