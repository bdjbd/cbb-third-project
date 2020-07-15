package com.am.frame.webapi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;


//后置接口
public interface IExecuteResAfter 
{
	public JSONObject execute(HttpServletRequest request
			,HttpServletResponse response
			,ActionParame param
			,JSONObject json);
}
