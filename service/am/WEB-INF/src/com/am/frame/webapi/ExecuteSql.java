package com.am.frame.webapi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.webapi.db.DBManager;
import com.p2p.service.IWebApiService;

/** 
 * @author  wz  
 * @descriptions 
 * @date 创建时间：2016年3月24日 上午11:24:55 
 * @version 1.0   
 */
public class ExecuteSql implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		String param = request.getParameter("param");
		
		JSONObject obj = null;
		
		try {
			if(param!=null)
			{
				obj = new DBManager().executeJson(param);
			}else{
				
				obj = new JSONObject();
				obj.put("CODE", "4999");
				obj.put("MSG", "执行操作失败，请检查参数");
				obj.put("DATA","[]");
			
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return obj.toString();
	}

}
