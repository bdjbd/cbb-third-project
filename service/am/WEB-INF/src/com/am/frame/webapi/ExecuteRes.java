package com.am.frame.webapi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.service.ExecuteResService;
import com.p2p.service.IWebApiService;

/** 
 * @author  wz  
 * @descriptions 请求执行资源方法
 * @date 创建时间：2016年3月24日 上午11:24:09 
 * @version 1.0   
 */
public class ExecuteRes implements IWebApiService 
{
	
	private static final Logger log = LoggerFactory.getLogger(ExecuteRes.class);
	
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) 
	{
		
		String action = request.getParameter("rcode");
		
		String params = request.getParameter("params");
		
		String pageSize = request.getParameter("pageSize");
		
		String pageNumber = request.getParameter("pageNumber");
		
		String requestMethod = request.getMethod();
		
		JSONObject reObj = null;
		
		ExecuteResService executeResService = new ExecuteResService();
		
		try{
			log.debug("com.am.frame.webapi.ExecuteRes：资源执行");
			reObj = executeResService.appExecuteRes(action, params, pageSize, pageNumber, requestMethod, request, response);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return reObj.toString();
	}
	
	
	
}
