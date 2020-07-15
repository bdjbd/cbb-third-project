package com.am.sdk.db.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IRestAction {
	
	String execute(HttpServletRequest request,HttpServletResponse response);

}
