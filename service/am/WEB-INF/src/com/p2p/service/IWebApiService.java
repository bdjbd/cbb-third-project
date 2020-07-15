package com.p2p.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IWebApiService 
{
	public String execute(HttpServletRequest request,HttpServletResponse response);
}