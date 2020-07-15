package com.am.frame.webapi.db;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;

import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * @author Mike
 * @create 2014年10月28日
 * @version 
 * 说明:<br />
 * Update SQL接口 ，URL编码解码
 */
public class UpdateSQLIWebApi implements IWebApiService {

	
	
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		String result = "{\"DATA\":[]}";
		String sql=request.getParameter("content");
		
		DB db=null;
		
		if(!Checker.isEmpty(sql)){
			try {
				db=DBFactory.newDB();
				//将移动端提交的数据进行转码
				sql=URLDecoder.decode(sql,"UTF-8");
				
				LoggerFactory.getLogger(this.getClass().toString()).info("UpdateSQLIWebApiSQL:"+sql);
				
				//执行SQL
				int count=db.execute(sql);
				
				result = "{\"COUNT\":\"" + count + "\"}";
				
			} catch (Exception e) {
				
				e.printStackTrace();
				result = "{'errcode':40007,'errmsg':'"+e.getMessage()+"'}";
			}finally{
				if(db!=null){
					try {
						db.close();
					} catch (JDBCException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return result;
	}

}
