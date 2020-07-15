package com.am.frame.webapi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

public interface IExecuteResBefore 
{
	public JSONObject execute(HttpServletRequest request
			,HttpServletResponse response
			,ActionParame param
			,JSONObject json);
}
