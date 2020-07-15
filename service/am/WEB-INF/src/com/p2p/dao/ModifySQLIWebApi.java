package com.p2p.dao;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fastunit.jdbc.DBFactory;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * @author Mike
 * @create 2014年10月27日
 * @version 
 * 说明:<br />
 */
public class ModifySQLIWebApi implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		String result = "{\"DATA\":[]}";
		String sql=request.getParameter("content");
		
		if(!Checker.isEmpty(sql)){
			try {
				//将移动端提交的数据进行转码
				sql=URLDecoder.decode(sql,"UTF-8");
				
				//执行Edit
				int count=DBFactory.getDB().execute(sql);
				
				result = "{\"COUNT\":\"" + count + "\"}";
				
			} catch (Exception e) {
				e.printStackTrace();
				result = "{'errcode':40007,'errmsg':'"+e.getMessage()+"'}";
			}
		}
		
		return result;
	}

}
