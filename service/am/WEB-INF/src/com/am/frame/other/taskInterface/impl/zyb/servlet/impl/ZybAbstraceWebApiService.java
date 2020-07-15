package com.am.frame.other.taskInterface.impl.zyb.servlet.impl;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.other.taskInterface.OtherInterfaceTaskManager;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年6月28日
 * @version 
 * 说明:<br />
 * 智游宝 系统回调接口
 */
public abstract class ZybAbstraceWebApiService implements IWebApiService {

	protected Logger logger =LoggerFactory.getLogger(getClass());
	
	
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		//相应数据
		String responseData="";
		
		DB db=null;
		try{
			
			db=DBFactory.newDB();
			
			//1，记录日志到系统
			//2，验证签名的正确性 
			//2，调用处理业务逻辑
			
			//1，请求数据 
			String requestData="";
			
			Enumeration<String> en = request.getParameterNames();  
			while(en.hasMoreElements()){  
			    String el = en.nextElement().toString();  
			    requestData+=el+"="+request.getParameter(el)+"&";
			}  
			
			OtherInterfaceTaskManager otherManager=new OtherInterfaceTaskManager();
			
			responseData=processBusess(db, request, response);
			
			otherManager.createPassive(db, requestData, responseData,requestData );
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		
		return responseData;
	}

	/**
	 * 处理智游宝业务数据
	 * @param db  DB 
	 * @param request  HttpServletRequest
	 * @param response HttpServletResponse
	 * @return 处理结果 
	 */
	protected abstract  String  processBusess(DB db,HttpServletRequest request,
			HttpServletResponse response)throws Exception;
	
}
