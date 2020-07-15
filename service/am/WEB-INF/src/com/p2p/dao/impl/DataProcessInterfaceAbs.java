package com.p2p.dao.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author: Mike 2014年7月15日 说明：
 * 
 **/
public abstract class DataProcessInterfaceAbs {
	
	
	
	public String handle(String action,HttpServletRequest request, HttpServletResponse response) throws Exception{
		return null;
	}
}
